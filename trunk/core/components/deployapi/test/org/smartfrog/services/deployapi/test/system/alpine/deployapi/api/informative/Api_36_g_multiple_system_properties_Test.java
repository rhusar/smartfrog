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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.informative;

import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * created 04-May-2006 13:46:55
 */

public class Api_36_g_multiple_system_properties_Test extends StandardTestBase {

    public Api_36_g_multiple_system_properties_Test(String name) {
        super(name);
    }

    private List<QName> properties = new ArrayList<QName>();

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        createSystem(null);
    }

    public void testGetMuwsProperties() throws Exception {
        properties.add(CddlmConstants.PROPERTY_MUWS_RESOURCEID);
        properties.add(CddlmConstants.PROPERTY_MUWS_MANAGEABILITY_CAPABILITY);
        assertGetMultiplePropertiesWorked(getSystem(), properties);
    }

    public void testGetsystemProperties() throws Exception {
        properties.add(CddlmConstants.PROPERTY_SYSTEM_CREATED_TIME);
        properties.add(CddlmConstants.PROPERTY_SYSTEM_SYSTEM_STATE);
        assertGetMultiplePropertiesWorked(getSystem(), properties);
    }

    public void testGetUnknownPropertyFaults() throws Exception {
        properties.add(CddlmConstants.PROPERTY_MUWS_RESOURCEID);
        properties.add(CddlmConstants.PROPERTY_MUWS_MANAGEABILITY_CAPABILITY);
        properties.add(CddlmConstants.FAULT_DEPLOYMENT_FAILURE);
        try {
            getSystem().getMultipleResourceProperties(properties);
            fail("Should have thrown a fault here");
        } catch (AlpineRuntimeException e) {
            log.debug("caught exception", e);
        }
    }
}
