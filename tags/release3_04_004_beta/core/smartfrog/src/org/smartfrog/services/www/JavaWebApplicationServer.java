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

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * A webapp server is something that knows how to deploy
 * web applications. How it does so is up to it.
 * created 23-Jun-2004 13:40:25
 */


public interface JavaWebApplicationServer extends Remote {

    /**
     * deploy a web application.
     * Deploys a web application identified by the component passed as a parameter; a component of arbitrary
     * type but which must have the mandatory attributes identified in {@link JavaWebApplication};
     * possibly even extra types required by the particular application server.
     *
     * @param webApplication the web application. this must be a component whose attributes include the
     * mandatory set of attributes defined for a JavaWebApplication component. Application-server specific attributes
     * (both mandatory and optional) are also permitted
     * @return the context path deployed to. 
     * @throws RemoteException  on network trouble
     * @throws SmartFrogException on any other problem
     */
    public String DeployWebApplication(Prim webApplication)
        throws RemoteException, SmartFrogException;

    /**
     * undeploy a web application
     * @param webApplication    the web application itself;
     * @throws RemoteException  on network trouble
     * @throws SmartFrogException on any other problem
     */
    public void UndeployWebApplication(Prim webApplication)
            throws RemoteException, SmartFrogException;

}
