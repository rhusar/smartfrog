/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.services.jetty.listeners;

import org.mortbay.http.SocketListener;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 * Socketlistner class for SocketListener for Jetty http server.
 * @author Ritu Sabharwal
 */

public class Socketlistener extends PrimImpl implements SocketListenerIntf {
    Reference listenerPortRef = new Reference(LISTENER_PORT);
    Reference serverHostRef = new Reference(SERVER_HOST);
    Reference serverNameRef = new Reference(SERVER);

    int listenerPort = 8080;

    String serverHost = null;

    String serverName = null;

    SocketListener listener = null;

    JettyHelper jettyHelper = new JettyHelper(this);

  /** Standard RMI constructor */
  public Socketlistener() throws RemoteException {
          super();
  }

  /**
   * Deploy the SocketListener listener
   * @exception  SmartFrogException In case of error while deploying
   * @exception  RemoteException In case of network/rmi error
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
          super.sfDeploy();
  }

  /**
   * sfStart: adds the SocketListener to the jetty server
   *
   * @exception  SmartFrogException In case of error while starting
   * @exception  RemoteException In case of network/rmi error
   */
  public void sfStart() throws SmartFrogException, RemoteException {
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
          listener = new SocketListener();
          listener.setPort(listenerPort);
          listener.setHost(serverHost);
          jettyHelper.addAndStartListener(listener);
          } catch (UnknownHostException unex) {
                   throw SmartFrogException.forward(unex);
          }
  }
}
