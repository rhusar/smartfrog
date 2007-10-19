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
package org.smartfrog.services.cddlm.api;

import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;

/**
 * created Aug 4, 2004 10:34:47 AM
 */

public class Constants {
    public static final String[] LANGUAGES = {
        "SmartFrog", "1.0", DeployApiConstants.SMARTFROG_NAMESPACE,
        "XML-CDL", "0.3", DeployApiConstants.XML_CDL_NAMESPACE,
//        "Apache Ant", "1.7", DeployApiConstants.ANT_NAMESPACE
    };

    public static final String[] LANGUAGE_NAMESPACES = {
        DeployApiConstants.SMARTFROG_NAMESPACE,
        DeployApiConstants.XML_CDL_NAMESPACE,
        DeployApiConstants.ANT_NAMESPACE
    };

    /**
     * these consts must match the position above
     */
    public static final int LANGUAGE_UNKNOWN = -1;
    public static final int LANGUAGE_SMARTFROG = 0;
    public static final int LANGUAGE_XML_CDL = 1;
    public static final int LANGUAGE_ANT = 2;


    public static final String WS_NOTIFICATION = DeployApiConstants.CALLBACK_WS_NOTIFICATION;
    public static final String CDDLM_CALLBACKS = DeployApiConstants.CALLBACK_CDDLM_PROTOTYPE;
    public static final String WS_EVENTING = DeployApiConstants.CALLBACK_WS_EVENTING;
    public static final String[] CALLBACKS = {
        CDDLM_CALLBACKS
    };


    /**
     * list of all the options we support.
     */
    public static final String[] SUPPORTED_OPTIONS = {
//        DeployApiConstants.OPTION_BLOCKING_DEPLOY,
        DeployApiConstants.OPTION_NAME,
        DeployApiConstants.OPTION_PROPERTIES,
        DeployApiConstants.OPTION_VALIDATE_ONLY
    };

    public static final String SMARTFROG_HOMEPAGE = "http://smartfrog.org/";
    public static final String PRODUCT_NAME = "SmartFrog implementation";
    public static final String CVS_INFO = "$Id: Constants.java 1156 2004-09-27 15:00:45Z steve_l $";
    public static final String SMARTFROG_SCHEMA = "smartfrog";
    public static final String ERROR_INVALID_SCHEMA = "invalid schema in URI: ";
    public static final String ERROR_NO_APPLICATION = "application is undefined";

    public static final String CDDLM_FAULT_NAMESPACE = DeployApiConstants.CDDLM_FAULT_NAMESPACE;


}
