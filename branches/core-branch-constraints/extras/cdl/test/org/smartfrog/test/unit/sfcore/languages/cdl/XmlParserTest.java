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

import org.smartfrog.services.xml.utils.ParserHelper;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.services.xml.utils.XmlConstants;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * created Aug 12, 2004 1:39:59 PM
 */

public class XmlParserTest extends XmlTestBase {
    public static final String PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    public XmlParserTest(String s) {
        super(s);
    }

    public void testXercesIsPresent() throws SAXException {
        createXerces();
    }

    public void testXercesHandlesSchema() throws SAXException {
        XMLReader xerces;
        xerces = createXerces();
        xerces.setFeature("http://apache.org/xml/features/validation/schema",
                true);
    }

    private XMLReader createXerces() throws SAXException {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader(PARSER_NAME);
        assertNotNull(PARSER_NAME, xmlReader);
        return xmlReader;
    }

    /**
     * as crimson is so common, we skip this test
     *
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public void testSaxParserExists() throws SAXException,
            ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        assertNotNull("Sax parser factory", factory);
        SAXParser sax = factory.newSAXParser();
        assertNotNull("Sax parser ", sax);
        System.out.println("Sax 1 Parser=" + sax.getClass().getName());
    }

    public void testXercesHandlesOurCatalog() throws Exception {
        XMLReader xerces;
        xerces = createAndConfigureXerces();
        parse(xerces, CDL_DOC_MINIMAL);
    }

    public void testOurCatalogFails() throws Exception {
        XMLReader xerces;
        xerces = createAndConfigureXerces();
        parseToFailure(xerces, CDL_DOC_WRONG_NAMESPACE);
    }

    private XMLReader createAndConfigureXerces() throws SAXException,
            IOException {
        XMLReader xerces;
        xerces = createXerces();
        xerces.setFeature("http://apache.org/xml/features/validation/schema",
                true);
        ResourceLoader loader = new ResourceLoader(this.getClass());
        CdlCatalog catalog = new CdlCatalog(loader);
        catalog.bind(xerces);
        assertEquals(catalog, xerces.getEntityResolver());
        xerces.setFeature(XmlConstants.FEATURE_SAX_NAMESPACES, true);
        ParserHelper.enableXmlSchema(xerces);
        xerces.setFeature(XmlConstants.FEATURE_SAX_VALIDATION, true);
        return xerces;
    }

    public void NotestParserSetupCodeWorks() throws Exception {
        XMLReader xerces;
        xerces = ParserHelper.createXmlParser(true, true, true);
        parse(xerces, CDL_DOC_MINIMAL);
    }

    public void NotestParserSetupIsValidating() throws Exception {
        XMLReader xerces;
        xerces = ParserHelper.createXmlParser(true, true, true);
        parseToFailure(xerces, CDL_DOC_WRONG_ELT_ORDER);
    }

    private void parseToFailure(XMLReader xerces, String resource)
            throws IOException, SAXException {
        try {
            parse(xerces, resource);
            fail("Should have failed to parse " + resource);
        } catch (SAXException e) {
            //accept
        }
    }

    private void parse(XMLReader xerces, String resource) throws IOException,
            SAXException {
        ResourceLoader loader2 = new ResourceLoader(this.getClass());
        InputStream in = loader2.loadResource(resource);
        InputSource ins = new InputSource(in);
        SaxErrorHandler handler = new SaxErrorHandler();
        try {
            xerces.setErrorHandler(handler);
            xerces.parse(ins);
            handler.rethrow();
        } finally {
            in.close();
        }
    }

    protected static class SaxErrorHandler implements ErrorHandler {
        private SAXParseException fault;

        public void rethrow() throws SAXParseException {
            if (fault != null) {
                throw fault;
            }
        }

        public void fatalError(SAXParseException exception) {
            fault = exception;
        }

        /**
         * receive notification of a recoverable error
         *
         * @param exception the error
         */
        public void error(SAXParseException exception) {
            fault = exception;
        }

        public void warning(SAXParseException exception) {
        }

        private void doLog(SAXParseException e, int logLevel) {
        }

    }

}