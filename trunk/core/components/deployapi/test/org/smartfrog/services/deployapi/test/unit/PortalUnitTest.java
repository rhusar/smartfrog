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
import org.smartfrog.services.deployapi.transport.endpoints.PortalEndpoint;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.xml.utils.XmlCatalogResolver;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.ggf.xbeans.cddlm.api.CreateRequestDocument;
import org.ggf.xbeans.cddlm.api.CreateResponseDocument;
import org.ggf.xbeans.cddlm.testhelper.TestsDocument;
import org.ggf.xbeans.cddlm.testhelper.TestType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlCursor;
import org.apache.axis2.om.OMElement;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.io.IOException;

/**
 * created 14-Sep-2005 11:53:51
 */

public class PortalUnitTest extends TestCase {
    private PortalEndpoint portal;
    public static final String TEST_FILES_API_VALID = "test/api/valid/";
    public static final String DOC_CREATE = TEST_FILES_API_VALID +"api-create.xml";
    public static final String DECLARE_TEST_NAMESPACE= "declare namespace t='"+ Constants.TEST_HELPER_NAMESPACE+"'; ";
    XmlOptions options;
    public static final QName TEST_ELEMENT=new QName(Constants.TEST_HELPER_NAMESPACE,"test");

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        portal = new PortalEndpoint();
        XmlCatalogResolver resolver=new XmlCatalogResolver(new ResourceLoader());
        options=new XmlOptions();
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
    public XmlObject loadTestElement(String resource,String name) throws IOException, XmlException {
        Axis2Beans<TestsDocument> binder = new Axis2Beans<TestsDocument>(options);
        TestsDocument doc = binder.loadBeansFromResource(resource);

        XmlObject[] xmlObjects = doc.selectPath(DECLARE_TEST_NAMESPACE+"//t:tests/t:test[@name='" + name + "']");
        if(xmlObjects.length==0) {
            return null;
        } else {
            TestType test = (TestType) xmlObjects[0];
            XmlCursor cursor = test.newCursor();
            cursor.toChild(0);
            return cursor.getObject();
        }
    }

    public void testCreateValid() throws Exception {
        Axis2Beans<CreateRequestDocument.CreateRequest> requestBinder = new Axis2Beans<CreateRequestDocument.CreateRequest>();
        CreateRequestDocument.CreateRequest doc= (CreateRequestDocument.CreateRequest)
                loadTestElement(DOC_CREATE,"createRequestHostname");
        assertValid(doc);
        //test round tripping
        OMElement request = requestBinder.convert(doc);
        CreateRequestDocument.CreateRequest r2=requestBinder.convert(request);
        OMElement response=portal.Create(request);
        Axis2Beans<CreateResponseDocument> responseBinder = new Axis2Beans<CreateResponseDocument>();
        CreateResponseDocument responseDoc=responseBinder.convert(response);
        assertValid(responseDoc);
    }



}
