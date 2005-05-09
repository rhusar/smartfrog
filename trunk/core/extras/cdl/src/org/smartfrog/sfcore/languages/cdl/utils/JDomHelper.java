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
package org.smartfrog.sfcore.languages.cdl.utils;

import org.jdom.input.SAXBuilder;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.services.xml.utils.ParserHelper;

/**
 * created 06-May-2005 11:26:29
 */

public class JDomHelper {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(ParserHelper.class);

    /**
     * ask for secure XML prarsing {@value}
     */
    private static final String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";
    /**
     * parser of choice is Apache Xerces; fallback is Sun xerces. {@value}
     */

    public static final String PARSER_XERCES = "org.apache.xerces.parsers.SAXParser";

    /**
     * what ships with Java1.5 {@value}
     */
    public static final String PARSER_JAVA_15 = "com.sun.org.apache.xerces.internal.parsers.SAXParser";
    public static final String XERCES_XSD = "http://apache.org/xml/features/validation/schema";
    public static final String XERCES_XSD_FULLCHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    public static final String XERCES_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
    public static final String XERCES_DOCTYPES = "http://apache.org/xml/features/disallow-doctype-decl";
    public static final String SAX_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";


    /**
     * Configure a created (and perhaps internally configured) SaxBuilder
     * @param builder   builder to configure
     * @param validate  enable validation of XSD types
     * @param disableDoctypes disable doctype declarations
     * @param secureLoading turn on security features (Xerces only)
     */
    public static void configureSaxBuilder(SAXBuilder builder, boolean validate,
            boolean disableDoctypes,
            boolean secureLoading) {
/*
        setFeature(builder,
                FEATURE_SECURE_PROCESSING,
                secureLoading);
*/
        setFeature(builder,
                XERCES_XSD,
                validate);
        setFeature(builder,
                XERCES_XSD_FULLCHECKING,
                validate);
        setFeature(builder,
                XERCES_URI_CONFORMANT,
                true);
        setFeature(builder,
                XERCES_DOCTYPES,
                disableDoctypes);
        setFeature(builder,
                SAX_GENERAL_ENTITIES,
                !secureLoading);
    }


    /**
     * set a feature on a builder; ignore exceptions raised
     * @param builder
     * @param name
     * @param flag
     */
    private static void setFeature(SAXBuilder builder, String name,
            boolean flag) {
        builder.setFeature(name, flag);
    }

}
