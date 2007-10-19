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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.creation;

import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

/**
 * created 13-Apr-2006 13:51:02
 * Create a system , then destroy it immediately.
 */

public class Api_08_system_destroy_operations_Test extends StandardTestBase {
    private static final int SECONDS = 16;

    public Api_08_system_destroy_operations_Test(String name) {
        super(name);
    }

    public void testPingSystem() throws Exception {
        SystemSession system = createSystem(null);
        system.ping();
    }


    public void testPingDestroyedSystem() throws Exception {
        SystemSession system = getPortal().create(null);
        system.destroy();
        try {
            for (int i = 1; i <= SECONDS; i++) {
                system.ping();
                sleep(i);
            }
            fail("Expected a system to be destroyed, but it was still present after "+SECONDS+" seconds");
        } catch (AlpineRuntimeException e) {
            //expected error of some form or other
        }
    }

}
