/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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

import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;

/**
 * Date: 15-Jul-2004 Time: 22:26:34
 */
public class Constants {

    private Constants() {
    }

    public static final String CDDLM_XSD_FILENAME = "cddlm.xsd";
    public static final String DEPLOY_API_SCHEMA_FILENAME = "deployAPIschema.xsd";


    public static final String CDL_NAMESPACE = DeployApiConstants.XML_CDL_NAMESPACE;

    public static final String CDL_API_NAMESPACE =  DeployApiConstants.CDL_API_NAMESPACE;


    public static final String CDL_ELT_CDL = "cdl";

    public static final String XPATH_URI = "http://www.w3.org/TR/1999/REC-xpath-19991116";

    public static final String SMARTFROG_NAMESPACE = DeployApiConstants.SMARTFROG_NAMESPACE;


    public static final String OPTION_VALIDATE_ONLY = DeployApiConstants.OPTION_VALIDATE_ONLY;

    public static final String OPTION_PROPERTIES = DeployApiConstants.OPTION_PROPERTIES;

    public static final String SMARTFROG_ELEMENT_NAME = "smartfrog";

    public static final String SMARTFROG_ELEMENT_VERSION_ATTR = "version";
}
