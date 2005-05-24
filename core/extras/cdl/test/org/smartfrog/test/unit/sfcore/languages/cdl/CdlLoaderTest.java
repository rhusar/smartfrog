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

import junit.framework.TestCase;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.sfcore.languages.cdl.CdlParser;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.xml.sax.SAXException;

import java.io.IOException;

import nu.xom.ParsingException;

/**
 * Junit test cause
 *
 * @author root
 */
public class CdlLoaderTest extends XmlTestBase {

    CdlParser laxParser;
    CdlParser parser;


    public CdlLoaderTest(String test) {
        super(test);
    }

    /**
     * The fixture set up called before every test method.
     */
    protected void setUp() throws Exception {
        initParsers();
    }

    private void initParsers() throws SAXException {
        ResourceLoader loader = new ResourceLoader(this.getClass());
        laxParser = new CdlParser(loader, false);
        parser = new CdlParser(loader, true);
    }


    public void testValid() throws Exception {
        initParsers();
        for (int i = 0; i < VALID_CDL.length; i++) {
            assertValid(VALID_CDL[i]);
        }
    }


    protected void assertInvalid(String filename, String text) throws Exception {
        try {
            if (text == null) {
                text = "";
            }
            CdlDocument doc = load(filename);
            doc.validate();
            fail("expected a validity failure with " + text);
        } catch (ParsingException e) {
            if (e.getMessage().indexOf(text) < 0) {
                log("expected [" + text + "] but got " + e.toString());
                throw e;
            }
        }
    }

    private void loading(String filename) {
        log(filename);
    }

    private void log(String message) {
        System.out.println(message);
    }


    protected void assertValid(String filename) throws Exception {
        CdlDocument doc = load(filename);
        doc.validate();
    }

    protected CdlDocument load(String filename) throws IOException,
            ParsingException {
        CdlDocument doc;
        loading(filename);
        doc = parser.parseResource(filename);
        return doc;
    }

    public void testWrongDocNamespace() throws Exception {
        assertInvalid(CDL_DOC_WRONG_NAMESPACE, WRONG_NAMESPACE_TEXT);
    }

    public void testWrongEltOrder() throws Exception {
        assertInvalid(CDL_DOC_WRONG_ELT_ORDER, null);
    }

    public void testWrongRootEltType() throws Exception {
        assertInvalid(CDL_DOC_WRONG_ROOT_ELT_TYPE,
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
            assertInvalid("no-such-document.cdl", "Not found");
        } catch (IOException e) {
            //expected
        }
    }


}