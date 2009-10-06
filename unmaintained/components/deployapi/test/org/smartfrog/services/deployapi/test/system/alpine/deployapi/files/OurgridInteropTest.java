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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.files;

import org.smartfrog.services.deployapi.test.system.alpine.AlpineTestBase;
import org.smartfrog.services.deployapi.alpineclient.model.PortalSession;
import org.smartfrog.projects.alpine.transport.ResourceTransmission;
import org.smartfrog.projects.alpine.core.MessageContext;

/**
 * This test case checks that we can do interoperability
 * 
 * created 11-Dec-2006 14:23:18
 */

public class OurgridInteropTest extends AlpineTestBase {


    public OurgridInteropTest(String name) {
        super(name);
    }


    public void testGetResource() throws Exception {
        PortalSession session = getPortal();
        MessageContext ctx = session.createMessageContextWithRequest(session.getEndpoint(),
                "http://www.gridforum.org/cddlm/deployapi/2005/02/wsdl/GetResourceProperty");
        ResourceTransmission tx=new ResourceTransmission(ctx,
                "/org/smartfrog/services/deployapi/test/system/alpine/deployapi/files/getResourceProperties.xml");
        session.getQueue().transmit(tx);
        tx.blockForResult(session.getTimeout());
    }
}
