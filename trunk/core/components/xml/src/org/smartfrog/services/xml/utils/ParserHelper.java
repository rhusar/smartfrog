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
package org.smartfrog.services.xml.utils;

import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.w3c.dom.DOMImplementation;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * XML support. Only for use with Xerces. created 26-Jan-2005 16:18:18
 */

public class ParserHelper {

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(ParserHelper.class);

    /**
     * parser of choice is Apache Xerces; fallback is Sun xerces.
     */

    public static final String XERCES_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    /**
     * what ships with Java1.5
     */
    public static final String SUN_PARSER_NAME = "com.sun.apache.xerces.parsers.SAXParser";

    /**
     * create our XML parser. We are relying on xerces here, and will fail if it
     * is not found.
     *
     * @param validate
     * @return
     * @throws org.xml.sax.SAXException
     */
    public static XMLReader createXmlParser(boolean validate,
            boolean doctypes,
            boolean entities)
            throws SAXException {
        XMLReader xerces = null;
        try {
            xerces = XMLReaderFactory.createXMLReader(XERCES_PARSER_NAME);
        } catch (SAXException e) {
            xerces = XMLReaderFactory.createXMLReader(SUN_PARSER_NAME);

        }
        setFeature(xerces,
                "http://apache.org/xml/features/validation/schema",
                validate);
        setFeature(xerces,
                "http://apache.org/xml/features/validation/schema-full-checking",
                validate);
        setFeature(xerces,
                "http://apache.org/xml/features/standard-uri-conformant",
                true);
        setFeature(xerces,
                "http://apache.org/xml/features/disallow-doctype-decl",
                doctypes);
        setFeature(xerces,
                "http://xml.org/sax/features/external-general-entities",
                entities);
        return xerces;
    }

    /**
     * set a feature on a parser, log any failure but continue This helps us to
     * get past variants in xerces version on the classpath
     *
     * @param parser parser instance
     * @param name   feature name
     * @param flag   flag to set/reset
     */
    private static void setFeature(XMLReader parser, String name,
            boolean flag) {
        try {
            parser.setFeature(name, flag);
        } catch (SAXNotRecognizedException e) {
            log.error("SAXNotRecognizedException setting " + name);
        } catch (SAXNotSupportedException e) {
            log.error("SAXNotSupportedException setting " + name);
        }
    }

    /**
     * use the JAXP APIs to locate and bind to a parser
     *
     * @return
     * @throws ParserConfigurationException
     */
    public static DOMImplementation loadDomImplementation()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        return impl;
    }
}
