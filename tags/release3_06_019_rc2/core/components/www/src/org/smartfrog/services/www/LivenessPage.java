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
package org.smartfrog.services.www;

import java.rmi.Remote;

/**
 * Interface for liveness checking
 * created 28-Apr-2004 17:41:06
 */


public interface LivenessPage extends Remote {

    static final String ATTR_URL = "url";
    static final String ATTR_HOST = "host";
    static final String ATTR_PORT = "port";
    static final String ATTR_PROTOCOL = "protocol";
    static final String ATTR_PAGE = "page";
    static final String ATTR_FOLLOW_REDIRECTS = "followRedirects";
    static final String ATTR_ERROR_TEXT = "fetchErrorText";
    static final String ATTR_MINIMUM_RESPONSE_CODE = "minimumResponseCode";
    static final String ATTR_MAXIMUM_RESPONSE_CODE = "maximumResponseCode";
    static final String ATTR_CHECK_FREQUENCY = "checkFrequency";
    static final String ATTR_ENABLED = "enabled";
    static final String ATTR_QUERIES = "queries";
    static final String ATTR_MIME_TYPES = "mimeType";
}
