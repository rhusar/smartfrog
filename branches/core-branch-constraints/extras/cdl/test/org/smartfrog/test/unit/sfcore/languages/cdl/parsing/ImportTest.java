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
package org.smartfrog.test.unit.sfcore.languages.cdl.parsing;

import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

import java.net.URL;

/**
 * Tests that rely on import working. created 15-Jun-2005 16:02:30
 */

public class ImportTest extends XmlTestBase {
    public static final String EXAMPLE = "http://example.org/";
    public static final String FILE = "file:/home/example/file/";
    public static final String FILENAME = "something.cdl";

    public ImportTest(String name) {
        super(name);
    }


    public void testEchoImport() throws Exception {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_SF_ECHO);
    }


    public void testImportRecursive() throws Exception {
        assertInvalidCDL(IMPORT_RECURSIVE, ParseContext.ERROR_RECURSIVE_IMPORT_PREFIX);
    }

    public void testImportRecursiveLocal() throws Exception {
        assertInvalidCDL(IMPORT_RECURSIVE_LOCAL, ParseContext.ERROR_RECURSIVE_LOCAL_IMPORT);
    }

    private void assertImportResolves(URL base, String path,String expected) throws CdlResolutionException {
        ParseContext context = new ParseContext();
        CdlDocument parent=context.createRootDocument();
        parent.setDocumentURL(base);
        String actual= context.resolveRelativePath(parent, path);
        assertEquals(expected, actual);
    }

    public void testResolveHttpReference() throws Exception {
        assertImportResolves(null, EXAMPLE, EXAMPLE);
    }

    public void testResolveFileReference() throws Exception {
        assertImportResolves(null, FILE, FILE);
    }

    public void testResolveRelativeHttpReference() throws Exception {
        URL url = new URL(EXAMPLE);
        assertImportResolves(url, FILENAME, EXAMPLE + FILENAME);
    }

    public void testResolveRelativeFileReference() throws Exception {
        URL url=new URL(FILE);
        assertImportResolves(url, FILENAME, FILE+FILENAME);
    }

    public void testResolveParentFileReference() throws Exception {
        URL url = new URL(FILE+"subdir/");
        assertImportResolves(url, "../", FILE );
    }

    public void testResolveParentHttpReference() throws Exception {
        URL url = new URL(EXAMPLE + "subdir/");
        assertImportResolves(url, "../", EXAMPLE);
    }

}
