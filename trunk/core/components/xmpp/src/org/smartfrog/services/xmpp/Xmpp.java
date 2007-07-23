/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xmpp;

import java.rmi.Remote;

/**
 */
public interface Xmpp extends Remote {
    /**
     * hostname of the server. {@value}
     */
    String ATTR_SERVER = "server";

    /**
     * name of the service when different from the server name {@value}
     */
    String ATTR_SERVICE_NAME = "serviceName";

    /**
     * user name for logins. {@value}
     */
    String ATTR_LOGIN = "login";
    /**
     * password for logins. {@value}
     */
    String ATTR_PASSWORD = "password";
    /**
     * connection port. {@value}
     */
    String ATTR_PORT = "port";

    /**
     * should presence information be provided. {@value}
     */
    String ATTR_PRESENCE = "presence";
    /**
     * should we require an encrypted connection
     * -that is, fail if the connection negotiated is not secured.
     * {@value}
     */
    String ATTR_REQUIRE_ENCRYPTION="requireEncryption";
    /**
     * resource for the login. {@value}
     */
    String ATTR_RESOURCE = "resource";

    /**
     * use TLS connection? {@value}
     */
    String ATTR_USE_TLS = "useTLS";


}
