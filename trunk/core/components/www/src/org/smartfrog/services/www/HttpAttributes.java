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
package org.smartfrog.services.www;

import java.rmi.Remote;

/**
 * Created 28-Nov-2007 17:31:08
 */


public interface HttpAttributes extends Remote {
    /** component attribute, value {@value} */
    String ATTR_URL = "url";
    /** component attribute, value {@value} */
    String ATTR_HOST = "host";
    /** component attribute, value {@value} */
    String ATTR_PORT = "port";
    /** component attribute, value {@value} */
    String ATTR_PROTOCOL = "protocol";
    /** component attribute, value {@value} */
    String ATTR_PAGE = "page";
    /** component attribute, value {@value} */
    String ATTR_PATH = "path";
    /** component attribute, value {@value} */
    String ATTR_FOLLOW_REDIRECTS = "followRedirects";
    /** component attribute, value {@value} */
    String ATTR_ERROR_TEXT = "fetchErrorText";
    /** component attribute, value {@value} */
    String ATTR_MINIMUM_RESPONSE_CODE = "minimumResponseCode";
    /** component attribute, value {@value} */
    String ATTR_MAXIMUM_RESPONSE_CODE = "maximumResponseCode";
    /** component attribute, value {@value} */
    String ATTR_CHECK_FREQUENCY = "checkFrequency";
    /** component attribute, value {@value} */
    String ATTR_ENABLED = "enabled";
    /** component attribute, value {@value} */
    String ATTR_QUERIES = "queries";
    /** component attribute, value {@value} */
    String ATTR_MIME_TYPES = "mimeType";
    /** component attribute, value {@value} */
    String ATTR_CHECK_ON_STARTUP = "checkOnStartup";
    /** component attribute, value {@value} */
    String ATTR_CHECK_ON_LIVENESS = "checkOnLiveness";
    /**
     * component attribute, value {@value}
     */
    String ATTR_RESPONSE_REGEXP = "responseRegexp";
    /**
     * component attribute, value {@value}
     */
    String ATTR_HEADERS = "headers";
    /**
     * component attribute, value {@value}
     */
    String ATTR_CONNECT_TIMEOUT = "connectTimeout";
    /**
     * component attribute, value {@value}
     */
    String ATTR_USERNAME = "username";
    /**
     * component attribute, value {@value}
     */
    String ATTR_PASSWORD = "password";
    
    /**
     * SF attribute - {@value}
     */
    String ATTR_AUTHORIZATION = "authorization";
    /**
     * SF attribute - {@value}
     */
    String ATTR_EXPECTEDMIMETYPE = "expectedMimeType";
    /**
     * SF attribute - {@value}
     */
    String ATTR_RESULTTEXT = "resultText";
    /** {@value} */
    String ATTR_LOGRESPONSE = "logResponse";
    /** {@value} */
    String ATTR_AUTH_REQUIRED = "authenticationRequired";
}
