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

import javax.xml.namespace.QName;

/**
 * created Aug 4, 2004 10:34:47 AM
 */

public class Constants {
    public static final String[] LANGUAGES = {
        "SmartFrog", "1.0", org.smartfrog.services.cddlm.cdl.Constants.SMARTFROG_NAMESPACE,
        "XML-CDL", "0.3", org.smartfrog.services.cddlm.cdl.Constants.CDL_NAMESPACE,
        "Apache Ant", "1.7", org.smartfrog.services.cddlm.cdl.Constants.ANT_NAMESPACE
    };

    public static final String[] LANGUAGE_NAMESPACES = {
        org.smartfrog.services.cddlm.cdl.Constants.SMARTFROG_NAMESPACE,
        org.smartfrog.services.cddlm.cdl.Constants.CDL_NAMESPACE,
        org.smartfrog.services.cddlm.cdl.Constants.ANT_NAMESPACE
    };

    /**
     * these consts must match the position above
     */
    public static final int LANGUAGE_UNKNOWN = -1;
    public static final int LANGUAGE_SMARTFROG = 0;
    public static final int LANGUAGE_XML_CDL =1;
    public static final int LANGUAGE_ANT =2;


    public static final String WS_NOTIFICATION = "ws-notification";
    public static final String CDDLM_CALLBACKS = "cddlm-prototype";
    public static final String WS_EVENTING = "ws-eventing";
    public static final String[] CALLBACKS = {
        CDDLM_CALLBACKS
    };
    public static final String SMARTFROG_HOMEPAGE = "http://smartfrog.org/";
    public static final String PRODUCT_NAME = "SmartFrog implementation";
    public static final String CVS_INFO = "$ID$ $NAME$ $REVISION$";
    public static final String SMARTFROG_SCHEMA = "smartfrog";
    public static final String ERROR_INVALID_SCHEMA = "invalid schema in URI: ";
    public static final String ERROR_NO_APPLICATION = "application is undefined";

    public static final String CDDLM_FAULT_NAMESPACE = org.smartfrog.services.cddlm.cdl.Constants.SMARTFROG_NAMESPACE;

    public static final QName CDDLM_BAD_ARGUMENT = new QName(CDDLM_FAULT_NAMESPACE,"bad-argument");
    public static final QName CDDLM_CAUGHT_EXCEPTION = new QName(CDDLM_FAULT_NAMESPACE, "caught-exception");
}
