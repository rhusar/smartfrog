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
import org.mortbay.thread.BoundedThreadPool;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * Socketlistner class for SocketListener for Jetty http server.
 * @author Ritu Sabharwal
 */

public class JettySocketConnectorImpl extends PrimImpl implements JettySocketConnector {
    private Reference listenerPortRef = new Reference(LISTENER_PORT);
    private Reference serverHostRef = new Reference(SERVER_HOST);
    private Reference serverNameRef = new Reference(SERVER_NAME);

    private SocketConnector connector = null;

    private JettyHelper jettyHelper = new JettyHelper(this);

    /**
     * constructor
     * @throws RemoteException parent failure
     */
  public JettySocketConnectorImpl() throws RemoteException {
  }



  /**
   * sfStart: adds the SocketListener to the jetty server
   *
   * @exception  SmartFrogException In case of error while starting
   * @exception  RemoteException In case of network/rmi error
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
     * Create and configure the connector, then
     * bind it to the http server
     * @exception SmartFrogException In case of error while starting
     * @exception RemoteException In case of network/rmi error
     */
    protected void bindConnector() throws
          SmartFrogException, RemoteException {
      connector = createConnector();
      configureConnector();
      jettyHelper.addAndStartConnector(connector);
  }

    /**
     * Do any configuration.
     * The base implementation sets up the acceptors and the max idle time, then binds the
     * host and port.
     * @exception SmartFrogException In case of error while starting
     * @exception RemoteException In case of network/rmi error
     */
    protected void configureConnector() throws SmartFrogException, RemoteException {

        //first, set up all the threads;
        int threads = sfResolve(ATTR_THREADS, 0, true);
        BoundedThreadPool pool=new BoundedThreadPool();
        pool.setMinThreads(threads);
        pool.setMaxThreads(threads);
        connector.setThreadPool(pool);
        connector.setAcceptors(threads);
        connector.setMaxIdleTime(sfResolve(ATTR_MAX_IDLE_TIME,0,true));

        //now bind to the host and port
        int port = sfResolve(listenerPortRef, 0, true);
        String host = sfResolve(serverHostRef, (String)null, false);
        connector.setPort(port);
        if (host != null) {
            connector.setHost(host);
        }
    }

    /**
     * Override point: create a connector.
     * Used in the {@link #bindConnector()} method; after creation
     * it has its port and host set, then is bound to Jetty
     * @return a new connector (or subclass), with any config other than that done by the parent
     * @exception SmartFrogException In case of error while starting
     * @exception RemoteException In case of network/rmi error
     */
    protected SocketConnector createConnector() throws SmartFrogException, RemoteException {
        return new SocketConnector();
    }
}
