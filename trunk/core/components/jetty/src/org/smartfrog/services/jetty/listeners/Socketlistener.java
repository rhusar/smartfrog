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

public class Socketlistener extends PrimImpl implements SocketListenerIntf {
    private Reference listenerPortRef = new Reference(LISTENER_PORT);
    private Reference serverHostRef = new Reference(SERVER_HOST);
    private Reference serverNameRef = new Reference(SERVER_NAME);

    private int listenerPort = 8080;

    private String serverHost = null;

    private String serverName = null;

    private SocketConnector listener = null;

    private JettyHelper jettyHelper = new JettyHelper(this);

    /**
     * constructor
     * @throws RemoteException parent failure
     */
  public Socketlistener() throws RemoteException {
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
        listenerPort = sfResolve(listenerPortRef, listenerPort, true);
        jettyHelper.bindToServer();
        // Optional. If null or not defined, then it listens using all network interfaces
        serverHost = sfResolve(serverHostRef, serverHost, false);
        addlistener(listenerPort, serverHost);
        } catch (Exception ex) {
            throw SmartFrogDeploymentException.forward(ex);
        }
  }

  /**
   * Termination phase
   */
  public synchronized void sfTerminateWith(TerminationRecord status) {
      jettyHelper.terminateListener(listener);
      super.sfTerminateWith(status);
  }

  /**
   * Add the listener to the http server
   * @exception  RemoteException In case of network/rmi error
   */
  public void addlistener(int port, String host) throws
          SmartFrogException, RemoteException {
      listener = new SocketConnector();
      listener.setPort(port);
      if (host != null) {
          listener.setHost(host);
      }
      jettyHelper.addAndStartListener(listener);
  }
}
