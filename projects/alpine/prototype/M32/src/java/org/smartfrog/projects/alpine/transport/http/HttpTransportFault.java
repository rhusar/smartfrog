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

import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * created 23-Mar-2006 17:09:35
 */

public class HttpTransportFault extends AlpineRuntimeException {

    private int status;

    private String response;

    private HttpMethod method;

    private static Log log = LogFactory.getLog(HttpTransmitter.class);

    public HttpTransportFault(String destination,HttpMethod method) {
        super("Error when communicating with "+destination);
        this.method=method;
        status=method.getStatusCode();
        try {
            response = method.getResponseBodyAsString();
        } catch (IOException e) {
            log.warn("Could not read response of fault",e);
        }
    }

    public HttpTransportFault(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpTransportFault(Throwable cause) {
        super(cause);
    }

    public HttpTransportFault(String message) {
        super(message);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }
}
