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

import org.smartfrog.services.deployapi.transport.endpoints.alpine.WsrfHandler;
import org.smartfrog.services.deployapi.transport.wsrf.NotificationSubscription;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.faults.ServerException;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import nu.xom.Element;

/**
 * Endpoint to handle all subscriptions.
 * created 27-Sep-2006 13:18:32
 */

public class SubscriptionService extends WsrfHandler {

    private Log log= LogFactory.getLog(SubscriptionService.class);


    private void remove(NotificationSubscription sub) {
        getServerInstance().getSubscriptionStore().remove(sub.getId());
    }

    /*
    Handle <Pause> -> Pause operation could not be performed on the Subscription
    */
    @Override
    public void process(MessageContext messageContext, EndpointContext endpointContext) {
        //hand off WSRF processing to the superclass
        super.process(messageContext, endpointContext);
        if (messageContext.isProcessed()) {
            //exit immediately if processing took place.
            return;
        }
        Element payload = messageContext.getRequest().getBody().getFirstChildElement();
        if (payload == null) {
            throw new ServerException("Empty SOAP message");
        }

        String requestName = payload.getLocalName();
        //TODO: handle WSNT pause/resume operations

    }

    /**
     * Returns a string representation of the object. I
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "SubscriptionService";
    }


    /**
     * Handle a destroy operation by unsubscribing
     *
     * @param messageContext
     * @param endpointContext
     */
    protected boolean destroy(MessageContext messageContext,
                           EndpointContext endpointContext) {
        NotificationSubscription sub = getSubscription(messageContext);
        if(sub!=null) {
            log.info("destorying subscription "+sub.getId());
            sub.unsubscribe();
            remove(sub);
        } else {
            log.error("No subscription to destroy for "+messageContext.getRequest().getAddressDetails().getTo());
        }
        return true;
    }

    /**
     * Get the subscription
     * @param messageContext
     * @return
     */
    private NotificationSubscription getSubscription(MessageContext messageContext) {
        MessageDocument request = messageContext.getRequest();
        AlpineEPR to = getTo(request);
        String id = NotificationSubscription.extractSubscriptionIDFromAddress(to);
        NotificationSubscription sub = getServerInstance().getSubscriptionStore().lookup(id);
        if (sub == null) {
            FaultRaiser.raiseBadArgumentFault("No subscription at "+to);
        }
        return sub;
    }

    /**
     * Return the resource source for this message, which is whatever
     * is subscribed to here
     *
     * @param message
     * @return the source of resources. Return null for no match in that context
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *
     */
    public WSRPResourceSource retrieveResourceSource(MessageContext message) {
        NotificationSubscription sub = getSubscription(message);
        return sub;
    }

}
