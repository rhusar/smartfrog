package org.smartfrog.services.jetty.listeners;

import java.rmi.RemoteException;
import java.net.UnknownHostException;

import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.mortbay.http.HttpServer;
import org.mortbay.http.ajp.AJP13Listener;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.jetty.JettyHelper;

/**
 * AJPlistener class for AJPListener for jetty server.
 * @author Ritu Sabharwal
 */

public class AJPlistener extends PrimImpl implements Listener {
    protected Reference listenerPortRef = new Reference(LISTENER_PORT);
    protected Reference serverHostRef = new Reference(SERVER_HOST);
    protected Reference serverNameRef = new Reference(SERVER);

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
