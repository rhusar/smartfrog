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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.portal;

import static org.ggf.cddlm.generated.api.CddlmConstants.CDL_API_TYPES_NAMESPACE;
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

/**
 * Examine the state of a portal; including languages and notification mechanisms.
 * created 13-Apr-2006 12:25:21
 */

public class Api_04_portal_getPortalState_Test extends StandardTestBase {

    private SoapElement state;

    public Api_04_portal_getPortalState_Test(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        state = (SoapElement) getProperty(PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
    }


    public void testValidType() throws Exception {
        assertEquals(CDL_API_TYPES_NAMESPACE, state.getNamespaceURI());
        assertEquals("StaticPortalStatus", state.getLocalName());
    }

    public void testHasLanguages() throws Exception {
        assertQueryResolves(state, "api:languages");
    }

    public void testHasNotifications() throws Exception {
        assertQueryResolves(state, "api:notifications");
    }

    public void testCdlSupported() throws Exception {
        assertQueryResolves(state, "api:languages/api:item/api:name[.=\"CDL-1.0\"]");
    }

    public void testWSNSupported() throws Exception {
        assertQueryResolves(state,
                "api:notifications/api:item[.=\"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd\"]");
    }

}


