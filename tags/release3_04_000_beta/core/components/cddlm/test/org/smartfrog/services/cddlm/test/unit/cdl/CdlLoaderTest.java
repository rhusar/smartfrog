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

package org.smartfrog.services.cddlm.test.unit.cdl;

import junit.framework.TestCase;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.smartfrog.services.cddlm.cdl.CdlDocument;
import org.smartfrog.services.cddlm.cdl.CdlParser;
import org.smartfrog.services.cddlm.cdl.ResourceLoader;

import java.io.IOException;

/**
 * Junit test cause
 * 
 * @author root
 */
public class CdlLoaderTest extends TestCase {

    CdlParser laxParser;
    CdlParser parser;

    private final static String RESOURCES="files/cdl/";
    private final static String INVALID_RESOURCES = RESOURCES+"invalid/";
    private final static String VALID_RESOURCES = RESOURCES + "valid/";

    private final static String VALID_CDL[]= {
        "minimal.cdl"
    };


    public CdlLoaderTest(String test) {
        super(test);
    }

    /**
     * The fixture set up called before every test method.
     */
    protected void setUp() throws Exception {
        ResourceLoader loader=new ResourceLoader(this.getClass());
        laxParser=new CdlParser(loader,false);
        parser = new CdlParser(loader, true);
    }

    /**
     * The fixture clean up called after every test method.
     */

    protected void tearDown() throws Exception {
    }

    public void testValid() throws Exception {
        Document doc;
        for(int i=0;i<VALID_CDL.length;i++) {
            assertValid(VALID_CDL[i]);
        }
    }


    protected void assertInvalid(String filename,String text) throws IOException, ParsingException {
        try {
            if(text==null) {
                text="";
            }
            loading(filename);
            CdlDocument doc=parser.parseResource(INVALID_RESOURCES+filename);
        } catch (ParsingException e) {
            if(e.getMessage().indexOf(text)<0) {
                throw e;
            }
        }
    }

    private void loading(String filename) {
        System.out.println(filename);
    }

    protected void assertValid(String filename) throws IOException, ParsingException {
        CdlDocument doc;
        loading(filename);
        doc=parser.parseResource(VALID_RESOURCES+filename);
    }

    public void testWrongDocNamespace() throws Exception {
        assertInvalid("wrong_doc_namespace.cdl",CdlDocument.ERROR_WRONG_NAMESPACE);
    }


    public void testUnsupportedPathLanguage() throws Exception {
        assertInvalid("unsupported_pathlanguage.cdl", CdlDocument.ERROR_BAD_PATHLANGUAGE);
    }

    public void testWrongEltOrder() throws Exception {
        assertInvalid("wrong_elt_order.cdl", null);
    }

    public void testWrongRootEltType() throws Exception {
        assertInvalid("wrong_root_elt_type.cdl", CdlDocument.ERROR_WRONG_ROOT_ELEMENT);
    }


}