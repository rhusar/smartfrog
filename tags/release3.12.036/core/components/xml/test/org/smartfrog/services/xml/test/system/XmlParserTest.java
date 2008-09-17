/* (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xml.test.system;

import junit.framework.TestCase;
import org.smartfrog.services.xml.utils.ParserHelper;
import org.w3c.dom.DOMImplementation;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test some aspects of XMLL created 27-Jan-2005 14:05:29
 */

public class XmlParserTest extends TestCase {

    private static final Log logger= LogFactory.getLog(XmlParserTest.class);

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
        XMLReader xmlReader = ParserHelper.createBaseXercesInstance();
        assertNotNull("failed to create XML parser", xmlReader);
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
        SAXParser parser = factory.newSAXParser();
        assertNotNull("Sax parser ", parser);
        logger.info("Sax 1 Parser=" + parser.getClass().getName());
    }

    public void testDomExists() throws ParserConfigurationException {
        DOMImplementation domImplementation = ParserHelper.loadDomImplementation();
        assertNotNull("Dom implementation null", domImplementation);
    }
}
