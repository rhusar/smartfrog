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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.lifecycle;

import org.smartfrog.services.deployapi.alpineclient.model.CallbackSubscription;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.notifications.muws.ReceivedEvent;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.SubscribingTestBase;
import org.ggf.cddlm.generated.api.CddlmConstants;

/**
 * created 04-May-2006 13:46:55
 */

public class Api_33_multiple_lifecycle_listeners_Test extends SubscribingTestBase {

    public Api_33_multiple_lifecycle_listeners_Test(String name) {
        super(name);
    }

    public void testSubscribeToMultipleListeners() throws Exception {
        SystemSession session = createSubscribedSystem();
        log.info("session 1 listening at "+getSubscription().getReceiver());
        CallbackSubscription s2 = session.subscribeToLifecycleEvents(
                createSubscriptionReceiver());
        log.info("session 2 listening at " + s2.getReceiver());
        runSystem(CddlmConstants.INTEROP_API_TEST_DOC_1_VALID_DESCRIPTOR);
        waitForSubscription("run for first listener of two");
        ReceivedEvent event = s2.waitForEvent(0);
        assertNotNull("Subscription timed out for second listener", event);
    }

}
