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

/**
 * created Aug 4, 2004 10:34:47 AM
 */

public class Constants {
    public static final String[] LANGUAGES = {
        "SmartFrog", "1.0", "http://gridforge.org/cddlm/smartfrog/2004/07/30",
        "XML-CDL", "0.3", "http://gridforge.org/cddlm/xml/2004/07/30/",
        "Apache Ant", "1.7", "http://ant.apache.org/xsd"
    };
    public static final String  WS_NOTIFICATION="ws-notification";
    public static final String  CDDLM_CALLBACKS= "cddlm-prototype";
    public static final String  WS_EVENTING= "ws-eventing";
    public static final String[] CALLBACKS= {
        CDDLM_CALLBACKS
    };
    public static final String SMARTFROG_HOMEPAGE = "http://smartfrog.org/";
    public static final String PRODUCT_NAME = "SmartFrog implementation";
    public static final String CVS_INFO = "$ID$ $NAME$ $REVISION$";
}
