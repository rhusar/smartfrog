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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.notification;

import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.SubscribingTestBase;

/**
 * The client can subscribe via the portal for system creation events.
 *
 * created 04-May-2006 13:46:55
 */

public class Api_15_subscribe_system_creation_Test extends SubscribingTestBase {

    public Api_15_subscribe_system_creation_Test(String name) {
        super(name);
    }

    public void testSubscribe() throws Exception {
        subscribeToSystemCreationEvent();
    }

}
