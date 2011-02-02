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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api;

import org.smartfrog.services.deployapi.alpineclient.model.CallbackSubscription;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.alpineclient.model.WsrfSession;
import org.smartfrog.services.deployapi.alpineclient.model.PortalSession;
import org.smartfrog.services.deployapi.notifications.muws.MuwsEventReceiver;
import org.smartfrog.services.deployapi.notifications.muws.NotifyServer;
import org.smartfrog.services.deployapi.notifications.muws.NotifyServerImpl;
import org.smartfrog.services.deployapi.notifications.muws.ReceivedEvent;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;
import java.net.URL;
import java.net.MalformedURLException;

import junit.framework.AssertionFailedError;

/**
 * created 29-Sep-2006 13:30:18
 */

public abstract class SubscribingTestBase extends StandardTestBase {
    private CallbackSubscription subscription;
    protected static final String HTTP_EXAMPLE_ORG = "http://invalid.example.org";

    private Reference ref;
    private static final String NOTIFICATIONS = "notifications";

    protected SubscribingTestBase(String name) {
        super(name);
    }

    /**
     * Set up our reference
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        ref = Reference.fromString("PARENT:"+ NOTIFICATIONS);
    }


    /**
     * Look up the notify server and fail if there is none.
     * @return the notify server
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    protected NotifyServer lookupNotifyServer() throws SmartFrogResolutionException, RemoteException {
        Prim self = getHostedTestSuite();
        Prim prim = self.sfResolve(ref, (Prim) null, false);
        assertTrue("Configuration error: No notify server at "+ref.toString(),prim!=null);
        assertTrue("Expected reference of type NotifyServer, not "+prim,
                prim instanceof NotifyServer);
        return (NotifyServer) prim;
    }

    /**
     * Create a muws receiver from our local receiver
     * @return a new event receiver
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    protected MuwsEventReceiver createSubscriptionReceiver() throws SmartFrogResolutionException, RemoteException {
        NotifyServer server = lookupNotifyServer();
        NotifyServerImpl serverImpl=(NotifyServerImpl) server;
        MuwsEventReceiver receiver = serverImpl.createReceiver();
        return receiver;
    }

    /**
    * subscribe to the portal using a specified callback URL
    * @param topic topic to subscribe to
    */
    protected CallbackSubscription subscribeToPortal(QName topic)
            throws SmartFrogResolutionException, RemoteException {
        assertNull("subscription in use", subscription);
        MuwsEventReceiver receiver=createSubscriptionReceiver();
        PortalSession session = getPortal();
        subscription = session.subscribe(topic, receiver.getURL(), true, null);
        subscription.setReceiver(receiver);
        subscription.setTimeout(getSubscribeWaitTimeout());
        patchSubscription(session,subscription);
        getLog().info("Subscribing to "+subscription.toString());
        return subscription;
    }



    /**
     * unsubscribe from the current endpoint
     */
    protected void unsubscribe() {
        try {
            CallbackSubscription.unsubscribe(subscription);
        } finally {
            subscription = null;
        }
    }

    /**
     * Fix problems wherein the remote endpoints get their hostname wrong
     * @param sub subscription to fix up.
     */
    protected void patchSubscription(WsrfSession session,CallbackSubscription sub) {
        AlpineEPR sessionEPR = session.getAddress().getTo();
        URL sessionURL=sessionEPR.createAddressURL();
        URL subURL = sub.getEndpoint().createAddressURL();
        if("localhost".equals(subURL.getHost()) && subURL.getPath().indexOf("/muse/services/")>=0) {
            //bad hostname

            try {
                URL newSub;
                newSub = new URL(subURL.getProtocol(),
                        sessionURL.getHost(),    // <- this is the patched host
                        sessionURL.getPort(),
                        subURL.getFile());       // <- the file is the path plus query
                log.warn("Patching the path of the subscription to "+newSub);
                sub.bind(new AlpineEPR(newSub));
            } catch (MalformedURLException e) {
                throw new AlpineRuntimeException("Could not convert the URL",e);
            }
        }

    }


    public CallbackSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(CallbackSubscription subscription) {
        this.subscription = subscription;
    }

    /**
     * Wait for a subscription event
     * @param reason
     * @return the received event
     * @throws junit.framework.AssertionFailedError on timeout
     */
    public ReceivedEvent waitForSubscription(String reason)
            throws AssertionFailedError {
        assertNotNull("No subscription",subscription);
        ReceivedEvent event = subscription.waitForEvent(getSubscribeWaitTimeout());
        assertNotNull("Subscription timed out waiting for "+reason,event);

        return event;
    }

    /**
     * unsubscribe, if needed
     */
    protected void tearDown() throws Exception {
        try {
            unsubscribe();
        } finally {
            super.tearDown();
        }
    }

    /**
     * Subscribe at the portal to a system creation event
     */
    protected void subscribeToSystemCreationEvent() throws SmartFrogResolutionException, RemoteException {
        subscribeToPortal(Constants.PORTAL_CREATED_EVENT);
    }

    protected SystemSession subscribeAndCreate() throws SmartFrogResolutionException, RemoteException {
        subscribeToPortal(Constants.PORTAL_CREATED_EVENT);
        return createSystem(null);
    }

    /**
     * Create a system to which we are subscribed.
     * This sets the system and subscription fields, which will be cleaned
     * up on teardown
     * @return the active system session.
     */
    protected SystemSession createSubscribedSystem() throws SmartFrogResolutionException,
            RemoteException {
        assertNull("subscription in use", subscription);
        assertHosted();
        SystemSession system = createSystem(null);
        setSystem(system);
        subscription = system.subscribeToLifecycleEvents(createSubscriptionReceiver());
        subscription.setTimeout(getSubscribeWaitTimeout());
        //fix up bad hostnames.
        patchSubscription(system, subscription);
        return system;
    }

    /**
     * Wait for the system to enter a the specified state. If the application is already in that
     * state, we return immediately, otherwise the method blocks unil the
     * @param expected the state we want to reach
     * @return the event that was used to signal an event change
     * @throws junit.framework.AssertionFailedError on timeout
     */
    protected ReceivedEvent waitForState(LifecycleStateEnum expected)
            throws AssertionFailedError {
        if (expected.equals(getSystem().getLifecycleState())) {
            return null;
        }
        ReceivedEvent event = waitForSubscription(expected.getXmlName());
        //get the state from the event
        LifecycleStateEnum state = event.getState();

        assertEquals(expected, state);
        return event;
    }
}
