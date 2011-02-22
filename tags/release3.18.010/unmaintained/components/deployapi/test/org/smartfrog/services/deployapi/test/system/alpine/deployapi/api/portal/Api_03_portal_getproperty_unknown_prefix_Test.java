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

import nu.xom.Element;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.faults.SoapException;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.services.deployapi.alpineclient.model.WsrfSession;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

import javax.xml.namespace.QName;

/**
 * created 12-Apr-2006 16:19:16
 */

public class Api_03_portal_getproperty_unknown_prefix_Test extends StandardTestBase {

    public Api_03_portal_getproperty_unknown_prefix_Test(String name) {
        super(name);
    }

    public void testUnknownPrefix() throws Exception {
        try {
            WsrfSession wsrfSession = getPortal();
            SoapElement request;
            request = new SoapElement(WsrfSession.QNAME_WSRF_GET_PROPERTY);
            request.appendChild("api2:StaticPortalStatus");
            Transmission tx = wsrfSession.queue(request);
            wsrfSession.endGetResourceProperty(tx);
            fail("Fault not thrown");
        } catch (SoapException e) {
            Fault fault = e.getFault();
            log.info("Expected fault ",e);
/*
            assertEquals(CddlmConstants.FAULT_WSRF_WSRP_INVALID_RESOURCE_PROPERTY_QNAME.toString(),
                    fault.getFaultCode());
*/
        }
    }
}
