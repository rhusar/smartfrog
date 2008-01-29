/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.restlet.client;

import org.smartfrog.services.www.HttpAttributes;


/**
 * Created 28-Nov-2007 17:23:26
 */


public interface RemoteRestletResource extends HttpAttributes {


    /**
     * SF attribute - {@value}
     */
    String ATTR_STARTACTIONS = "startActions";

    /**
     * SF attribute - {@value}
     */
    String ATTR_TERMINATEACTIONS = "terminateActions";

    /**
     * SF attribute - {@value}
     */
    String ATTR_LIVENESSACTIONS = "livenessActions";


    /**
     * SF attribute - {@value}
     */
    String ATTR_RESULTXPATH = "resultXPath";

    /**
     * SF constant {@value}
     */
    String GET = "get";

    /**
     * SF attribute - {@value}
     */
    String POST = "post";

    /**
     * SF constant {@value}
     */
    String PUT = "put";
    /**
     * SF constant {@value}
     */
    String DELETE = "delete";
    /**
     * SF constant {@value}
     */
    String OPTIONS = "options";
    /**
     * SF constant {@value}
     */
    String HEAD = "options";
    /**
     * SF constant {@value}
     */
    String ATTR_READ_TIMEOUT = "readTimeout";

    /**
     * SF constant {@value}
     */
    String ATTR_USE_SYSTEM_PROXY_SETTINGS = "useSystemProxySettings";

}
