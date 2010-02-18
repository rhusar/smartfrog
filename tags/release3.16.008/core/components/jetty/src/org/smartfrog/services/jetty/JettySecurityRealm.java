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
package org.smartfrog.services.jetty;

import org.smartfrog.services.www.ApplicationServerContext;

import java.rmi.Remote;

/**
 * Created 25-Oct-2007 16:57:39
 * <pre>
 *     sfClass "";
 name TBD;
 path TBD;
 //lazy link to server
 server TBD;

 authentication "basic";

 //list of [name, credential] pairs
 users [];

 //list of [[user,role1,role2]] tuples
 roles []

 //list of tuples of paths and lists of name, role pairs.
 constraints [];
 </pre>
 */


public interface JettySecurityRealm extends Remote {

    /**
     * {@value}
     */
    String ATTR_SERVER="server";

    /**
     * {@value}
     */
    String ATTR_NAME = "name";

    /**
     * {@value}
     */
    String ATTR_PATH = ApplicationServerContext.ATTR_CONTEXT_PATH;

    /**
     * {@value}
     */
    String ATTR_AUTHENTICATION = "authentication";

    /**
     * {@value}
     */
    String ATTR_USERS = "users";

    /**
     * {@value}
     */
    String ATTR_ROLES = "roles";

    /**
     * {@value}
     */
    String ATTR_CONSTRAINTS = "constraints";
}
