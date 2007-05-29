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
package org.smartfrog.services.deployapi.transport.endpoints.alpine;

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.wsrf.WSNConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * this is an endpoint for events.
 * created 10-Oct-2006 14:12:11
 */

public class WSNotifyHandler extends WsrfHandler implements WSNConstants {

    private Log log= LogFactory.getLog(WSNotifyHandler.class);

    /**
     * Message handler
     *
     * @param messageContext
     * @param endpointContext
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public void process(MessageContext messageContext, EndpointContext endpointContext) {
        super.process(messageContext, endpointContext);
        if(messageContext.isProcessed()) {
            return;
        }

        if (WSNT_NOTIFY.equals(getRequestName(messageContext))) {
            verifyNamespace(getRequest(messageContext),
                    Constants.WSRF_WSNT_NAMESPACE);

            boolean processed=notificationReceived(messageContext);
            messageContext.setProcessed(processed);
        }
    }

    /**
     * Handle a notification event. The default implementation logs it and returns true
     * @param messageContext incoming message
     * @return true if the message was processed
     *
     */
    protected boolean notificationReceived(MessageContext messageContext) {
        log.info("received WSN-Message");
        return true;
    }

    /**
     * Returns a string representation of the object. I
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "WSNotifyHandler";
    }

}
