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
import org.smartfrog.services.deployapi.notifications.muws.MuwsEventReceiver;
import org.smartfrog.services.deployapi.notifications.muws.NotifyServer;
import org.smartfrog.services.deployapi.notifications.muws.NotifyServerImpl;
import org.smartfrog.services.deployapi.notifications.muws.ReceivedEvent;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;

/**
 * created 29-Sep-2006 13:30:18
 */

public abstract class SubscribingTestBase extends StandardTestBase {
    private CallbackSubscription subscription;
    protected static final String HTTP_EXAMPLE_ORG = "http://invalid.example.org";

    private Reference ref;
    private static final String NOTIFICATIONS = "notifications";
    private static final int SUBSCRIBE_WAIT_TIMEOUT = 5000;
    private static final String PROPERTY_WAIT_TIMEOUT = "wait.timeout";

    public SubscribingTestBase(String name) {
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
        Prim prim = self.sfResolve(ref, (Prim) null, true);
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
        subscription = getPortal().subscribe(topic, receiver.getURL(), true, null);
        subscription.setReceiver(receiver);
        return subscription;
    }

    /**
     * unsubscribe from the current endpoint
     */
    protected void unsubscribe() {
        CallbackSubscription.unsubscribe(subscription);
        subscription=null;
    }

    public CallbackSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(CallbackSubscription subscription) {
        this.subscription = subscription;
    }

    public ReceivedEvent waitForSubscription()  {
        assertNotNull("No subscription",subscription);
        MuwsEventReceiver receiver = subscription.getReceiver();
        ReceivedEvent event = receiver.waitForEvent(getSubscribeWaitTimeout());
        assertNotNull("Subscription timed out",event);
        return event;
    }

    /**
     * Get the junit parameter for waiting
     * @return
     */
    protected int getSubscribeWaitTimeout() {
        return getJunitParameter(PROPERTY_WAIT_TIMEOUT,SUBSCRIBE_WAIT_TIMEOUT,false);
    }

    /**
     * unsubscribe, if needed
     */
    protected void tearDown() throws Exception {
        unsubscribe();
        super.tearDown();
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
        MuwsEventReceiver receiver = createSubscriptionReceiver();
        subscription = system.subscribeToLifecycleEvents(receiver.getURL(), false);
        subscription.setReceiver(receiver);
        return system;
    }
}
