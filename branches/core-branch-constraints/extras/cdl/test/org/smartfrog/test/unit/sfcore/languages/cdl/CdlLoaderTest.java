/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.test.unit.sfcore.languages.cdl;

import org.smartfrog.services.xml.utils.ResourceLoader;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * verify that basic doc loading works
 *
 * @author root
 */
public class CdlLoaderTest extends XmlTestBase {


    public CdlLoaderTest(String test) {
        super(test);
    }

    /**
     * The fixture set up called before every test method.
     */
    protected void setUp() throws Exception {
        super.setUp();
        initLaxParser();
    }

    private void initLaxParser() throws SAXException {
        ResourceLoader loader = new ResourceLoader(this.getClass());
    }


    public void testMinimal() throws Exception {
        assertValidCDL(CDL_DOC_MINIMAL);
    }

    public void testExtraElements() throws Exception {
        assertValidCDL(CDL_DOC_EXTRA_ELEMENTS);
    }

    public void testDocumented() throws Exception {
        assertValidCDL(CDL_DOC_DOCUMENTED);
    }

    public void testExpression() throws Exception {
        assertValidCDL(CDL_DOC_EXPRESSION_1);
    }

    public void testLazy1() throws Exception {
        assertValidCDL(CDL_DOC_LAZY_1);
    }

    public void testLazy2() throws Exception {
        assertValidCDL(CDL_DOC_LAZY_2);
    }

    public void testParameterization1() throws Exception {
        assertValidCDL(CDL_DOC_PARAMETERIZATION_1);
    }

    public void testFullExample1() throws Exception {
        assertValidCDL(CDL_DOC_FULL_EXAMPLE_1);
    }

    public void testFullExample2() throws Exception {
        assertValidCDL(CDL_DOC_FULL_EXAMPLE_2);
    }


    public void testReferences1() throws Exception {
        assertValidCDL(CDL_DOC_REFERENCES_1);
    }

    public void testReferences2() throws Exception {
        assertValidCDL(CDL_DOC_REFERENCES_2);
    }

    public void testReferences3() throws Exception {
        assertValidCDL(CDL_DOC_REFERENCES_3);
    }

    public void testWebServer() throws Exception {
        assertValidCDL(CDL_DOC_WEBSERVER);
    }

    public void testWebServerDefaultNamespace() throws Exception {
        assertValidCDL(CDL_DOC_WEBSERVER_DEFAULT_NAMESPACE);
    }

    public void testWebServerNoNamespace() throws Exception {
        assertValidCDL(CDL_DOC_WEBSERVER_NO_NAMESPACE);
    }

/*    public void test() throws Exception {
        assertValidCDL(CDL_DOC_);
    }
    public void test() throws Exception {
        assertValidCDL(CDL_DOC_);
    }
    public void test() throws Exception {
        assertValidCDL(CDL_DOC_);
    }
    public void test() throws Exception {
        assertValidCDL(CDL_DOC_);
    }*/



    public void testWrongDocNamespace() throws Exception {
        assertInvalidCDL(CDL_DOC_WRONG_NAMESPACE, WRONG_NAMESPACE_TEXT);
    }

    public void testWrongEltOrder() throws Exception {
        assertInvalidCDL(CDL_DOC_WRONG_ELT_ORDER, null);
    }

    public void NotestWrongRootEltType() throws Exception {
        assertInvalidCDL(CDL_DOC_WRONG_ROOT_ELT_TYPE,
                "Cannot find the declaration of element");
    }

/*
    public void testDuplicateNames() throws Exception {
        CdlDocument doc = load(CDL_DOC_DUPLICATE_NAMES);
        doc.validate();
    }
*/

    public void testMissingFile() throws Exception {
        try {
            assertInvalidCDL("no-such-document.cdl", "Not found");
        } catch (IOException e) {
            //expected
        }
    }


}