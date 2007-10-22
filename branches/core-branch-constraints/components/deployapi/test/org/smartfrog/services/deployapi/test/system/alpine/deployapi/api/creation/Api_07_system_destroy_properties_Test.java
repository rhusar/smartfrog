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

import nu.xom.Element;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

/**
 * created 13-Apr-2006 13:51:02
 * Create a system , then destroy it immediately.
 */

public class Api_07_system_destroy_properties_Test extends StandardTestBase {

    public Api_07_system_destroy_properties_Test(String name) {
        super(name);
    }


    public void testCreateDestroySystem() throws Exception {
        SystemSession system = getPortal().create(null);
        system.getResourcePropertySingle(CddlmConstants.PROPERTY_MUWS_RESOURCEID);
        system.getResourcePropertySingle(CddlmConstants.PROPERTY_SYSTEM_CREATED_TIME);
        system.destroy();
        Element result = null;
        try {
            //exponential backoff as we wait for a system to cease to exists
            for (int i = 1; i <= 128; i *= 2) {
                result = system.getResourcePropertySingle(CddlmConstants.PROPERTY_MUWS_RESOURCEID);
                sleep(i);
            }
            fail("Expected failure, got a system with resid " + result.getValue());
        } catch (AlpineRuntimeException e) {
            //expected error of some form or other
        }
    }

}
