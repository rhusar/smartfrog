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
package org.smartfrog.test.unit.sfcore.languages.cdl;

import junit.framework.Assert;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.utils.ResourceLoader;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import org.smartfrog.sfcore.languages.cdl.CdlParser;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * Class to help parsing; XmlTestBase delegates stuff here
 * created 25-Nov-2005 15:33:28
 */

public class DocumentTestHelper extends Assert {
    public DocumentTestHelper() {
    }

    /**
     * create a parser as we go
     * @param validating is the parser validating?
     * @throws SAXException
     */
    public DocumentTestHelper(boolean validating) throws SAXException {
        initParser(validating);
    }

    private CdlParser parser;

    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * configure the parser
     *
     * @throws org.xml.sax.SAXException
     */
    public void initParser(boolean validate) throws SAXException {
        ResourceLoader loader = new ResourceLoader(this.getClass());
        ParseContext context = new ParseContext(null,loader);
        parser = new CdlParser(context, validate);
    }


    public CdlParser getParser() {
        return parser;
    }

    /**
     * create a new catalog, using the local classloader for resolution
     *
     * @return the catalog
     */
    public CdlCatalog createCatalog() {
        ResourceLoader loader;
        loader = new ResourceLoader(this.getClass());
        return new CdlCatalog(loader);
    }

    private void loading(String filename) {
        log(filename);
    }

    protected void log(String message) {
        log.info(message);
    }

    public CdlDocument load(String filename) throws IOException,
            ParsingException, CdlException {
        CdlDocument doc;
        loading(filename);
        doc = parser.parseResource(filename);
        return doc;
    }

    public CdlDocument load(org.w3c.dom.Document dom) throws CdlException, ParsingException {
        return parser.parseDom(dom);
    }

    /**
     * assert that a resource loads as invalid CDL
     *
     * @param resource resource to load
     * @param text     text to look for in the exception (ParsingException or
     *                 CdlParsingException only)
     * @throws Exception for any other type of exception thrown during
     *                   load/parse
     */
    public void assertInvalidCDL(String resource, String text)
            throws Exception {
        ParseContext context=new ParseContext();
        assertInvalidCDL(context, resource, text);
    }

    /**
     * assert that a resource loads as invalid CDL
     *
     * @param resource resource to load
     * @param text     text to look for in the exception (ParsingException or
     *                 CdlParsingException only)
     * @throws Exception for any other type of exception thrown during
     *                   load/parse
     */
    public void assertInvalidCDL(ParseContext context,
                                    String resource,
                                    String text) throws Exception {
        try {
            if (text == null) {
                text = "";
            }
            CdlDocument doc = load(resource);
            doc.parse(context);
            fail("expected a validity failure with " + text);
        } catch (Exception e) {
            if (e.getMessage().indexOf(text) < 0) {
                log("expected [" + text + "] but got " + e.toString());
                throw e;
            }
        }
    }

    /**
     * load a file; assert that it is valid. This parses the doc as well as
     * loading it.
     *
     * @param resource
     * @throws IOException
     * @throws ParsingException
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException
     *
     */
    public void assertValidCDL(String resource) throws IOException,
            ParsingException, CdlException {
        ParseContext context = new ParseContext();
        parseValidCDL(context, resource);
    }

    /**
     * load a file; assert that it is valid by initiating parsing
     *
     * @param context
     * @param resource
     * @return a parsed document
     * @throws IOException
     * @throws ParsingException
     * @throws CdlException
     */
    public CdlDocument parseValidCDL(ParseContext context, String resource)
            throws IOException,
            ParsingException, CdlException {
        CdlDocument cdlDocument = loadValidCDL(resource);
        cdlDocument.parse(context);
        return cdlDocument;
    }

    /**
     * load a file; assert that it is valid by initiating parsing
     *
     * @param resource
     * @return a parsed document
     * @throws IOException
     * @throws ParsingException
     * @throws CdlException
     */
    public CdlDocument parseValidCDL(String resource)
            throws IOException,
            ParsingException, CdlException {
        ParseContext context = new ParseContext();
        return parseValidCDL(context,resource);
    }


    /**
     * Load a valid CDL document
     *
     * @param resource
     * @return the loaded document
     */
    public CdlDocument loadValidCDL(String resource) throws IOException,
            ParsingException, CdlException {
        CdlDocument doc = load(resource);
        return doc;
    }



    /**
     * Load a valid CDL document and build the DOM, but do not apply any other
     * phases
     *
     * @param resource
     * @return the document, built up but without imports, expands or other
     *         operations
     */
    public CdlDocument loadCDLToDOM(String resource) throws IOException,
            ParsingException, CdlException {
        CdlDocument doc = load(resource);
        ParseContext context = new ParseContext();
        doc.setParseContext(context);
        doc.parsePhaseBuildDom();
        return doc;
    }

    /**
     * Assert that a template has an attribute
     *
     * @param template
     * @param local
     */
    public static void assertHasAttribute(PropertyList template, String local) {
        assertHasAttribute(template, "", local);
    }

    /**
     * Assert that a template has an attribute
     *
     * @param template
     * @param local
     */
    public static void assertHasAttribute(PropertyList template,
                                      String namespace,
                                      String local) {
        assertTrue("Template " + template + " lacks the attribute " + local,
                template.hasAttribute(namespace, local));
    }

    /**
     * Assert that a template has an attribute of a given value
     *
     * @param template
     * @param namespace
     * @param local
     * @param expected
     */
    public static void assertAttributeValueEquals(PropertyList template,
                                              String namespace,
                                              String local,
                                              String expected) {
        Attribute attribute = template.getAttribute(local, namespace);
        assertNotNull("Template " + template + " lacks the attribute " + local,
                attribute);
        assertEquals(expected, attribute.getValue());
    }

    /**
     * Assert that a template has an attribute of a given value
     *
     * @param template
     * @param local
     * @param expected
     */
    public static void assertAttributeValueEquals(PropertyList template,
                                              String local,
                                              String expected) {
        assertAttributeValueEquals(template, "", local, expected);
    }

    public static void assertElementValueEquals(Element e, String value) {
        assertNotNull("Null element", e);
        String actual = e.getValue();
        assertEquals("Element " +
                e +
                " has value [" +
                actual +
                "] and not the expected value [" + value + "]",
                value, actual);
    }

    public static void assertElementTextContains(Element element,
                                             String search) {
        String value = element.getValue();
        assertTrue("Not found: [" + search + "] in [" + value + "]",
                value.indexOf(search) >= 0);
    }

    /**
     * looku up a component; thro
     *
     * @param doc
     * @param localname
     */
    public static PropertyList lookup(CdlDocument doc, String localname) {
        PropertyList template = doc.lookup(new QName(localname));
        assertNotNull("Lookup failed for element name " + localname, template);
        return template;
    }

}
