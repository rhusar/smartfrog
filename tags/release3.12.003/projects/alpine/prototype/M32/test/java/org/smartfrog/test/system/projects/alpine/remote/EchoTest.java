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
package org.smartfrog.test.system.projects.alpine.remote;

import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.Body;
import org.smartfrog.projects.alpine.om.soap11.Header;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.faults.SoapException;

import nu.xom.Element;
import nu.xom.Elements;


/**
 * Tests that the echo testpoint is functional. This uses the client API, so tests that too
 * created 05-Apr-2006 12:08:01
 */

public class EchoTest extends RemoteTestBase {

    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        createMessageContext("echo");
    }

    public EchoTest(String name) {
        super(name);
    }

    public void testEmptyEcho() throws Throwable {
        MessageDocument request = messageCtx.createRequest();
        request.setAddressDetails(address);
        send(messageCtx);
    }

    public void testEchoString() throws Exception {
        MessageDocument request = messageCtx.createRequest();
        request.setAddressDetails(address);
        Body body = request.getBody();
        Element payload = new Element("payload");
        String text = "Hello, World";
        payload.appendChild(text);
        body.addOrReplaceChild(payload);
        Transmission transmission = send(messageCtx);
        MessageDocument response = transmission.getContext().getResponse();
        Body body2=response.getBody();
        Elements childElements = body2.getChildElements("payload");
        assertEquals(1,childElements.size());
        Element pl2=childElements.get(0);
        String value = pl2.getValue();
        assertEquals(text,value);
    }

    public void testNotUnderstoodHeader() throws Exception {
        MessageDocument request = messageCtx.createRequest();
        request.setAddressDetails(address);
        Header header = request.getEnvelope().getHeader();
        Element headerElement=new Element("header");
        header.setHeaderElement(headerElement,true);
        Body body = request.getBody();
        Element payload = new Element("payload");
        String text = "Hello, World";
        payload.appendChild(text);
        body.addOrReplaceChild(payload);
        try {
            Transmission transmission = send(messageCtx);
            fail("expected a not understood fault");
        } catch (SoapException e) {
            Fault fault=e.getFault();
            assertEquals(SoapConstants.FAULTCODE_MUST_UNDERSTAND,fault.getFaultCode());
            assertNotNull(fault.getFaultActor(), fault.getFaultActor());
            assertNotNull(fault.getFaultString(), fault.getFaultString());
        }
    }

}
