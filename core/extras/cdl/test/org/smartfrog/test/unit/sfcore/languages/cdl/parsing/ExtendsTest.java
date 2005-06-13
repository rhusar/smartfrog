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

import nu.xom.ParsingException;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRecursiveExtendsException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.sfcore.languages.cdl.resolving.ExtendsContext;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.EmptyStackException;

/**
 * created 10-Jun-2005 16:53:50
 */

public class ExtendsTest extends XmlTestBase {

    ExtendsContext extendsContext = new ExtendsContext();
    public static final QName PROPERTYLIST = new QName(
            Constants.CDL_API_TYPES_NAMESPACE,
            "propertylist");
    public static final QName LOCALONLY = new QName("propertylist");


    public ExtendsTest(String name) {
        super(name);
    }

    public void testExtendsIsExtracted() throws IOException, CdlException,
            ParsingException {
        CdlDocument cdlDocument = loadValidCDL(CDL_DOC_EXTENDS_1);

    }

    public void testExtendsContextWorks() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        extendsContext.enter(LOCALONLY);
        extendsContext.exit(LOCALONLY);
        extendsContext.exit(PROPERTYLIST);
        assertEquals(0, extendsContext.depth());
    }

    public void testExtendsContextRecursion() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        extendsContext.enter(LOCALONLY);
        try {
            extendsContext.enter(PROPERTYLIST);
            fail("expected error");
        } catch (CdlRecursiveExtendsException e) {
            //success!
        }
    }

    public void testExtendsContextRecursion2() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        extendsContext.enter(LOCALONLY);
        try {
            extendsContext.enter(LOCALONLY);
            fail("expected error");
        } catch (CdlRecursiveExtendsException e) {
            //success!
        }
    }

    public void testExtendsContextWrongExit() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        try {
            extendsContext.exit(LOCALONLY);
            fail("expected error");
        } catch (CdlRuntimeException e) {
            //success!
        }
    }

    public void testExtendsContextEmptyPop() throws Exception {
        try {
            extendsContext.exit(LOCALONLY);
            fail("expected error");
        } catch (EmptyStackException e) {
            //success!
        }
    }

    public void testExtendsContextNullDoesntNPE() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        try {
            extendsContext.exit(null);
            fail("expected error");
        } catch (CdlRuntimeException e) {
            //success!
        }
    }

    public void testDuplicatePrototypes() throws Exception {
        assertInvalidCDL(CDL_DOC_DUPLICATE_NAME,
                ParseContext.ERROR_DUPLICATE_PROTOTYPE);
    }

    public void testDirectLoop() throws Exception {
        assertInvalidCDL(CDL_DOC_DIRECT_LOOP,
                ExtendsContext.ERROR_RECURSING);
    }

    public void testIndirectLoop() throws Exception {
        assertInvalidCDL(CDL_DOC_INDIRECT_LOOP,
                ExtendsContext.ERROR_RECURSING);
    }

    public void testBadReference() throws Exception {
        assertInvalidCDL(CDL_DOC_BAD_REFERENCE,
                "Unknown reference");
    }

}
