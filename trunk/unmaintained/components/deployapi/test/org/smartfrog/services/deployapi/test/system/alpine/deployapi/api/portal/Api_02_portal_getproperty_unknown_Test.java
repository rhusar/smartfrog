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

import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.faults.SoapException;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;

import javax.xml.namespace.QName;

/**
 * created 12-Apr-2006 16:19:16
 */

public class Api_02_portal_getproperty_unknown_Test extends StandardTestBase {

    public Api_02_portal_getproperty_unknown_Test(String name) {
        super(name);
    }

    public void testUnknownProperty() throws Exception {
        try {
            getProperty(new QName(CddlmConstants.CDL_API_TYPES_NAMESPACE, "unknown-property"));
            fail("Fault not thrown");
        } catch (SoapException e) {
            Fault fault = e.getFault();
            log.info("received fault from the endpoint",e);
/*            assertEquals(CddlmConstants.FAULT_WSRF_WSRP_INVALID_RESOURCE_PROPERTY_QNAME.toString(),
                    fault.getFaultCode());*/
        }
    }
}
