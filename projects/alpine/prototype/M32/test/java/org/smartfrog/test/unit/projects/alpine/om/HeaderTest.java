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
package org.smartfrog.test.unit.projects.alpine.om;

import junit.framework.TestCase;
import nu.xom.Element;
import org.smartfrog.projects.alpine.om.soap11.Header;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;
import org.smartfrog.projects.alpine.handlers.MustUnderstandChecker;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.faults.MustUnderstandFault;

/**
 * created 22-Mar-2006 15:23:38
 */

public class HeaderTest extends TestCase {

    Element element;
    private MustUnderstandChecker checker;

    public static final String SOAP11 = SoapConstants.URI_SOAP11;
    public static final String SOAP12 = SoapConstants.URI_SOAP12;
    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        element=new Element("something");
        checker = new MustUnderstandChecker();
    }

    public void testAddMuTrue() throws Exception {
        assertFalse(Header.isMustUnderstand(element, SOAP11));
        Header.setMustUnderstand(element, SOAP11, true);
        assertTrue(Header.isMustUnderstand(element, SOAP11));
        assertFalse(Header.isMustUnderstand(element, SOAP12));
    }

    public void testAddMuTrueSoap12() throws Exception {
        assertFalse(Header.isMustUnderstand(element, SOAP12));
        Header.setMustUnderstand(element, SOAP12, true);
        assertTrue(Header.isMustUnderstand(element, SOAP12));
        assertFalse(Header.isMustUnderstand(element, SOAP11));
    }


    public void testAddMuFalse() throws Exception {
        assertFalse(Header.isMustUnderstand(element, SOAP11));
        Header.setMustUnderstand(element, SOAP11, false);
        assertFalse(Header.isMustUnderstand(element, SOAP11));
    }

    public void testAddMuTrueFalse() throws Exception {
        assertFalse(Header.isMustUnderstand(element, SOAP11));
        Header.setMustUnderstand(element, SOAP11, true);
        assertTrue(Header.isMustUnderstand(element, SOAP11));
        Header.setMustUnderstand(element, SOAP11, false);
        assertFalse(Header.isMustUnderstand(element, SOAP11));
    }

    public void testMuCheckerPass() throws Exception {
        MessageContext mc=new MessageContext();
        MessageDocument request = mc.createRequest();
        Header header = request.getEnvelope().getHeader();
        assertNotNull(header);
        header.appendChild(element);
        checker.processMessage(mc,new EndpointContext());
    }

    public void testMuCheckerFail() throws Exception {
        MessageContext mc = new MessageContext();
        MessageDocument request = mc.createRequest();
        Header header = request.getEnvelope().getHeader();
        assertNotNull(header);
        Header.setMustUnderstand(element, SOAP11, true);
        header.appendChild(element);
        try {
            checker.processMessage(mc, new EndpointContext());
            fail("Should have rejected the message");
        } catch (MustUnderstandFault e) {
            //success
        }
    }


}
