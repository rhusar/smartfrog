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

import org.mortbay.http.ajp.AJP13Listener;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 * AJPlistener class for AJPListener for jetty server.
 * @author Ritu Sabharwal
 */

public class AJPlistener extends PrimImpl implements Listener {
    protected Reference listenerPortRef = new Reference(LISTENER_PORT);
    protected Reference serverHostRef = new Reference(SERVER_HOST);
    protected Reference serverNameRef = new Reference(SERVER_NAME);

    protected int listenerPort = 8009;

    protected String serverHost = null;

    protected String serverName = null;

    protected AJP13Listener listener = null;

    protected JettyHelper jettyHelper = new JettyHelper(this);

  /** Standard RMI constructor */
  public AJPlistener() throws RemoteException {
	  super();
  }

  /**
   * Deploy the AJPListener listener
   * @exception  SmartFrogException In case of error while deploying
   * @exception  RemoteException In case of network/rmi error
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
	  super.sfDeploy();
  }

  /**
   * sfStart: adds the AJPListener to the jetty server
   *
   * @exception  SmartFrogException In case of error while starting
   * @exception  RemoteException In case of network/rmi error
   */
  public void sfStart() throws SmartFrogException, RemoteException {
	  super.sfStart();
      listenerPort = sfResolve(listenerPortRef, listenerPort, true);
      serverHost = sfResolve(serverHostRef, serverHost, false);
      jettyHelper.bindToServer();
      addlistener(listenerPort, serverHost);
  }

  /**
   * Termination phase
   */
  public void sfTerminateWith(TerminationRecord status) {
      jettyHelper.terminateListener(listener);
	  super.sfTerminateWith(status);
  }

  /**
   * Add the listener to the http server
   * @exception  RemoteException In case of network/rmi error
   */
  public void addlistener(int listenerPort, String serverHost) throws
  SmartFrogException, RemoteException {
	  try {
		  listener = new AJP13Listener();
          listener.setPort(listenerPort);
          listener.setHost(serverHost);
          jettyHelper.addAndStartListener(listener);
	  } catch (UnknownHostException unex) {
		   throw SmartFrogException.forward(unex);
	  }
  }
}
