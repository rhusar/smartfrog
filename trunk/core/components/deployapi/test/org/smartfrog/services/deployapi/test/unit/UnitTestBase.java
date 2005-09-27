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

import junit.framework.TestCase;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlCursor;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMAttribute;
import org.apache.axis2.om.impl.llom.builder.StAXOMBuilder;
import org.w3c.dom.Node;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.endpoints.PortalEndpoint;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.xml.utils.XmlCatalogResolver;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.ggf.xbeans.cddlm.testhelper.TestsDocument;
import org.ggf.xbeans.cddlm.testhelper.TestsType;
import org.ggf.xbeans.cddlm.testhelper.TestType;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;

import nu.xom.XPathContext;

/**
 * created 21-Sep-2005 14:57:51
 */

public abstract class UnitTestBase extends TestCase {
    public static final String TEST_FILES_API_VALID = "test/api/valid/";
    public static final String DECLARE_TEST_NAMESPACE= "declare namespace t='"+ Constants.TEST_HELPER_NAMESPACE+"'; ";
    XmlOptions options;
    public static final QName TEST_ELEMENT=new QName(Constants.TEST_HELPER_NAMESPACE,"test");
    public static final QName TEST_NAME = new QName(Constants.TEST_HELPER_NAMESPACE, "name");
    public static final QName TEST_NAME_LOCAL = new QName("name");
    private XmlCatalogResolver resolver;


    public UnitTestBase(String name) {
        super(name);
    }

    public XmlCatalogResolver getResolver() {
        return resolver;
    }
    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        resolver = new XmlCatalogResolver(new ResourceLoader());
        options = new XmlOptions();
        options.setEntityResolver(resolver);
    }

    /**
     * Assert that a doc is valid
     *
     * @param bean bean to check
     * @throws junit.framework.AssertionFailedError
     *          with all the error messages inside
     */
    public void assertValid(XmlObject bean) {
        assertNotNull("XmlObject is null", bean);
        ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);
        if (!bean.validate(validationOptions)) {
            StringBuffer errors = new StringBuffer();
            for (XmlError error : validationErrors) {
                errors.append(error.getMessage());
                errors.append("\n");
            }
            fail(errors.toString());
        }
    }

    public void assertName(XmlObject bean,String namespace,String localname) {
        if (namespace== null) {
            namespace = "";
        }
        QName match = new QName(namespace, localname);
        assertName(bean, match);
    }

    public void assertName(XmlObject bean, QName match) {
        assertNotNull("XmlObject is null", bean);
        Node node = bean.getDomNode();
        String namespaceURI = node.getNamespaceURI();
        QName name = new QName(namespaceURI,node.getLocalName());
        assertEquals(match,name);
    }

    protected InputStream loadResource(String resource) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            throw new BaseException("Resource missing: " + resource);
        }
        return stream;
    }

    public XmlObject loadTestElement(String resource,String name) throws IOException, XmlException {
        Axis2Beans<TestsDocument> binder = new Axis2Beans<TestsDocument>(options);
        TestsDocument doc = binder.loadBeansFromResource(resource);
        XmlObject[] children = doc.selectChildren(Constants.TEST_HELPER_NAMESPACE,
                "tests");
        TestsType tests = doc.getTests();
        tests.dump();
        for(TestType test:tests.getTestList()) {
            test.dump();
            if("name".equals(test.getName())) {
                XmlCursor cursor=test.newCursor();
                if (cursor.toFirstChild()) {
                    return cursor.getObject();    
                } else {
                    throw new XmlException("Element "+test+" has no children");
                }
            }
        }

        throw new XmlException("No node of name " +
                name +
                " found in resource " +
                resource);
/*


        XmlObject[] xmlObjects = doc.selectPath(DECLARE_TEST_NAMESPACE
                +"//t:tests/t:test[@name='" + name + "']/child::*[position()=1]");
        if(xmlObjects.length==0) {
            throw new XmlException("No node of name "+name+" found in resource "+resource);
        } else {
            return xmlObjects[0];
        }
*/
    }

    public OMElement loadTestOMElement(String resource, String name) throws IOException, XmlException,
            XMLStreamException {
        InputStream in=loadResource(resource);
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
            if(element==null) {
                //bad things here
                break;
            }
            OMAttribute attribute = element.getAttribute(TEST_NAME);
            if (attribute == null) {
                attribute = element.getAttribute(TEST_NAME_LOCAL);
            }
            if(attribute!=null && name.equals(attribute.getValue())) {
                return element.getFirstElement();
            }
        }
        throw new XmlException("No node of name " + name + " found in resource " + resource);
    }
}
