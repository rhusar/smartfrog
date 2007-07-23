/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.ggf.cddlm.generated.api.CddlmConstants;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * created Jul 16, 2004 4:38:12 PM
 */

public class CdlCatalogTest extends XmlTestBase {

    private CdlCatalog catalog;

    public CdlCatalogTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        catalog = createCatalog();
    }


    public void testValidate() throws Exception {
        catalog.validateImportPaths();
    }

    public void testCdlLookup() {
        String cddlm = catalog.lookup(Constants.XMLNS_CDL);
        assertTrue(cddlm != null && cddlm.endsWith(CddlmConstants.CDL_FILENAME_XML_CDL));
    }


    public void testApiLookup() {
        String resource = catalog.lookup(Constants.XMLNS_DEPLOY_API_TYPES);
        assertTrue(resource != null &&
                resource.endsWith(Constants.DEPLOY_API_SCHEMA_FILENAME));
    }

    public void testCdlResolve() throws TransformerException {
        Source src = catalog.resolve(Constants.XMLNS_CDL, "");
        assertTrue(src != null);
    }

    public void testCdlEntityResolved() throws IOException, SAXException {
        assertResolved(Constants.XMLNS_CDL);
    }

    public void testSaxResolve() throws IOException, SAXException {
        assertResolved(Constants.XMLNS_DEPLOY_API_TYPES);
    }

    private void assertResolved(String uri) throws SAXException, IOException {
        InputSource src = catalog.resolveEntity("", uri);
        assertTrue("Did not resolve " + uri, src != null);
    }

    private void assertNotResolved(String uri) throws SAXException,
            IOException {
        InputSource src = catalog.resolveEntity("", uri);
        assertTrue("Did not want to resolve " + uri, src == null);
    }

    public void testResolveFile() throws IOException, SAXException {
        assertResolved("file://xml-cdl.xsd");
    }

    public void testResolveFile2() throws IOException, SAXException {
        assertResolved("file:///dir/subdir/xml-cdl.xsd");
    }

    public void testNoResolveFile3() throws IOException, SAXException {
        assertNotResolved("file://xml-cdl.xsd/");
    }

    public void testNoResolveFile4() throws IOException, SAXException {
        assertNotResolved("file://");
    }

    public void testNoResolveEmptyString() throws IOException, SAXException {
        assertNotResolved("");
    }

    public void testNoResolveNull() throws IOException, SAXException {
        assertNotResolved("");
    }

}


