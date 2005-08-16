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

import org.smartfrog.services.www.context.ApplicationServerContextEntry;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A webapp server is something that knows how to deploy
 * web applications. How it does so is up to it.
 * created 23-Jun-2004 13:40:25
 */


public interface JavaWebApplicationServer extends Remote {


    /**
     * attribute that is to be set only if WAR deployment is supported.
     * {@value}
     */
    public static final String ATTR_SUPPORTS_WAR = "supportsWAR";
    /**
     * attribute true iff servlet contexts are supported.
     * {@value}
     */
    public static final String ATTR_SUPPORTS_SERVLET_CONTEXT = "supportsServletContext";
    /**
     * attribute true iff EAR deployment is supported.
     * {@value}
     */
    public static final String ATTR_SUPPORTS_EAR = "supportsEAR";

    /**
     * deploy a web application.
     * Deploys a web application identified by the component passed as a parameter; a component of arbitrary
     * type but which must have the mandatory attributes identified in {@link JavaWebApplication};
     * possibly even extra types required by the particular application server.
     *
     * @param webApplication the web application. this must be a component whose attributes include the
     * mandatory set of attributes defined for a JavaWebApplication component. Application-server specific attributes
     * (both mandatory and optional) are also permitted
     * @return a token referring to the application
     * @throws RemoteException  on network trouble
     * @throws SmartFrogException on any other problem
     */
    public String deployWebApplication(Prim webApplication)
        throws RemoteException, SmartFrogException;

    /**
     * Deploy a servlet context. This can be initiated with other things.
     *
     * This should be called from sfDeploy. The servlet is not deployed
     *
     *
     * @param servlet
     * @return a token referring to the application
     * @throws RemoteException  on network trouble
     * @throws SmartFrogException on any other problem
     */
    public ApplicationServerContextEntry deployServletContext(Prim servlet) throws RemoteException, SmartFrogException;

    /**
     * undeploy a web application
     * @param context the context reference supplied when a context was created
     * @throws RemoteException  on network trouble
     * @throws SmartFrogException on any other problem
     */
    public void undeployApplicationServerContext(String context)
            throws RemoteException, SmartFrogException;

    /**
     * lookup a servlet context, get the servlet interface back.
     * This servlet interface is one bound tightly to the implementation.
     * @param context
     * @return
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public ServletContextIntf lookupServletContext(String context) throws RemoteException, SmartFrogException;
}
