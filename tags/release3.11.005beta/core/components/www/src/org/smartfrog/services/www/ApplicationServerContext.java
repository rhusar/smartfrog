/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An application server context is anything that can be deployed inside an app
 * server. There is a 1:1 mapping of the methods here and those of the normal
 * smartfrog lifecycle, because the relevant delegate operations get called at
 * the appropriate points.
 * <p/>
 * Why the different name? Just so that they can live side-by-side in the same
 * classes.
 */
public interface ApplicationServerContext extends Remote {
    /**
     * {@value}
     */
    String ATTR_CONTEXT_PATH = "contextPath";
    /**
     * {@value}
     */
    String ATTR_SERVER = "server";
    /**
     * absolute path is the path up to the first "*" {@value}
     */
    String ATTR_ABSOLUTE_PATH = "absolutePath";
    /**
     * name or File reference of a file {@value}
     */
    String ATTR_FILE = "filename";

    /**
     * Error string for missing WAR file {@value}
     */
    String ERROR_FILE_NOT_FOUND = "File not found:";

    void deploy() throws SmartFrogException, RemoteException;

    /**
     * start the component
     *
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException for RMI/Networking problems
     */
    void start() throws SmartFrogException, RemoteException;

    /**
     * this method is here for server-specific implementation classes,
     *
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException for RMI/Networking problems
     */
    public void terminate() throws RemoteException, SmartFrogException;


    /**
     * liveness check
     *
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException for RMI/Networking problems
     */
    void ping() throws SmartFrogLivenessException, RemoteException;
}
