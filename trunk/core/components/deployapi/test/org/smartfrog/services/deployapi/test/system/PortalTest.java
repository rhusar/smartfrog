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
import org.smartfrog.services.deployapi.client.PortalEndpointer;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.binding.EprHelper;
import org.smartfrog.sfcore.languages.cdl.utils.ElementsIterator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.ggf.xbeans.cddlm.api.StaticPortalStatusType;
import org.ggf.xbeans.cddlm.api.PortalInformationType;
import org.ggf.xbeans.cddlm.api.NameUriListType;
import org.ggf.xbeans.cddlm.api.UriListType;
import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyResponseDocument;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Node;

import java.util.List;
import java.util.ArrayList;

/**
 * created 21-Sep-2005 14:24:11
 */

public class PortalTest extends ApiTestBase {
    public static final String XPATH_STATUS = "api:StaticPortalStatus";


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
            String id=system.getCachedResourceId();
            String idProperty= system.getResourceId();
            assertEquals(id,idProperty);
        } finally {
            terminateSystem(system);
        }
    }

    private void logSystemCreated(SystemEndpointer system) {
        log.info("Created system "+system.getCachedResourceId()+" @ "+system.getEndpointer().getAddress());
    }

    public void testCreate() throws Exception {
        SystemEndpointer system = null;
        try {
            system = createSystem();
            logSystemCreated(system);
            String id = system.getCachedResourceId();
            String idProperty = system.getResourceId();
            assertEquals(id, idProperty);
        } finally {
            terminateSystem(system);
        }
    }


    public void testPortalResourceID() throws Exception {
        PortalEndpointer portal = getPortal();
        Element elt=portal.getPropertyXom(Constants.PROPERTY_MUWS_RESOURCEID);
        String id1 = elt.getValue();
        assertNotNull(id1);
        assertTrue(id1.length()>0);
        assertTrue(id1.indexOf("uuid_")==0);
        assertEquals(id1,id1.trim());
        String id2=portal.getResourceId();
        assertEquals(id1,id2);
    }

    private PortalEndpointer getPortal() {
        return getOperation().getPortal();
    }


    /**
     * Here to take some confusion out of the loop
     * @throws Exception
     */
    public void testStaticPortalStatusXom() throws Exception {
        Element status = getOperation().getPortalPropertyXom(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
        Nodes nodes = status.query(XPATH_STATUS, Constants.XOM_CONTEXT);
        assertEquals(XPATH_STATUS+" resolved",1,nodes.size());
        Node n1=nodes.get(0);
        assertTrue(XPATH_STATUS+" resolved to an element",n1 instanceof Element);
        Element staticstatus =(Element)n1;
        nodes = staticstatus.query("api:portal", Constants.XOM_CONTEXT);
        assertEquals("portal resolved", 1, nodes.size());
        Element portal=(Element)nodes.get(0);
        Nodes languages=staticstatus.query("api:languages", Constants.XOM_CONTEXT);
        assertTrue(languages.size()>=1);
        nodes= staticstatus.query("api:notifications", Constants.XOM_CONTEXT);
        assertTrue(nodes.size() >= 1);
        Element notifications=(Element) nodes.get(0);
        ElementsIterator it=new ElementsIterator(notifications);
        boolean found=false;
        for(Element n:it) {
            if (Constants.WSRF_WSNT_NAMESPACE.equals(n.getValue().trim())) {
                found=true;
                break;
            }
        }
        assertTrue("WSNT is NOT supported", found);
    }

    public void NotestStaticPortalStatus() throws Exception {

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
        assertTrue("WSNT is NOT supported", found);
    }

    public void testActiveApplications() throws Exception {
        Element graph = getOperation().getPortalPropertyXom(Constants.PROPERTY_PORTAL_ACTIVE_SYSTEMS);
        Nodes systems=graph.query("api:ActiveSystems/api:system",Constants.XOM_CONTEXT);
        int systemCount = systems.size();
        List<EndpointReference> systemsList2=new ArrayList<EndpointReference>(systemCount);
        for(int i=0;i< systemCount;i++) {
            Element job=(Element) systems.get(i);
            EndpointReference epr= EprHelper.XomWsa2003ToEpr(job);
            systemsList2.add(epr);
            log.info(EprHelper.stringify(epr));
        }
        List<EndpointReference> systemsList = getOperation().listSystems();
        assertEquals(systemCount,systemsList.size());
        int count=0;
        for (EndpointReference epr : systemsList) {
            assertTrue(EprHelper.compareEndpoints(epr, systemsList2.get(count++)));
        }
    }


}
