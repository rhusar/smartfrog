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
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_MUWS_RESOURCEID;
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_PORTAL_ACTIVE_SYSTEMS;
import static org.ggf.cddlm.generated.api.CddlmConstants.WS_ADDRESSING_NAMESPACE;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

/**
 * created 13-Apr-2006 13:51:02
 * Create a system , then destroy it immediately.
 */

public class Api_09_system_active_systems_Test extends StandardTestBase {

    public Api_09_system_active_systems_Test(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        createSystem(null);
    }

    public void testActiveSystemFound() throws Exception {
        String resID = getSystem().getResourcePropertySingle(PROPERTY_MUWS_RESOURCEID).getValue();
        assertNotNull(resID);
        assertTrue(resID.length() > 0);
        SoapElement activeSystems = (SoapElement) getPortal().getResourcePropertySingle(PROPERTY_PORTAL_ACTIVE_SYSTEMS);
        boolean found = false;
        for (Element e : activeSystems.elements()) {
            assertEquals(CddlmConstants.ELEMENT_NAME_SYSTEM, e.getLocalName());
            assertEquals(CddlmConstants.CDL_API_TYPES_NAMESPACE, e.getNamespaceURI());
            AlpineEPR epr = new AlpineEPR(e, WS_ADDRESSING_NAMESPACE);
            epr.validate();
            SystemSession system = new SystemSession(getPortal(), epr);
            String newResID = system.getResourcePropertySingle(PROPERTY_MUWS_RESOURCEID).getValue();
            assertNotNull(newResID);
            assertTrue(newResID.length() > 0);
            if (newResID.equals(resID)) {
                found = true;
            }
        }
        assertTrue("Created system not found in the list of active systems", found);


    }

}
