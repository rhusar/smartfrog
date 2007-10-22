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
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;

/**
 After subscribing to the portal for system creation events,
 a &lt;wsrf:Destroy&gt; operation must unsubscribe them.
 After unsubscribing, creating a new system must not result in an
 event being sent to the (now unsubscribed) endpoint.
 */

public class Api_16_unsubscribe_Test extends SubscribingTestBase {

    public Api_16_unsubscribe_Test(String name) {
        super(name);
    }

    public void testUnsubscribe() throws Exception {
        subscribeToSystemCreationEvent();
        try {
            unsubscribe();
        } catch (AlpineRuntimeException e) {
            getLog().error(e);
            getLog().error(e.toString());
            throw e;
        }
        createSystem(null);
    }


}
