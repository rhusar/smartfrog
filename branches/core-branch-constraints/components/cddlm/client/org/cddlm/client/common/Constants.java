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
package org.cddlm.client.common;

/**
 * created Aug 31, 2004 4:37:08 PM
 */

public class Constants {

    private Constants() {
    }

    public static final String DEFAULT_HOST = "127.0.0.1";

    public static final String DEFAULT_PATH = "/axis/services/cddlm";

    public static final int DEFAULT_SERVICE_PORT = 5050;


    public static final String DEFAULT_PROTOCOL = "http";


    //======================================================
    //the set below are ripped from the org.smartfrog.services.cddlm.cdl.Constants class
    //the reason we keep them apart is simply to ensure that everything is standalone
    //now, as string constants are expanded in place (I think), this should not be an issue. 

    public static final String WS_ADDRESSING_NAMESPACE = "http://schemas.xmlsoap.org/ws/2003/03/addressing";
    public static final String CDDLM_XSD_FILENAME = "cddlm.xsd";
    public static final String DEPLOY_API_SCHEMA_FILENAME = "deployAPIschema.xsd";


    public static final String CDL_NAMESPACE = "http://gridforge.org/cddlm/xml/2004/07/30/";

    public static final String CDL_API_NAMESPACE = "http://gridforum.org/cddlm/serviceAPI/api/2004/07/30";

    public static final String CDL_ELT_CDL = "cdl";

    public static final String XPATH_URI = "http://www.w3.org/TR/1999/REC-xpath-19991116";

    public static final String SMARTFROG_NAMESPACE = "http://gridforge.org/cddlm/smartfrog/2004/07/30";

    public static final String FAULTS_NAMESPACE = "http://gridforge.org/cddlm/serviceAPI/faults/2004/07/30";

    public static final String ANT_NAMESPACE = "http://ant.apache.org/xsd/1.7";

    public static final String OPTION_VALIDATE_ONLY = "http://gridforum.org/cddlm/serviceAPI/options/validateOnly/2004/07/30";

    public static final String OPTION_PROPERTIES = "http://gridforum.org/cddlm/serviceAPI/options/propertyMap/2004/07/30";

    public static final String SMARTFROG_ELEMENT_NAME = "smartfrog";

    public static final String SMARTFROG_ELEMENT_VERSION_ATTR = "version";

    //======================================================

}
