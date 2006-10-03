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

import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;
import org.smartfrog.services.deployapi.alpineclient.model.CallbackSubscription;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.system.Constants;

import javax.xml.namespace.QName;

/**
 * created 29-Sep-2006 13:30:18
 */

public abstract class SubscribingTestBase extends StandardTestBase {
    private CallbackSubscription subscription;
    protected static final String HTTP_EXAMPLE_ORG = "http://example.org";

    public SubscribingTestBase(String name) {
        super(name);
    }

    /**
     * subscribe to the portal using a dummy URL
     * @param topic topic to subscribe to
     */
    protected CallbackSubscription subscribeToPortal(QName topic) {
        return subscribeToPortal(topic, getCallbackURL());
    }

    /**
     * Get the default URL for subscriptions
     * @return {@link #HTTP_EXAMPLE_ORG}
     */
    protected String getCallbackURL() {
        return HTTP_EXAMPLE_ORG;
    }

    /**
     * subscribe to the portal using a specified callback URL
     * @param topic topic to subscribe to
     */
    protected CallbackSubscription subscribeToPortal(QName topic, String callback) {
        assertNull("subscription in use", subscription);
        subscription = getPortal().subscribe(topic, callback, false, null);
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
    protected void subscribeToSystemCreationEvent() {
        subscribeToPortal(Constants.PORTAL_CREATED_EVENT);
    }

    protected SystemSession subscribeAndCreate(String callback) {
        subscribeToPortal(Constants.PORTAL_CREATED_EVENT, callback);
        return createSystem(null);
    }

    /**
     * Create a system to which we are subscribed.
     * This sets the system and subscription fields, which will be cleaned
     * up on teardown
     * @param callback callback URL
     * @return the active system session.
     */
    protected SystemSession createSubscribedSystem(String callback) {
        assertNull("subscription in use", subscription);
        SystemSession system = createSystem(null);
        setSystem(system);
        subscription = system.subscribeToLifecycleEvents(callback, false);
        return system;
    }

}
