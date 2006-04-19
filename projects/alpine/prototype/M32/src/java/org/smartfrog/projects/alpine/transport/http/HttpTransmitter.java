/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.projects.alpine.transport.http;

import nu.xom.ParsingException;
import nu.xom.Serializer;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.faults.SoapException;
import org.smartfrog.projects.alpine.http.HttpConstants;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.SoapMessageParser;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.xml.sax.SAXException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implement Http using commons-httpclient
 * created 23-Mar-2006 16:48:29
 */

public class HttpTransmitter {

    private static Log log = LogFactory.getLog(HttpTransmitter.class);
    private Transmission tx;

    private HttpClient httpclient;
    private ProxySettings proxySettings;


    private AddressDetails wsa;
    private final MessageContext context;
    private final MessageDocument request;
    public static final String ERROR_DURING_PREPARATION = "Failure while creating the output request." +
            "No communication with the server took place";
    public static final int BLOCKSIZE = 4096;

    public HttpTransmitter(Transmission tx) {
        this.tx = tx;
        context = tx.getContext();
        request = context.getRequest();
        wsa = request.getAddressDetails();
        wsa.checkToIsValid();
        wsa.addressMessage(request);
        //create a client
        httpclient = new HttpClient();
        //proxy settings
        proxySettings = new ProxySettings();
        proxySettings.bindToSystemSettings();
        proxySettings.configureClient(httpclient);
    }


    public void transmit() {
        String destination = wsa.getDestination();
        log.debug("Posting to " + destination);
        PostMethod method = new ProgressingPostMethod(destination);

        //fill in the details
        //1. get the message into a byte array
        //2. add it
        //3. TODO: add files?
        File outputFile = null;
        OutputStream outToFile = null;
        RequestEntity re = null;
        try {
            try {
                try {
                    outputFile = File.createTempFile("alpine", ".post");
                    outToFile = new BufferedOutputStream(new FileOutputStream(outputFile));
                    Serializer serializer = new Serializer(outToFile);
                    serializer.write(request);
                    serializer.flush();
                    outToFile.flush();
                    re = new ProgressiveFileUploadRequestEntity(tx, request, outputFile,
                            HttpConstants.CONTENT_TYPE_TEXT_XML,
                            tx.getUploadFeedback(), BLOCKSIZE);
                    method.setRequestEntity(re);
                } finally {
                    if (outToFile != null) {
                        outToFile.close();
                    }
                }
            } catch (IOException ioe) {
                //error before we even connect to the server
                throw new HttpTransportFault(ERROR_DURING_PREPARATION, ioe);

            }
            InputStream responseStream = null;
            try {
                int statusCode = httpclient.executeMethod(method);
                SoapMessageParser parser = tx.getContext().createParser();
                //get the content type and drop anything following a semicolon
                String contentType = getResponseContentType(method);
                final int semicolon = contentType.indexOf(';');
                if (semicolon >= 0) {
                    contentType = contentType.substring(0, semicolon);
                }
                boolean responseIsXml = HttpConstants.CONTENT_TYPE_TEXT_XML
                        .equals(contentType)
                        || HttpConstants.CONTENT_TYPE_SOAP_XML.equals(contentType);

                final boolean requestFailed = statusCode != HttpStatus.SC_OK;
                if (requestFailed &&
                        (!responseIsXml || statusCode != HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                    //TODO: treat 500+text/xml response specially, as it is probably a SOAPFault
                    log.error("Method failed: " + method.getStatusLine());
                    throw new HttpTransportFault(destination, method);
                }

                if (!responseIsXml) {
                    HttpTransportFault fault = new HttpTransportFault(destination, method,
                            "Wrong content type: expected "
                                    + HttpConstants.CONTENT_TYPE_TEXT_XML
                                    + "or "
                                    + HttpConstants.CONTENT_TYPE_SOAP_XML
                                    + " but got ["
                                    + contentType
                                    + ']');
                    throw fault;
                }
                //extract the response
                responseStream = method.getResponseBodyAsStream();
                //parse it
                MessageDocument response = parser.parseStream(responseStream);
                //set our response
                tx.getContext().setResponse(response);
                // if is a fault, turn it into an exception.
                if (requestFailed) {
                    throw new SoapException(response);
                }


            } catch (IOException ioe) {
                throw new HttpTransportFault(destination, ioe);
            } catch (SAXException e) {
                throw new HttpTransportFault(destination, e);
            } catch (ParsingException e) {
                throw new HttpTransportFault(destination, e);
            } finally {
                if (responseStream != null) {
                    try {
                        responseStream.close();
                    } catch (IOException e) {
                        //ignore this.
                    }
                }
                method.releaseConnection();
            }
        } finally {
            if (outputFile != null) {
                outputFile.delete();
            }
        }
    }

    public void outputResponse(MessageContext messageContext,
                               HttpServletResponse response)
            throws IOException {
        MessageDocument message = messageContext.getResponse();
        int responseCode = message.isFault() ?
                HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        response.setStatus(responseCode);
        response.setContentType("UTF-8");
        //PrintWriter writer = response.getWriter();
        ServletOutputStream out = response.getOutputStream();
        Serializer serializer = new Serializer(out);
        serializer.write(message);
        serializer.flush();
        out.flush();
    }

    private String getResponseContentType(PostMethod method) {
        Header content = method.getResponseHeader("Content-Type");
        if (content == null) {
            return null;
        } else {
            return content.getValue();
        }
    }


}
