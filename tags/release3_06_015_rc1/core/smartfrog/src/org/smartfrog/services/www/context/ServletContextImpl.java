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

import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * This implements our servlet component
 */
public class ServletContextImpl extends ApplicationServerContextImpl implements ServletContextIntf {

    /**
     * our entry
     */
    private ApplicationServerContextEntry entry;

    private ServletContextIntf delegate;

    public ServletContextImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        bindToServer();
        entry = getServer().deployServletContext(this);
        delegate=(ServletContextIntf) entry.getImplementation();
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        delegate.start();
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        delegate.ping();
    }


    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            delegate.undeploy();
        } catch (RemoteException e) {
            //ignore
        } catch (SmartFrogException e) {
            //ignore
        }
    }

    /**
     * Add a mime mapping
     *
     * @param extension extension to map (no '.')
     * @param mimeType  mimetype to generate
     */
    public void addMimeMapping(String extension, String mimeType) throws RemoteException, SmartFrogException {
        delegate.addMimeMapping(extension, mimeType);
    }

    /**
     * Remove a mime mapping for an extension
     *
     * @param extension extension to unmap
     * @return true if the unmapping was successful
     */
    public boolean removeMimeMapping(String extension) throws RemoteException, SmartFrogException {
        return delegate.removeMimeMapping(extension);
    }

    /**
     * add a servlet
     *
     * @param servletDeclaration component declaring the servlet
     * @return the delegate that implements the servlet binding
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public ServletContextComponentDelegate addServlet(ServletComponent servletDeclaration) throws RemoteException, SmartFrogException {
        return delegate.addServlet(servletDeclaration);
    }
}
