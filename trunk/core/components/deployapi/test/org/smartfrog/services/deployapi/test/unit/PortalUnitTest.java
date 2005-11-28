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
package org.smartfrog.services.deployapi.test.unit;

import org.apache.axis2.om.OMElement;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.endpoints.PortalEndpoint;
import org.smartfrog.services.deployapi.transport.endpoints.portal.CreateProcessor;

import javax.xml.namespace.QName;

/**
 * created 14-Sep-2005 11:53:51
 */

public class PortalUnitTest extends UnitTestBase {

    /**
     * Constructs a test case with the given name.
     */
    public PortalUnitTest(String name) {
        super(name);
    }

    private PortalEndpoint portal;

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        portal = new PortalEndpoint();
    }

    public void testOMLookup() throws Exception {
        OMElement ome = loadTestOMElement(DOC_CREATE, TEST_CREATE_REQUEST_HOSTNAME);
        assertNotNull(ome);
    }

}
