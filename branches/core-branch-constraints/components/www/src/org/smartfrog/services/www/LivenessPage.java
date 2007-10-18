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

import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for liveness checking
 * created 28-Apr-2004 17:41:06
 */


public interface LivenessPage extends Remote {

    String ATTR_URL = "url";
    String ATTR_HOST = "host";
    String ATTR_PORT = "port";
    String ATTR_PROTOCOL = "protocol";
    String ATTR_PAGE = "page";
    String ATTR_PATH = "path";
    String ATTR_FOLLOW_REDIRECTS = "followRedirects";
    String ATTR_ERROR_TEXT = "fetchErrorText";
    String ATTR_MINIMUM_RESPONSE_CODE = "minimumResponseCode";
    String ATTR_MAXIMUM_RESPONSE_CODE = "maximumResponseCode";
    String ATTR_CHECK_FREQUENCY = "checkFrequency";
    String ATTR_ENABLED = "enabled";
    String ATTR_QUERIES = "queries";
    String ATTR_MIME_TYPES = "mimeType";
    String ATTR_CHECK_ON_STARTUP = "checkOnStartup";
    String ATTR_CHECK_ON_LIVENESS = "checkOnLiveness";

    /**
     * Check the page, regardless of whether the component is enabled or not.
     * This is the programmatic option.
     *
     * @throws SmartFrogLivenessException for an http error
     * @throws RemoteException for network problems
     *
     */
    void checkPage() throws SmartFrogLivenessException, RemoteException;
}
