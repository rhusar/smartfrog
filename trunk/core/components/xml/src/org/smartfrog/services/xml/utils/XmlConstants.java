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
package org.smartfrog.services.xml.utils;

/**
 * General definition of various XML constants, usually parser options but also
 * classnames and things. Somewhat Xerces-specific. 
 * created 17-May-2005 15:14:18
 */


public interface XmlConstants {
    /**
     * parser of choice is Apache Xerces; fallback is Sun xerces. {@value}
     */

    final static String PARSER_XERCES = "org.apache.xerces.parsers.SAXParser";
    /**
     * what ships with Java1.5 {@value}
     */
    final static String PARSER_JAVA_15 = "com.sun.org.apache.xerces.internal.parsers.SAXParser";
    final static String XERCES_XSD = "http://apache.org/xml/features/validation/schema";
    final static String XERCES_XSD_FULLCHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    final static String XERCES_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
    final static String XERCES_DOCTYPES = "http://apache.org/xml/features/disallow-doctype-decl";
    final static String SAX_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    /**
     * ask for secure XML prarsing
     * {@value}
     */
    final static String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";
}
