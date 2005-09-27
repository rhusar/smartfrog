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
import org.apache.xmlbeans.XmlObject;
import org.ggf.xbeans.cddlm.api.StaticPortalStatusType;
import org.ggf.xbeans.cddlm.api.PortalInformationType;
import org.ggf.xbeans.cddlm.api.NameUriListType;
import org.ggf.xbeans.cddlm.api.UriListType;
import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyResponseDocument;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Node;

/**
 * created 21-Sep-2005 14:24:11
 */

public class PortalTest extends ApiTestBase {
    public static final String XPATH_STATUS = "wsrf-rp:GetResourcePropertyResponse/api:StaticPortalStatus";


    public PortalTest(String name) {
        super(name);
    }

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
            SystemEndpointer system = createSystem("0.0.0.0");
            logSystemCreated(system);
            fail("Expected to fail");
        } catch (AxisFault fault) {
            assertFaultMatches(fault, Constants.F_UNSUPPORTED_CREATION_HOST);
        }
    }


    public void testCreateLocalhost() throws Exception {
        SystemEndpointer system=null;
        try {
            system = createSystem("localhost");
            logSystemCreated(system);
            String id=system.getResourceID();
            String idProperty=getSystemResourceID(system);
            assertEquals(id,idProperty);
        } finally {
            terminateSystem(system);
        }
    }

    private void logSystemCreated(SystemEndpointer system) {
        log.info("Created system "+system.getResourceID()+" @ "+system.getEndpointer().getAddress());
    }

    public void testCreate() throws Exception {
        SystemEndpointer system = null;
        try {
            system = createSystem();
            logSystemCreated(system);
            String id = system.getResourceID();
            String idProperty = getSystemResourceID(system);
            assertEquals(id, idProperty);
        } finally {
            terminateSystem(system);
        }
    }


    public void testPortalResourceID() throws Exception {
        GetResourcePropertyResponseDocument resourceProperty = getPortalResourceProperty(Constants.PROPERTY_MUWS_RESOURCEID);
        String id = extractResourceID(resourceProperty);
        log.info("Portal resource ID="+id);
        resourceProperty = getPortalResourceProperty(Constants.PROPERTY_MUWS_RESOURCEID);
        String id2 = extractResourceID(resourceProperty);
        assertEquals(id,id2);
    }

    private static boolean dump=true;
    private void maybedump(XmlObject object) {
        if(dump) {
            object.dump();
        }
    }


    /**
     * Here to take some confusion out of the loop
     * @throws Exception
     */
    public void testStaticPortalStatusXom() throws Exception {
        Element graph = getOperation().getPortalPropertyXom(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
        Nodes nodes = graph.query(XPATH_STATUS, Constants.XOM_CONTEXT);
        assertEquals(XPATH_STATUS+" resolved",1,nodes.size());
        Node n1=nodes.get(0);
        assertTrue(XPATH_STATUS+" resolved to an element",n1 instanceof Element);
        Element status=(Element)n1;
        nodes = status.query("api:portal", Constants.XOM_CONTEXT);
        assertEquals("portal resolved", 1, nodes.size());
        Element portal=(Element)nodes.get(0);
    }

    public void testStaticPortalStatus() throws Exception {

        GetResourcePropertyResponseDocument responseDoc;
        responseDoc = getPortalResourceProperty(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
        maybedump(responseDoc);
        GetResourcePropertyResponseDocument.GetResourcePropertyResponse response;
        response = responseDoc.getGetResourcePropertyResponse();
        StaticPortalStatusType status;
        status =StaticPortalStatusType.Factory.parse(response.getDomNode());
        maybedump(status);
        assertNotNull("status is null",status);
        PortalInformationType portal = status.getPortal();
        assertNotNull("portal is null", portal);
        String name = portal.getName();
        log.info(name);
        assertNotNull(name);
        log.info(portal.getBuild());
        log.info("TZ offset"+portal.getTimezoneUTCOffset());
        NameUriListType languages = status.getLanguages();
        assertNotNull("languages are null",status);
        assertTrue("Languages are supported",languages.sizeOfItemArray()>0);
        UriListType notifications = status.getNotifications();
        assertNotNull("notifications are null", notifications);

        boolean found=false;
        for(String language: notifications.getItemList()) {
            if(Constants.WSRF_WSNT_NAMESPACE.equals(language)) {
                found=true;
            }
        }
        assertTrue("WSNT is supported", found);
    }

    public void testActiveApplications() throws Exception {
        GetResourcePropertyResponseDocument responseDoc;
        responseDoc = getPortalResourceProperty(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);


    }

}
