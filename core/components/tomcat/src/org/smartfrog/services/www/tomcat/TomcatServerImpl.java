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
package org.smartfrog.services.www.tomcat;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.services.www.context.ApplicationServerContextEntry;
import org.smartfrog.services.www.context.ApplicationServerContextHolder;

import java.rmi.RemoteException;

/**
 */
public class TomcatServerImpl extends PrimImpl implements TomcatServer {
    /**
     * any contexts that we have deployed
     */
    protected ApplicationServerContextHolder contexts = new ApplicationServerContextHolder();


    public TomcatServerImpl() throws RemoteException {
    }

    /**
     * deploy a web application.
     * Deploys a web application identified by the component passed as a parameter; a component of arbitrary
     * type but which must have the mandatory attributes identified in {@link JavaWebApplication};
     * possibly even extra types required by the particular application server.
     *
     * @param webApplication the web application. this must be a component whose attributes include the
     *                       mandatory set of attributes defined for a JavaWebApplication component. Application-server specific attributes
     *                       (both mandatory and optional) are also permitted
     * @return a token referring to the application
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on any other problem
     */
    public ApplicationServerContextEntry deployWebApplication(Prim webApplication) throws RemoteException, SmartFrogException {
        TomcatWebApplication delegate = new TomcatWebApplication(this, webApplication);
        delegate.deploy();
        ApplicationServerContextEntry entry;
        entry = contexts.createEntry(ApplicationServerContextEntry.TYPE_WAR, delegate);
        return entry;
    }

    /**
     * Deploy an EAR file
     *
     * @param enterpriseApplication
     * @return an entry referring to the application
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public ApplicationServerContextEntry deployEnterpriseApplication(Prim enterpriseApplication) throws RemoteException, SmartFrogException {
        throw new SmartFrogException("not implemented : DeployWebApplication");
    }

    /**
     * Deploy a servlet context. This can be initiated with other things.
     * <p/>
     * This should be called from sfDeploy. The servlet is not deployed
     *
     * @param servlet
     * @return a token referring to the application
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on any other problem
     */
    public ApplicationServerContextEntry deployServletContext(Prim servlet) throws RemoteException, SmartFrogException {
        throw new SmartFrogException("not implemented : deployServletContext");
    }

    /**
     * lookup a context, get the context information back
     *
     * @param context
     * @return the
     * @throws RemoteException
     * @throws SmartFrogException
     *
     */
    public ApplicationServerContextEntry lookupContext(String context) throws RemoteException, SmartFrogException {
        return contexts.lookup(context);
    }

    /**
     * undeploy a web application
     *
     * @param context the context reference supplied when a context was created
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on any other problem
     */
    public void undeployApplicationServerContext(String context) throws RemoteException, SmartFrogException {
        ApplicationServerContextEntry entry = lookupContext(context);
        if (entry != null && entry.getImplementation() != null) {
            entry.getImplementation().undeploy();
        }
    }

    /**
     * lookup a servlet context, get the servlet interface back.
     * This servlet interface is one bound tightly to the implementation.
     *
     * @param context
     * @return
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public ServletContextIntf lookupServletContext(String context) throws RemoteException, SmartFrogException {
        ApplicationServerContextEntry entry = contexts.lookup(context);
        if (entry != null) {
            if (entry.getType() == ApplicationServerContextEntry.TYPE_SERVLET_CONTEXT) {
                return (ServletContextIntf) entry.getImplementation();
            } else {
                throw new SmartFrogException(ApplicationServerContextEntry.ERROR_WRONG_TYPE + context);
            }
        }
        return null;
    }
}
