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
package org.smartfrog.services.jetty.listeners;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.jetty.JettyHelper;
import org.mortbay.jetty.Connector;

import java.rmi.RemoteException;

/**
 *
 * Created 11-Oct-2007 12:18:34
 *
 */

public abstract class AbstractConnectorImpl extends PrimImpl {
    protected Reference portRef = new Reference(JettyConnector.LISTENER_PORT);
    protected Reference hostRef = new Reference(JettyConnector.SERVER_HOST);
    protected Reference serverNameRef = new Reference(JettyConnector.SERVER_NAME);
    protected Connector connector = null;
    protected JettyHelper jettyHelper = new JettyHelper(this);


    protected AbstractConnectorImpl() throws RemoteException {
    }

    public Connector getConnector() {
        return connector;
    }

    /**
     * sfStart: adds the SocketListener to the jetty server
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            jettyHelper.bindToServer();
            bindConnector();
        } catch (Exception ex) {
            throw SmartFrogDeploymentException.forward(ex);
        }
    }

    /**
     * Termination phase
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        jettyHelper.terminateConnector(connector);
        super.sfTerminateWith(status);
    }

    /**
     * Create and configure the connector, then bind it to the http server
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException In case of network/rmi error
     */
    protected void bindConnector() throws
            SmartFrogException, RemoteException {
        connector = createConnector();
        configureConnector();
        jettyHelper.addAndStartConnector(connector);
    }

    protected abstract void configureConnector() throws SmartFrogException, RemoteException;

    protected abstract Connector createConnector() throws SmartFrogException, RemoteException;

    /**
     * This method reads the port and host attributes, and sets the connector to it
     * @param conn the connector
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException In case of network/rmi error
     */
    protected void bindConnectorToPortAndHost(Connector conn) throws SmartFrogException, RemoteException {
        //now bind to the host and port
        int port = sfResolve(portRef, 0, true);
        String host = sfResolve(hostRef, (String) null, false);
        conn.setPort(port);
        if (host != null) {
            conn.setHost(host);
        }
    }

    /**
     * Set the max idle time of this connector to that of {@link #ATTR_MAX_IDLE_TIME}
     * @param connector
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    protected void setMaxIdleTime(Connector connector) throws SmartFrogResolutionException, RemoteException {
        connector.setMaxIdleTime(sfResolve(JettySocketConnector.ATTR_MAX_IDLE_TIME, 0, true));
    }
}
