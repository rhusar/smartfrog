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
package org.cddlm.components;

import java.rmi.Remote;

/**
 * created 28-Apr-2004 17:41:06
 */


public interface LivenessPage extends Remote {

    String URL = "url";
    String HOST = "host";
    String PORT = "port";
    String PROTOCOL = "protocol";
    String PAGE = "page";
    String FOLLOW_REDIRECTS = "followRedirects";
    String FETCH_ERROR_TEXT = "fetchErrorText";
    String MINIMUM_RESPONSE_CODE = "minimumResponseCode";
    String MAXIMUM_RESPONSE_CODE = "maximumResponseCode";
    String CHECK_FREQUENCY = "checkFrequency";
    String ENABLED = "enabled";
}
