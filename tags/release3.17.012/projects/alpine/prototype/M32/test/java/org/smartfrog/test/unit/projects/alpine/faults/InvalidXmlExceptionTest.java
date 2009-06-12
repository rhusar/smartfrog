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

package org.smartfrog.test.unit.projects.alpine.faults;

import org.smartfrog.test.unit.projects.alpine.ParserTestBase;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.Body;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.faults.FaultConstants;
import nu.xom.Element;


/**
 
 */
public class InvalidXmlExceptionTest extends ParserTestBase {
    
    private Fault fault;
    
    private SoapElement element=new Body();
    
    public InvalidXmlExceptionTest(String name) {
        super(name);
    }

    /**
     * Sets up the fixture by initialising the parser
     */
    protected void setUp() throws Exception {
        super.setUp();
        try {
            throw new InvalidXmlException(element,"nothing interesting to say");
        } catch (AlpineRuntimeException e) {
            fault = e.GenerateSoapFault(SoapConstants.URI_SOAPAPI);
        }
    }
        
    //InvalidXmlException
    public void testElementIsPassedIn() throws Exception {
        SoapElement detail=fault.getFaultDetail();
        SoapElement child=(SoapElement) fault.getFirstFaultDetailChild(FaultConstants.QNAME_FAULTDETAIL_INVALID_XML);
        assertNotNull("Node not found in detail",child);
        Element nested=child.getFirstChildElement(element.getQName());
        assertNotNull("nested not found in detail", nested);
        assertTrue("failed: nested instanceof Body", nested instanceof Body);
        
    }

}
