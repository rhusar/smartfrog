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
import nu.xom.Element;
import nu.xom.ParsingException;
import org.apache.axis.message.MessageElement;
import org.smartfrog.services.cddlm.cdl.CdlDocument;
import org.smartfrog.services.cddlm.cdl.CdlParser;
import org.smartfrog.services.cddlm.cdl.ResourceLoader;
import org.smartfrog.services.cddlm.cdl.XomAxisHelper;
import org.w3c.dom.DOMImplementation;

import javax.xml.parsers.ParserConfigurationException;
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

    public static final String WRONG_NAMESPACE_TEXT="Cannot find the declaration of element 'cdl:cdl'";
    public static final String CDL_DOC_MINIMAL = VALID_RESOURCES+"minimal.cdl";
    private final static String VALID_CDL[]= {
        CDL_DOC_MINIMAL
    };
    public static final String CDL_DOC_WRONG_ELT_ORDER = INVALID_RESOURCES+"wrong_elt_order.cdl";
    public static final String CDL_DOC_WRONG_ROOT_ELT_TYPE = INVALID_RESOURCES + "wrong_root_elt_type.cdl";
    public static final String CDL_DOC_DUPLICATE_NAMES = INVALID_RESOURCES + "duplicate-names.cdl";
    public static final String CDL_DOC_WRONG_NAMESPACE = INVALID_RESOURCES + "wrong_doc_namespace.cdl";


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
            CdlDocument doc=load(filename);
            doc.validate();
            fail("expected a validity failure with "+text);
        } catch (ParsingException e) {
            if(e.getMessage().indexOf(text)<0) {
                log("expected "+text+" but got "+e.toString());
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


    protected void assertValid(String filename) throws IOException, ParsingException {
        CdlDocument doc = load(filename);
        doc.validate();
    }

    protected CdlDocument load(String filename) throws IOException,
            ParsingException {
        CdlDocument doc;
        loading(filename);
        doc = parser.parseResource( filename);
        return doc;
    }

    public void testWrongDocNamespace() throws Exception {
        assertInvalid(CDL_DOC_WRONG_NAMESPACE, WRONG_NAMESPACE_TEXT);
    }

/*
    public void testUnsupportedPathLanguage() throws Exception {
        assertInvalid("unsupported_pathlanguage.cdl", CdlDocument.ERROR_BAD_PATHLANGUAGE);
    }
*/
    public void testWrongEltOrder() throws Exception {
        assertInvalid(CDL_DOC_WRONG_ELT_ORDER, null);
    }

    public void testWrongRootEltType() throws Exception {
        assertInvalid(CDL_DOC_WRONG_ROOT_ELT_TYPE, CdlDocument.ERROR_WRONG_ROOT_ELEMENT);
    }

    public void testDuplicateNames() throws Exception {
        CdlDocument doc = load(CDL_DOC_DUPLICATE_NAMES);
        doc.validate();
    }

    public void testMissingFile() throws Exception  {
        try {
            assertInvalid("no-such-document.cdl","Not found");
        } catch (IOException e) {
            //expected
        }
    }

    public void testAxisConversionValid() throws Exception {
        CdlDocument doc=load(CDL_DOC_MINIMAL);
        doc.validate();
        assertConversionWorks(doc);

    }

    private void assertConversionWorks(CdlDocument doc)
            throws ParserConfigurationException {
        DOMImplementation impl = XomAxisHelper.loadDomImplementation();
        Document dom = doc.getDocument();
        MessageElement messageElement = XomAxisHelper.convert(dom, impl);
        assertNotNull(messageElement);
        Element rootElement = dom.getRootElement();
        assertEquals("namespaces match",rootElement.getNamespaceURI(),
                messageElement.getNamespaceURI());
        String n2 = messageElement.getName();
        String n1 = rootElement.getLocalName();
        assertEquals("element names",n1,n2);
    }
}