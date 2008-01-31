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
package org.smartfrog.services.ssh;

import org.smartfrog.sfcore.utils.ShouldDetachOrTerminate;

import java.rmi.Remote;

/**
 * Interface with common connection options
 * Created 22-Oct-2007 16:10:09
 */


public interface SSHComponent extends Remote {

    /**
     * {@value}
     */
    String ATTR_PASSWORD_PROVIDER = "passwordProvider";

    /**
     * {@value}
     */
    String ATTR_HOST = "host";
    /**
     * {@value}
     */
    String ATTR_PORT = "port";
    /**
     * {@value}
     */
    String ATTR_USER = "username";
    /**
     * {@value}
     */
    String ATTR_KEYFILE = "keyfile";
    /**
     * {@value}
     */
    String ATTR_PASSWORD = "password";
    /**
     * Waits at most millis milliseconds for this operation to finish.
     * A timeout of 0 means to wait forever.
     * {@value}
     */
    String ATTR_TIMEOUT = "timeout";
    /**
     * {@value}
     */
    String ATTR_TRUST_ALL_CERTIFICATES = "trustAllCertificates";
    /**
     * {@value}
     */
    String ATTR_KNOWN_HOSTS = "knownHosts";
    /**
     * How to authenticate: {@value}
     */
    String ATTR_AUTHENTICATION ="authentication";

    /**
     * Use password based authentication: {@value}
     */
    String AUTHENTICATION_PASSWORD ="password";

    /**
     * Use a key file: {@value}
     */
    String AUTHENTICATION_PUBLICKEY = "key";

    /**
     * {@value}
     */

    String ATTR_SHOULD_TERMINATE = ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE;
    /**
     * {@value}
     */

    String ATTR_LOG_FILE = "logFile";

    /**
     * Default SSH Port
     */
    int SSH_PORT = 22;
}
