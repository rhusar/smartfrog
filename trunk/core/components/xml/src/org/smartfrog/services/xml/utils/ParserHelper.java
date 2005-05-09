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
     * ask for secure XML prarsing
     * {@value}
     */
    private static final String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";
    /**
     * parser of choice is Apache Xerces; fallback is Sun xerces.
     * {@value}
     */

    public static final String PARSER_XERCES = "org.apache.xerces.parsers.SAXParser";

    /**
     * what ships with Java1.5
     * {@value}
     */
    public static final String PARSER_JAVA_15 = "com.sun.org.apache.xerces.internal.parsers.SAXParser";
    public static final String XERCES_XSD = "http://apache.org/xml/features/validation/schema";
    public static final String XERCES_XSD_FULLCHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    public static final String XERCES_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
    public static final String XERCES_DOCTYPES = "http://apache.org/xml/features/disallow-doctype-decl";
    public static final String SAX_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

    /**
     * create our XML parser. We are relying on xerces here, and will fail if it
     * is not found.
     *
     * @param validate flag to turn validation on
     * @param disableDoctypes flag to disable doctypes
     * @param secureLoading flag for secure loading (disables entity expansion)
     * @return an appropriately configured XML reader
     * @throws SAXException
     */
    public static XMLReader createXmlParser(boolean validate,
            boolean disableDoctypes,
            boolean secureLoading)
            throws SAXException {
        
        XMLReader xerces = createBaseXercesInstance();
        setFeature(xerces,
                FEATURE_SECURE_PROCESSING,
                secureLoading);
        setFeature(xerces,
                XERCES_XSD,
                validate);
        setFeature(xerces,
                XERCES_XSD_FULLCHECKING,
                validate);
        setFeature(xerces,
                XERCES_URI_CONFORMANT,
                true);
        setFeature(xerces,
                XERCES_DOCTYPES,
                disableDoctypes);
        setFeature(xerces,
                SAX_GENERAL_ENTITIES,
                !secureLoading);
        return xerces;
    }

    /**
     * create Xerces. look first for xerces, then for the sun version
     *
     * @return a copy of Xerces
     * @throws SAXException if neither implementation coudl be loaded
     */
    public static XMLReader createBaseXercesInstance() throws SAXException {
        XMLReader xerces = null;
        try {
            xerces = XMLReaderFactory.createXMLReader(PARSER_XERCES);
        } catch (SAXException e) {
            log.debug("Failed to find Xerces", e);
            xerces = XMLReaderFactory.createXMLReader(PARSER_JAVA_15);
        }
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
