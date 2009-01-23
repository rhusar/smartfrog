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

import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Connector;
import org.mortbay.thread.BoundedThreadPool;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.RemoteException;

/**
 * Socketlistner class for SocketListener for Jetty http server.
 *
 * @author Ritu Sabharwal
 */

public class JettySocketConnectorImpl extends AbstractConnectorImpl implements JettySocketConnector {

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
     * @throws RemoteException In case of network/rmi error
     */
    protected void configureConnector() throws SmartFrogException, RemoteException {

        Server server = jettyHelper.getServer();
        // set up all the threads;
        int threads = sfResolve(ATTR_THREADS, 0, true);
        BoundedThreadPool pool = new BoundedThreadPool();
        pool.setMinThreads(threads);
        pool.setMaxThreads(threads);
        SocketConnector socketConnector = getSocketConnector();
        socketConnector.setAcceptors(threads);
        //connector.setThreadPool(pool);
        //bind to the main thread pool
        socketConnector.setThreadPool(server.getThreadPool());
        setMaxIdleTime(connector);
        bindConnectorToPortAndHost(connector);
    }

    /**
     * Override point: create a connector. Used in the {@link #bindConnector()} method; after creation it has its port
     * and host set, then is bound to Jetty
     *
     * @return a new connector (or subclass), with any config other than that done by the parent
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException In case of network/rmi error
     */
    @Override
    protected Connector createConnector() throws SmartFrogException, RemoteException {
        return new SocketConnector();
    }
}
