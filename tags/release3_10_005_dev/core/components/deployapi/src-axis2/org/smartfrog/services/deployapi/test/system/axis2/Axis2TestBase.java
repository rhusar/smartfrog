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
package org.smartfrog.services.deployapi.test.system.axis2;

import junit.framework.TestCase;
import org.smartfrog.services.deployapi.test.unit.UnitTestBase;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.ws.commons.om.OMAttribute;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
/**
 * created 24-Apr-2006 13:43:38
 */

public abstract class Axis2TestBase extends UnitTestBase {
    public final static EndpointReference EPR_LOCAL_PORTAL
            = new EndpointReference("http://localhost:5050/services/Portal/");
    public final static EndpointReference EPR_SAMPLE_JOB
            = new EndpointReference("http://localhost:5050/services/System/#uuid_1235678_0045");

    protected Axis2TestBase(String name) {
        super(name);
    }


    public OMElement loadTestOMElement(String resource, String name) throws IOException,
            XMLStreamException {
        InputStream in = loadResource(resource);
        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(in);
        //create the builder
        StAXOMBuilder builder =
                new StAXOMBuilder(parser);
        OMElement doc = builder.getDocumentElement();
        doc.build();
        //now we need to locate the child with attribute "name=name";
        Iterator childElements = doc.getChildrenWithName(TEST_ELEMENT);
        while (childElements.hasNext()) {
            OMElement element = (OMElement) childElements.next();
            if (element == null) {
                //bad things here
                break;
            }
            OMAttribute attribute = element.getAttribute(TEST_NAME);
            if (attribute == null) {
                attribute = element.getAttribute(TEST_NAME_LOCAL);
            }
            if (attribute != null && name.equals(attribute.getAttributeValue())) {
                return element.getFirstElement();
            }
        }
        fail("No node of name " + name + " found in resource " + resource);
        return null;
    }
}
