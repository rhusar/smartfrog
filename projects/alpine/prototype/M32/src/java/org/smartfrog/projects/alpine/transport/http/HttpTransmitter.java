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
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.core.Context;
import org.smartfrog.projects.alpine.core.ContextConstants;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.faults.SoapException;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.http.HttpBinder;
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
 *
 * this code passes its proxy settings down from the
 * created 23-Mar-2006 16:48:29
 */

public class HttpTransmitter {

    private static final Log log = LogFactory.getLog(HttpTransmitter.class);
    private Transmission tx;

    private HttpClient httpclient;
    private ProxySettings proxySettings;


    private AddressDetails wsa;
    private final MessageContext context;
    private final MessageDocument request;
    public static final String ERROR_DURING_PREPARATION = "Failure while creating the output request." +
            "No communication with the server took place";
    public static final int BLOCKSIZE = 4096;
    private HttpConnectionParams connectionParams;

    public HttpTransmitter(Transmission tx) {
        this.tx = tx;
        context = tx.getContext();
        request = context.getRequest();
        wsa = request.getAddressDetails();
        wsa.checkToIsValid();
        wsa.addressMessage(request);
        //create a client
        httpclient = new HttpClient();
        propagateNetworkSettings(tx.getContext());
    }


    /**
     * Extract proxy settings from the system and pass them down to httpclient
     * @param settings settings context, including timeout. {@link HttpConnectionParams#SO_TIMEOUT}
     *
     */
    private void propagateNetworkSettings(Context settings) {
        //proxy settings
        proxySettings = new ProxySettings();
        proxySettings.bindToSystemSettings();
        proxySettings.configureClient(httpclient);
        //socket options
        connectionParams = new HttpConnectionParams();
        Object o=settings.get(HttpConnectionParams.SO_TIMEOUT);
        if(o!=null) {
            int timeout=(Integer) o;
            httpclient.setTimeout(timeout);
            connectionParams.setSoTimeout(timeout);
        }
    }


    /**
     * Save a request to a file. This file should be deleted afterwards
     *
     * @return the file containing the request
     * @throws IOException if the temp file could not be saved
     */
    protected File saveRequestToFile() throws IOException {
        File outputFile = null;
        OutputStream outToFile = null;
        try {
            outputFile = File.createTempFile("alpine", ".xml");
            outToFile = new BufferedOutputStream(new FileOutputStream(outputFile));
            Serializer serializer = new Serializer(outToFile);
            serializer.write(request);
            serializer.flush();
            outToFile.flush();
            outToFile.close();
        } catch(IOException ioe) {
            //clean up and rethrow on failure
            if (outToFile != null) {
                try {
                    outToFile.close();
                } catch (IOException e) {
                    //swallow
                }
                //delete the file
                outputFile.delete();
            }
            //rethrow
            throw ioe;
        }
        return outputFile;
    }

    /**
     * Save the current request to a file and then transmit it.
     * @throws AlpineRuntimeException in case of trouble.
     */
    public void transmit() {
        File outputFile = null;
        try {
            outputFile = saveRequestToFile();
        } catch (IOException ioe) {
            //error before we even connect to the server
            throw new HttpTransportFault(ERROR_DURING_PREPARATION, ioe);
        }
        try {
            transmitPayload(outputFile);
        } finally {
            if (outputFile != null) {
                outputFile.delete();
            }
        }
    }

    /**
     * Transmit a file containing an XML message.
     * the file is not deleted afterwards, so the transport can be used to push up existing messages 
     * @param outputFile file to upload.
     * @throws AlpineRuntimeException in case of trouble.
     */
    public void transmitPayload(File outputFile) {
        String destination = wsa.getDestination();
        log.debug("Posting to " + destination);
        PostMethod method = new ProgressingPostMethod(destination);
        //REVISIT. Its not clear that this method should stay around.
        //method.setFollowRedirects(true);
        method.addRequestHeader("SOAPAction", "");
        method.addRequestHeader("User-Agent", HttpConstants.ALPINE_VERSION);
        //fill in the details
        //1. get the message into a byte array
        //2. add it
        //3. TODO: add files?
        RequestEntity re = null;
        String contentType = HttpConstants.CONTENT_TYPE_TEXT_XML;
        String ctxContentType = (String) tx.getContext().get(ContextConstants.ATTR_SOAP_CONTENT_TYPE);
        if (ctxContentType != null) {
            contentType = ctxContentType;
        }
        re = new ProgressiveFileUploadRequestEntity(tx, request, outputFile,
                contentType,
                tx.getUploadFeedback(), BLOCKSIZE);
        method.setRequestEntity(re);
        InputStream responseStream = null;
        try {
            int statusCode = httpclient.executeMethod(method);
            final boolean requestFailed = statusCode != HttpStatus.SC_OK;
            boolean responseIsXml;
            //get the content type and drop anything following a semicolon
            //this can be null on an empty response
            contentType = getResponseContentType(method);
            if(contentType!=null) {
                contentType = HttpBinder.extractBaseContentType(contentType);
                responseIsXml = HttpBinder.isValidSoapContentType(contentType);
            } else {
                responseIsXml=false;
            }

            if (requestFailed &&
                    (!responseIsXml || statusCode != HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                //TODO: treat 500+text/xml response specially, as it is probably a SOAPFault
                log.error("Method failed: " + method.getStatusLine());
                throw new HttpTransportFault(destination, method);
            }

            //response is 200, but is it HTML?
            if (!responseIsXml) {
                HttpTransportFault fault = new HttpTransportFault(destination, method,
                        "Wrong content type: expected "
                                + HttpConstants.CONTENT_TYPE_TEXT_XML
                                + " or "
                                + HttpConstants.CONTENT_TYPE_SOAP_XML
                                + " but got ["
                                + contentType
                                + ']');
                throw fault;
            }
            //extract the response
            responseStream = new CachingInputStream(method.getResponseBodyAsStream(),"utf8");
            //parse it
            SoapMessageParser parser = tx.getContext().createParser();
            MessageDocument response;
            if (!requestFailed) {
                response = parser.parseStream(responseStream);
                //set our response
                tx.getContext().setResponse(response);
            } else {
                // if is a fault, turn it into an exception.
                try {
                    response = parser.parseStream(responseStream);
                    //set our response
                    tx.getContext().setResponse(response);
                } catch (Exception e) {
                    //this is here to catch XML Responses that cannot be
                    //parsed, and to avoid the underlying problem 'remote server error'
                    //from being lost.
                    String text=responseStream.toString();
                    SoapException ex = new SoapException(
                            "The remote endpoint returned an error,\n"
                                    + "but the response could not be parsed\n"
                                    + "and turned into a SOAPFault.\n"
                                    +"XML:"+ text+
                            "\nParse Error:"+e.toString(),
                            e, null);
                    ex.addAddressDetails(request);
                    throw ex;
                }
                SoapException ex = new SoapException(response);
                throw ex;
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
