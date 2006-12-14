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
package org.smartfrog.services.deployapi.alpineclient.model;

import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.services.deployapi.transport.wsrf.WSNConstants;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.notifications.muws.MuwsEventReceiver;
import org.smartfrog.services.deployapi.notifications.muws.ReceivedEvent;
import org.ggf.cddlm.generated.api.CddlmConstants;
import nu.xom.Element;

/**
 * This is a subscription to a callback.
 * It is a WSRF endpoint with its own operations
 * created 03-Oct-2006 11:27:57
 */

public class CallbackSubscription extends WsrfSession {

    /**
     * optional cached muws event receiver
     */
    private MuwsEventReceiver receiver;

    public CallbackSubscription(AlpineEPR endpoint, boolean validating, TransmitQueue queue) {
        super(endpoint, validating, queue);
    }

    /**
     * extract the subscription from a message
     * @throws AlpineRuntimeException if parsing fails
     * @param payload payload to get the addresss from
     * @param parent parent session
     */
    public CallbackSubscription(WsrfSession parent,Element payload) {
        super(parent,null);
        Element address = payload.getFirstChildElement(WSNConstants.SUBSCRIPTION_REFERENCE,
                Constants.WSRF_WSNT_NAMESPACE);
        if (address == null) {
            throw new AlpineRuntimeException("Missing element " + WSNConstants.SUBSCRIPTION_REFERENCE
                    + "in " + payload);
        }
        //get the WSA address back
        AlpineEPR epr = new AlpineEPR(address, CddlmConstants.WS_ADDRESSING_NAMESPACE);
        epr.validate();
        bind(epr);
    }

    public Transmission beginUnsubscribe() {
        return beginDestroy();
    }

    public void endUnsubscribe(Transmission tx) {
        endDestroy(tx);
        if(receiver!=null) {
            receiver.destroy();
        }
    }

    public static void unsubscribe(CallbackSubscription subscription) {
        if (subscription != null) {
            //unsubscribe.
            subscription.destroy();
        }
    }


    public MuwsEventReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(MuwsEventReceiver receiver) {
        this.receiver = receiver;
    }

    /**
     * Wait for an incoming event. If there already is one in the buffer, return that
     * @param milliseconds time to wait.
     * @return the event or null for timeout
     */
    public ReceivedEvent waitForEvent(long milliseconds) {
         return getReceiver().waitForEvent(milliseconds);
    }


    public String toString() {
        return super.toString()+" with returns to "+getReceiver().getURL();
    }

    /**
     * For use in the toString method; return the session type
     *
     * @return the type of this session, e,g "SOAP", "WSRF"
     */
    protected String sessionType() {
        return "Subscription";
    }
}
