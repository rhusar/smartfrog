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

package org.smartfrog.test.unit.projects.alpine.om;

import org.smartfrog.test.unit.projects.alpine.ParserTestBase;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.faults.FaultConstants;
import nu.xom.Element;

/**
 
 */
public class FaultTest extends ParserTestBase {
    private Fault fault;
    public static final String HOSTNAME = "lucky";

    public FaultTest(String name) {
        super(name);
    }

    /**
     * Sets up the fixture by initialising the parser
     */
    protected void setUp() throws Exception {
        super.setUp();
        fault = new Fault();
        fault.addFaultDetail(FaultConstants.QNAME_FAULTDETAIL_HOSTNAME, HOSTNAME);
    }

    public void testHasCode() throws Exception {
        assertNotNull("no fault code", fault.getFaultCode());
    }

    public void testHasDetail() throws Exception {
        assertNotNull("no fault detail", fault.getFaultDetail());
    }
    
    public void testHasString() throws Exception {
        assertNotNull("no fault string",fault.getFaultString());
    }

    public void testLastCodeWins() throws Exception {
        fault.setFaultCode("a");
        fault.setFaultCode("b");
        assertEquals("b",fault.getFaultCode());
    }

    public void testLastStringWins() throws Exception {
        fault.setFaultString("a");
        fault.setFaultString("b");
        assertEquals("b", fault.getFaultString());
    }
    
    public void testDetail() throws Exception {
        Element child = getHostnameDetail();
        assertNotNull("hostname not found",child);
        assertEquals(HOSTNAME, child.getValue());
        
    }

    private Element getHostnameDetail() {
        Element child = fault.getFirstFaultDetailChild(FaultConstants.QNAME_FAULTDETAIL_HOSTNAME);
        return child;
    }

    public void testDeleteDetail() throws Exception {
        fault.setFaultDetail(null);
        Element child = getHostnameDetail();
        assertNull("detail not reset",child);
        
    }
}
