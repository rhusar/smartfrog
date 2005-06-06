/** (C) Copyright 2004-2005 Hewlett-Packard Development Company, LP

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


package org.smartfrog.sfcore.languages.cdl;

import org.ggf.cddlm.generated.api.CddlmConstants;

/**
 * Date: 15-Jul-2004 Time: 22:26:34
 */
public class Constants {

    private Constants() {
    }



    /**
     * full path to CDDLM
     * {@value}
     * */
    public static final String CDL_XSD_FILENAME = CddlmConstants.CDL_FILENAME_XML_CDL;

    public static final String DEPLOY_API_SCHEMA_FILENAME = 
            CddlmConstants.CDL_FILENAME_DEPLOYMENT_API;

    public static final String XML_CDL_NAMESPACE = CddlmConstants.XML_CDL_NAMESPACE;

    public static final String CDL_API_TYPES_NAMESPACE =  CddlmConstants.CDL_API_TYPES_NAMESPACE;


    public static final String CDL_ELT_CDL = "cdl";

    public static final String XPATH_URI = "http://www.w3.org/TR/1999/REC-xpath-19991116";

    public static final String SMARTFROG_NAMESPACE = CddlmConstants.SMARTFROG_NAMESPACE;


    public static final String OPTION_VALIDATE_ONLY = CddlmConstants.OPTION_VALIDATE_ONLY;

    public static final String OPTION_PROPERTIES = CddlmConstants.OPTION_PROPERTIES;

    public static final String SMARTFROG_ELEMENT_NAME = "smartfrog";

    public static final String SMARTFROG_ELEMENT_VERSION_ATTR = "version";
}
