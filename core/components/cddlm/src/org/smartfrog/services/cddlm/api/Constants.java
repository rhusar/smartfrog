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
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;

/**
 * created Aug 4, 2004 10:34:47 AM
 */

public class Constants {
    public static final String[] LANGUAGES = {
        "SmartFrog", "1.0", DeployApiConstants.SMARTFROG_NAMESPACE,
        "XML-CDL", "0.3", DeployApiConstants.XML_CDL_NAMESPACE,
        "Apache Ant", "1.7", DeployApiConstants.ANT_NAMESPACE
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


    public static final String WS_NOTIFICATION = "ws-notification";
    public static final String CDDLM_CALLBACKS = "cddlm-prototype";
    public static final String WS_EVENTING = "ws-eventing";
    public static final String[] CALLBACKS = {
        CDDLM_CALLBACKS
    };
    public static final String SMARTFROG_HOMEPAGE = "http://smartfrog.org/";
    public static final String PRODUCT_NAME = "SmartFrog implementation";
    public static final String CVS_INFO = "$Id$";
    public static final String SMARTFROG_SCHEMA = "smartfrog";
    public static final String ERROR_INVALID_SCHEMA = "invalid schema in URI: ";
    public static final String ERROR_NO_APPLICATION = "application is undefined";

    public static final String CDDLM_FAULT_NAMESPACE = DeployApiConstants.CDDLM_FAULT_NAMESPACE;
/*
    public static final QName FAULT_BAD_ARGUMENT = FaultCodes.FAULT_BAD_ARGUMENT;
    public static final QName FAULT_NESTED_EXCEPTION = FaultCodes.FAULT_NESTED_EXCEPTION
    public static final QName FAULT_APPLICATION_NOT_FOUND = new QName(
            CDDLM_FAULT_NAMESPACE, FaultCodes.NO_SUCH_APPLICATION_FAULTCODE);
    public static final QName FAULT_WRONG_APP_STATE = new QName(
            CDDLM_FAULT_NAMESPACE,
            FaultCodes.WRONG_APPLICATION_STATE_FAULTCODE);
    public static final QName FAULT_UNSUPPORTED_LANGUAGE = new QName(
            CDDLM_FAULT_NAMESPACE, FaultCodes.UNSUPPORTED_LANGUAGE_FAULTCODE);
    public static final QName FAULT_UNSUPPORTED_CALLBACK = new QName(
            CDDLM_FAULT_NAMESPACE, FaultCodes.UNSUPPORTED_CALLBACK_FAULTCODE);
    public static final QName FAULT_NOTUNDERSTOOD = new QName(
            CDDLM_FAULT_NAMESPACE, FaultCodes.NOT_UNDERSTOOD_FAULTCODE);
*/

}
