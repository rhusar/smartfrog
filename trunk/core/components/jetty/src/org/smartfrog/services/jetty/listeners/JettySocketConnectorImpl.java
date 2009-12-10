/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.thread.QueuedThreadPool;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

import java.rmi.RemoteException;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;

/**
 * Socketlistner class for SocketListener for Jetty http server.
 *
 * @author Ritu Sabharwal
 */

public class JettySocketConnectorImpl extends AbstractConnectorImpl implements JettySocketConnector {
    public static final String ERROR_WRONG_FAMILY = "Address is of the wrong IP Family ";

    /**
     * constructor
     *
     * @throws RemoteException parent failure
     */
    public JettySocketConnectorImpl() throws RemoteException {
    }


    public SocketConnector getSocketConnector() {
        return (SocketConnector) connector;
    }


    /**
     * Do any configuration. The base implementation sets up the acceptors and the max idle time, then binds the host
     * and port.
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    protected void configureConnector() throws SmartFrogException, RemoteException {

        Server server = jettyHelper.getServer();
        // set up all the threads;
        QueuedThreadPool pool = createBoundedThreadPool();
        SocketConnector socketConnector = getSocketConnector();
        socketConnector.setAcceptors(sfResolve(ATTR_ACCEPTORS, 2, true));
        //bind to the thread pool
        socketConnector.setThreadPool(pool);
        setMaxIdleTime(connector);
        bindConnectorToPortAndHost(connector);
    }

    /**
     * Override point: create a connector. Used in the {@link #bindConnector()} method; after creation it has its port
     * and host set, then is bound to Jetty
     *
     * @return a new connector (or subclass), with any config other than that done by the parent
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    protected Connector createConnector() throws SmartFrogException, RemoteException {
        return new SocketConnector();
    }

    /**
     * Override point, something called after startup to do any port checking or similar
     *
     * @param startedConnector the connector
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    protected void onConnectorStarted(Connector startedConnector) throws SmartFrogException, RemoteException {
        boolean allowIPv4= sfResolve(ATTR_ALLOW_IPV4, true, true);
        boolean allowIPv6 = sfResolve(ATTR_ALLOW_IPV6, true, true);
        ServerSocket sock = getServerSocket();
        if (sock==null) {
            return;
        }
        InetAddress address = sock.getInetAddress();
        if((address instanceof Inet4Address) && !allowIPv4) {
            throw new SmartFrogDeploymentException(ERROR_WRONG_FAMILY + address);
        }
        if ((address instanceof Inet6Address) && !allowIPv6) {
            throw new SmartFrogDeploymentException(ERROR_WRONG_FAMILY + address);
        }
    }

    /**
     * Get the server socket or null
     * @return the socket if there is one
     */
    protected ServerSocket getServerSocket() {
        SocketConnector socketconn = getSocketConnector();
        if (socketconn == null) {
            return null;
        }
        Object connInstance = socketconn.getConnection();
        return (ServerSocket) connInstance;
    }
}
