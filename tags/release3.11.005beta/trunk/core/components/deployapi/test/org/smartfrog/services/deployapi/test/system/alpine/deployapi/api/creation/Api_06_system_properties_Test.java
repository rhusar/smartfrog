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
import static org.ggf.cddlm.generated.api.CddlmConstants.CDL_API_SYSTEM_CAPABILITY;
import static org.ggf.cddlm.generated.api.CddlmConstants.MUWS_CAPABILITY_MANAGEABILITY_CHARACTERISTICS;
import static org.ggf.cddlm.generated.api.CddlmConstants.MUWS_CAPABILITY_MANAGEABILITY_REFERENCES;
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_SYSTEM_CREATED_TIME;
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_SYSTEM_STARTED_TIME;
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_SYSTEM_TERMINATED_TIME;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

import java.util.List;

/**
 * created 13-Apr-2006 13:51:02
 * Create a system , then destroy it immediately.
 */

public class Api_06_system_properties_Test extends StandardTestBase {

    public Api_06_system_properties_Test(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        SystemSession system = createSystem(null);
    }

    public void testCreateCreatedTimeDestroySystem() throws Exception {
        SystemSession system = getPortal().create(null);
        //this sets it up for cleanup on teardown
        setSystem(system);
        Element time = system.getResourcePropertySingle(PROPERTY_SYSTEM_CREATED_TIME);
        String value = time.getValue();
        log.info("Created time=" + value);
    }

    public void testMuwsCapabilitiesFound() throws Exception {
        List<Element> muws = getSystemMuwsCapabilities();
        assertTrue("muws list length is zero",muws.size()>0);
    }

    public void testCapabilityCdlSystem() throws Exception {
        assertSystemCapable(CDL_API_SYSTEM_CAPABILITY);
    }

    public void testCapabilityMuwsManageabilityReferences() throws Exception {
        assertSystemCapable(MUWS_CAPABILITY_MANAGEABILITY_REFERENCES);
    }

    public void testCapabilityMuwsManageabilityCharacteristics() throws Exception {
        assertSystemCapable(MUWS_CAPABILITY_MANAGEABILITY_CHARACTERISTICS);
    }

    public void testStartedTimeExists() throws Exception {
        Element time = getSystem().getResourcePropertySingle(PROPERTY_SYSTEM_STARTED_TIME,false);
    }

    public void testTerminatedTimeExists() throws Exception {
        Element time = getSystem().getResourcePropertySingle(PROPERTY_SYSTEM_TERMINATED_TIME, false);
    }


}
