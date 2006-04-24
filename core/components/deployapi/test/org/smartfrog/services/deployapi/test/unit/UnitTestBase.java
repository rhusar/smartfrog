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

import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.services.xml.utils.XmlCatalogResolver;

import javax.xml.namespace.QName;

import java.io.InputStream;

/**
 * created 21-Sep-2005 14:57:51
 */

public abstract class UnitTestBase extends TestCase {
    public static final String TEST_FILES_API_VALID = "org/ggf/cddlm/files/api/valid/";
    public static final String DECLARE_TEST_NAMESPACE = "declare namespace t='" + Constants.TEST_HELPER_NAMESPACE
            + "'; ";
    public static final QName TEST_ELEMENT = new QName(Constants.TEST_HELPER_NAMESPACE, "test");
    public static final QName TEST_NAME = new QName(Constants.TEST_HELPER_NAMESPACE, "name");
    public static final QName TEST_NAME_LOCAL = new QName("name");
    private XmlCatalogResolver resolver;
    private static boolean dump = false;

    public static final String DOC_CREATE = TEST_FILES_API_VALID + "api-create.xml";
    protected static final String TESTS = "tests";
    public static final String TEST_CREATE_REQUEST_HOSTNAME = "createRequestHostname";


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
    }

    protected InputStream loadResource(String resource) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            throw new BaseException("Resource missing: " + resource);
        }
        return stream;
    }




    /**
     * assert that text is an iso date
     *
     * @param text text to check
     */
    protected void assertIsoDate(String text) {
        assertNotNull("No iso date", text);
        assertEquals("text aint ISO:" + text, 0, text.indexOf("20"));
        assertEquals("text aint ISO :" + text, 4, text.indexOf('-'));
        assertEquals("text aint ISO :" + text, 7, text.indexOf('-', 5));
        assertEquals("text aint ISO :" + text, 10, text.indexOf('T', 5));
    }
}
