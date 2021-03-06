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
package org.smartfrog.services.www.context;

import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.FilterComponent;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * This implements our servlet component
 */
public class ServletContextImpl extends ApplicationServerContextImpl implements ServletContextIntf {


    /**
     * Get our servlet cast to a ServletContextIntf
     * @return the delegate, cast to the right type
     */
    protected ServletContextIntf getContext() {
        return (ServletContextIntf) getDelegate();
    }

    /**
     * construct
     * @throws RemoteException trouble in the superconstructor
     */
    public ServletContextImpl() throws RemoteException {
    }


    /**
     * subclasses must implement this to deploy their component.
     * It is called during sfDeploy, after we have bound to a server
     *
     * @return a component
     * @throws SmartFrogException error while doing the work
     * @throws RemoteException In case of network/rmi error
     */
    protected ApplicationServerContext deployThisComponent() throws RemoteException, SmartFrogException {
        return getServer().deployServletContext(this);
    }


    /**
     * Add a mime mapping
     *
     * @param extension extension to map (no '.')
     * @param mimeType  mimetype to generate
     * @throws SmartFrogException error while doing the work
     * @throws RemoteException In case of network/rmi error
     */
    public void addMimeMapping(String extension, String mimeType) throws RemoteException, SmartFrogException {
        getContext().addMimeMapping(extension, mimeType);
    }

    /**
     * Remove a mime mapping for an extension
     *
     * @param extension extension to unmap
     * @return true if the unmapping was successful
     * @throws SmartFrogException error while doing the work
     * @throws RemoteException In case of network/rmi error
     */
    public boolean removeMimeMapping(String extension) throws RemoteException, SmartFrogException {
        return getContext().removeMimeMapping(extension);
    }

    /**
     * add a servlet
     *
     * @param servletDeclaration component declaring the servlet
     * @return the delegate that implements the servlet binding
     * @throws SmartFrogException error while doing the work
     * @throws RemoteException In case of network/rmi error
     */
    public ServletContextComponentDelegate addServlet(ServletComponent servletDeclaration) throws RemoteException, SmartFrogException {
        return getContext().addServlet(servletDeclaration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContextComponentDelegate addFilter(FilterComponent declaration)
            throws RemoteException, SmartFrogException {
        return getContext().addFilter(declaration);
    }

    /**
     * Check the target file exists
     *
     * @throws SmartFrogException error while validating
     * @throws RemoteException In case of network/rmi error
     */
    @Override
    protected void validateDuringStartup()
            throws SmartFrogException, RemoteException {
   }
}
