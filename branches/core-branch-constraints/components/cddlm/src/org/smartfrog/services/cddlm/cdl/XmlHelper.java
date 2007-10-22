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
package org.smartfrog.services.cddlm.cdl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * created Aug 13, 2004 2:39:09 PM
 */

public class XmlHelper {

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(XmlHelper.class);

    public static final String PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    /**
     * create our XML parser. We are relying on xerces here, and will fail if it
     * is not found.
     *
     * @param validate
     * @return
     * @throws org.xml.sax.SAXException
     */
    public static XMLReader createXmlParser(boolean validate)
            throws SAXException {
        XMLReader xerces = null;
        try {
            xerces = XMLReaderFactory.createXMLReader(PARSER_NAME);
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
                    true);
            setFeature(xerces,
                    "http://xml.org/sax/features/external-general-entities",
                    false);
        } catch (SAXException e) {
            throw e;
        }
        return xerces;
    }

    /**
     * set a feature on a parser, log any failure but continue This helps us get
     * past variants in xerces version on the old classpath
     *
     * @param xerces
     * @param name
     * @param validate
     */
    private static void setFeature(XMLReader xerces, String name,
            boolean validate) {
        try {
            xerces.setFeature(name,
                    validate);
        } catch (SAXNotRecognizedException e) {
            log.error("SAXNotRecognizedException seting " + name);
        } catch (SAXNotSupportedException e) {
            log.error("SAXNotSupportedException seting " + name);
        }
    }

}
