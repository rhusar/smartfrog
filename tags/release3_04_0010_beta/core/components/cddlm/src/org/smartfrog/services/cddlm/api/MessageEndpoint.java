/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cddlm.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPEnvelope;
import java.rmi.RemoteException;

/**
 * created Aug 31, 2004 1:30:59 PM this endpoint is message based
 */

public class MessageEndpoint extends SmartFrogHostedEndpoint {

    /**
     * log for everything other than operations
     */
    private static final Log log = LogFactory.getLog(DeploymentEndpoint.class);

    /**
     * log just for operational data
     */
    private static final Log operations = LogFactory.getLog(DeploymentEndpoint.class.getName() +
            ".OPERATIONS");


    public void serverStatus(SOAPEnvelope req, SOAPEnvelope resp)
            throws RemoteException {
        try {
            operations.info("entering serverStatus");
            ServerStatusProcessor serverStatusProcessor = new ServerStatusProcessor(
                    this);

        } finally {
            operations.info("exiting serverStatus");
        }
    }

    public Document serverStatus(Document body) throws RemoteException {
        return body;
    }


}
