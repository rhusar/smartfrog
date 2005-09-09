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
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

/**
 * Tests that rely on import working. created 15-Jun-2005 16:02:30
 */

public class ImportTest extends XmlTestBase {

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
    
}
