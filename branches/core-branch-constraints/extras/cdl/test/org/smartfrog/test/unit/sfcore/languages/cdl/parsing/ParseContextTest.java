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

import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlDuplicatePrototypeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.sfcore.languages.cdl.importing.ClasspathResolver;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

/**
 * created 10-Jun-2005 15:48:34
 */

public class ParseContextTest extends XmlTestBase {

    public ParseContextTest(String name) {
        super(name);
    }

    public void testContext() throws Exception {
        ParseContext context = new ParseContext(new ClasspathResolver(), null);
        CdlDocument doc = new CdlDocument();
        doc.setParseContext(context);
        assertEquals(context, doc.getParseContext());
    }

    /**
     * test that we can create a doc
     *
     * @throws Exception
     */
    public void testDocCreation() throws Exception {
        ParseContext context = new ParseContext(new ClasspathResolver(), null);
        assertNotNull(context.getImportResolver());
        CdlDocument doc = context.createRootDocument();
        assertNotNull(doc);
        assertEquals(context, doc.getParseContext());
        assertEquals(doc, context.getDocument());
    }

    //obsolete w/ integral binding of qname and element name
    public void ObsoletetestNullPrototype() throws Exception {
        ParseContext context = new ParseContext();
        PropertyList prototype = new PropertyList("empty");
        try {
            context.prototypeAddNew(prototype);
            fail("expected a fault");
        } catch (CdlRuntimeException e) {
            //success
        }
    }

    public void testDuplicatePrototype() throws Exception {
        ParseContext context = new ParseContext();
        PropertyList prototype = new PropertyList("smartfrog",
                Constants.XMLNS_SMARTFROG);
        try {
            context.prototypeAddNew(prototype);
            context.prototypeAddNew(prototype);
            fail("expected a fault");
        } catch (CdlDuplicatePrototypeException e) {
            //success
        }
    }


    public void testUpdatePrototype() throws Exception {
        ParseContext context = new ParseContext();
        PropertyList prototype = new PropertyList("smartfrog",
                Constants.XMLNS_SMARTFROG);
        context.prototypeAddNew(prototype);
        context.prototypeUpdate(prototype);
    }
}
