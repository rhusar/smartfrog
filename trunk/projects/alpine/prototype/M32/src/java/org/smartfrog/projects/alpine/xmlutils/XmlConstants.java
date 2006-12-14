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
package org.smartfrog.projects.alpine.xmlutils;

/**
 * General definition of various XML constants, usually parser options but also
 * classnames and things. Somewhat Xerces-specific.
 * created 17-May-2005 15:14:18
 */


public interface XmlConstants {
    /**
     * parser of choice is Apache Xerces; fallback is Sun xerces. {@value}
     */

    String PARSER_XERCES = "org.apache.xerces.parsers.SAXParser";
    /**
     * what ships with Java1.5 {@value}
     */
    String PARSER_JAVA_15 = "com.sun.org.apache.xerces.internal.parsers.SAXParser";

    String FEATURE_XERCES_XSD = "http://apache.org/xml/features/validation/schema";

    String FEATURE_XERCES_XSD_FULLCHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    String FEATURE_XERCES_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
    String FEATURE_XERCES_DISALLOW_DOCTYPES = "http://apache.org/xml/features/disallow-doctype-decl";
    String FEATURE_SAX_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    /**
     * Java1.5+ option for secure XML parsing
     * {@value}
     */
    String FEATURE_JAXP_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";

    String PROPERTY_XERCES_SCHEMA_LOCATION =
            "http://apache.org/xml/properties/schema/external-schemaLocation";
    String PROPERTY_XERCES_NO_NAMESPACE_SCHEMA_LOCATION =
            "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

    String FEATURE_SAX_VALIDATION = "http://xml.org/sax/features/validation";
    String FEATURE_SAX_NAMESPACES = "http://xml.org/sax/features/namespaces";
    String FEATURE_JAXP12_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    String FEATURE_JAXP12_SCHEMA_SOURCE =
            "http://java.sun.com/xml/jaxp/properties/schemaSource";
    String URI_XSD =
            "http://www.w3.org/2001/XMLSchema";
}
