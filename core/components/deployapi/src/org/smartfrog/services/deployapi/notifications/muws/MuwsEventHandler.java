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
package org.smartfrog.services.deployapi.notifications.muws;

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.WSNotifyHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * created 10-Oct-2006 14:58:54
 */

public class MuwsEventHandler extends WSNotifyHandler {


    private static final Log log= LogFactory.getLog(MuwsEventHandler.class);

    /**
     * Handle a notification event. The default implementation logs it and returns true
     *
     * @param messageContext incoming message
     * @return true if the message was processed
     */
    protected boolean notificationReceived(MessageContext messageContext) {
        SoapElement request = getRequest(messageContext);
        SoapElement event = (SoapElement) request.getFirstChildElement(MUWS_MANAGEMENT_EVENT,
                                                    Constants.MUWS_P1_NAMESPACE);
        if (event == null) {
            throw new AlpineRuntimeException("No muws-p1:" + MUWS_MANAGEMENT_EVENT + " found in the request");
        }
        return muwsEventReceived(messageContext,event);
    }


    /**
     * Handle a muwsEvent; return true if the message was processed
     * @param messageContext incomding message
     * @param event received event
     * @return true iff the message was processed.
     */
    protected boolean muwsEventReceived(MessageContext messageContext, SoapElement event) {
        //look up the ID in the server.
        NotifyServerImpl server = getNotifyServer();
        AlpineEPR to = messageContext.getRequest().getAddressDetails().getTo();
        String eventID = to.lookupQuery(NotifyServer.EVENT);
        MuwsEventReceiver receiver = server.lookup(eventID);
        if(receiver==null) {
            log.warn("received an event for a nonexistent endpoint "+ to);
            throw new AlpineRuntimeException("Unknown notification Endpoint " + to);
        }
        //forward to the receiver
        receiver.muwsEventReceived(messageContext, event);
        //return as processed
        return true;
    }

    private NotifyServerImpl getNotifyServer() {
        return NotifyServerImpl.getSingleton();
    }

    /**
     * Returns a string representation of the object. I
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "MuwsEventHandler";
    }

}
