/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.test.system;

import org.smartfrog.services.deployapi.client.SystemEndpointer;
import org.smartfrog.services.deployapi.system.Constants;
import org.apache.axis2.AxisFault;
import org.ggf.xbeans.cddlm.api.StaticPortalStatusType;
import org.ggf.xbeans.cddlm.api.PortalInformationType;
import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyResponseDocument;

import nu.xom.Element;

/**
 * created 21-Sep-2005 14:24:11
 */

public class PortalTest extends ApiTestBase {

    public void testUnknownApp() throws Exception {
        assertNoSuchApplication("Unknown");
    }

    public void testCreateBadHost() throws Exception {
        try {
            createSystem("no-such-host-is-allowed");
            fail("Expected to fail");
        } catch (AxisFault fault) {
            assertFaultMatches(fault, Constants.F_UNSUPPORTED_CREATION_HOST);
        }
    }

    public void testCreateBadIPaddr() throws Exception {
        try {
            createSystem("0.0.0.0");
            fail("Expected to fail");
        } catch (AxisFault fault) {
            assertFaultMatches(fault, Constants.F_UNSUPPORTED_CREATION_HOST);
        }
    }

    public void testCreate() throws Exception {
        SystemEndpointer system = createSystem();
        terminateSystem(system);
    }

    public void testCreateLocalhost() throws Exception {
        SystemEndpointer system = createSystem("localhost");
        terminateSystem(system);
    }

    public void testPortalResourceID() throws Exception {
        String id =getOperation().getResourcePropertyText(Constants.PROPERTY_MUWS_RESOURCEID);
        log.info("Portal resource ID="+id);
        String id2= getOperation().getResourcePropertyText(Constants.PROPERTY_MUWS_RESOURCEID);
        assertEquals(id,id2);
    }

    public void testStaticPortalStatus() throws Exception {
        Element graph= getOperation().getResourcePropertyXom(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
        GetResourcePropertyResponseDocument responseDoc;
        responseDoc = getOperation().getPropertyResponse(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
        GetResourcePropertyResponseDocument.GetResourcePropertyResponse response;
        response = responseDoc.getGetResourcePropertyResponse();
        StaticPortalStatusType status;
        status =StaticPortalStatusType.Factory.parse(response.getDomNode());
        PortalInformationType portal = status.getPortal();
        portal.getName();
    }


}
