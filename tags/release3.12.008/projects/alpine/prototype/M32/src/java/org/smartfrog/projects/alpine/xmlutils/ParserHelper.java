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
package org.smartfrog.projects.alpine.xmlutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public final class ParserHelper implements XmlConstants {

    private ParserHelper() {
    }

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(ParserHelper.class);

    /**
     * create our XML parser. We are relying on xerces here, and will fail if it
     * is not found.
     *
     * @param validate        flag to turn validation on
     * @param disableDoctypes flag to disable doctypes
     * @param secureLoading   flag for secure loading (disables entity expansion)
     * @return an appropriately configured XML reader
     * @throws org.xml.sax.SAXException if something went very wrong
     */
    public static XMLReader createXmlParser(boolean validate,
                                            boolean disableDoctypes,
                                            boolean secureLoading)
            throws SAXException {

        XMLReader xerces = createBaseXercesInstance();
        setFeatures(xerces, secureLoading, validate, disableDoctypes);
        return xerces;
    }

    public static void setFeatures(XMLReader xerces,
                                   boolean secureLoading,
                                   boolean validate, boolean disableDoctypes) {
/*        setFeature(xerces,
                FEATURE_SECURE_PROCESSING,
                secureLoading);*/
        if (validate) {
            enableXmlSchema(xerces);
        }
        setFeature(xerces,
                FEATURE_SAX_NAMESPACES,
                true);
        setFeature(xerces,
                FEATURE_SAX_VALIDATION,
                validate);
/*        setFeature(xerces,
                FEATURE_XERCES_URI_CONFORMANT,
                true);*/
/*        setFeature(xerces,
                FEATURE_XERCES_DISALLOW_DOCTYPES,
                disableDoctypes);
        setFeature(xerces,
                FEATURE_SAX_GENERAL_ENTITIES,
                !secureLoading);*/
    }

    /**
     * turn XSD support on (xerces-specific)
     *
     * @param xerces the parser
     */
    public static void enableXmlSchema(XMLReader xerces) {
        setFeature(xerces,
                FEATURE_XERCES_XSD,
                true);
        setFeature(xerces,
                FEATURE_XERCES_XSD_FULLCHECKING,
                true);
    }

    /**
     * create Xerces. look first for xerces, then for the sun version
     *
     * @return a copy of Xerces
     * @throws org.xml.sax.SAXException if neither implementation coudl be loaded
     */
    public static XMLReader createBaseXercesInstance() throws SAXException {
        XMLReader xerces;
        try {
            xerces = XMLReaderFactory.createXMLReader(PARSER_XERCES);
        } catch (SAXException e) {
            log.info("Failed to find Xerces; using the Java 1.5+ parser");
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
    public static void setFeature(XMLReader parser, String name,
                                  boolean flag) {
        try {
            parser.setFeature(name, flag);
        } catch (SAXNotRecognizedException ignored) {
            log.debug("SAXNotRecognizedException setting " + name);
        } catch (SAXNotSupportedException ignored) {
            log.debug("SAXNotSupportedException setting " + name);
        }
    }

    /**
     * use the JAXP APIs to locate and bind to a parser
     *
     * @return a new instance (somehow)
     * @throws javax.xml.parsers.ParserConfigurationException if it refuses to be
     *
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
