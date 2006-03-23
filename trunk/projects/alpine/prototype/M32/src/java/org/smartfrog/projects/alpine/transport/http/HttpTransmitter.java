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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;

import java.io.IOException;

/**
 * Implement Http using commons-httpclient
 * created 23-Mar-2006 16:48:29
 */

public class HttpTransmitter {

    private static Log log= LogFactory.getLog(HttpTransmitter.class);
    private Transmission tx;

    private HttpClient httpclient;


    private AddressDetails wsa;
    private final MessageContext context;
    private final MessageDocument request;

    public HttpTransmitter(Transmission tx) {
        this.tx = tx;
        context = tx.getContext();
        request = context.getRequest();
        wsa=request.getAddressDetails();
        wsa.checkToIsValid();
        httpclient = new HttpClient();
    }



    public void transmit() {
        String destination = wsa.getDestination();
        log.debug("Posting to "+destination);
        HttpMethod method= new ProgressingPostMethod(destination);

        //TODO: fill in the details
        //1. get the message into a byte array
        //2. add it
        //3. add files?


        method.setRequestHeader("content-type","text/xml");
        //method.setRequestBody()
        try {
            int statusCode = httpclient.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                //TODO: treat 500+text/xml response specially, as it is probably a SOAPFault
                log.error("Method failed: " + method.getStatusLine());
                throw new HttpTransportFault(destination,method);
            }

            // Read the response body.
            byte[] responseBody = method.getResponseBody();
        } catch(IOException ioe) {
            throw new HttpTransportFault(destination, ioe);
        } finally {
            method.releaseConnection();
        }
    }
}
