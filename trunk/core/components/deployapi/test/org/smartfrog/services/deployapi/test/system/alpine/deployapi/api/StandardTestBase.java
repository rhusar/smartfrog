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

import nu.xom.Element;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.alpineclient.model.SystemSession;
import org.smartfrog.services.deployapi.test.system.alpine.AlpineTestBase;

/**
 * created 11-Apr-2006 14:56:59
 */

public abstract class StandardTestBase extends AlpineTestBase {

    protected StandardTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected boolean isSystemInActiveSystems(String id) {
        SoapElement activeSystems = (SoapElement) getPortal()
                .getResourceProperty(CddlmConstants.PROPERTY_PORTAL_ACTIVE_SYSTEMS);
        boolean found = false;
        for (Element e : activeSystems.elements()) {
            AlpineEPR epr = new AlpineEPR(e, CddlmConstants.WS_ADDRESSING_NAMESPACE);
            epr.validate();
            SystemSession system = new SystemSession(getPortal(), epr);
            String newResID = system.getResourceProperty(CddlmConstants.PROPERTY_MUWS_RESOURCEID).getValue();
            assertNotNull(newResID);
            assertTrue(newResID.length() > 0);
            if (newResID.equals(id)) {
                found = true;
            }
        }
        return found;
    }
}
