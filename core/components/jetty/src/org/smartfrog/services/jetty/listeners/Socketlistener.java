package org.smartfrog.services.jetty.listeners;

import java.rmi.RemoteException;
import java.net.UnknownHostException;

import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.jetty.JettyHelper;
import org.mortbay.http.SocketListener;
import org.mortbay.http.HttpServer;

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
      listenerPort = sfResolve(listenerPortRef, listenerPort, true);
      serverHost = sfResolve(serverHostRef, serverHost, true);
      serverName = sfResolve(serverNameRef, serverName, true);
  }

  /**
   * sfStart: adds the SocketListener to the jetty server
   * 
   * @exception  SmartFrogException In case of error while starting  
   * @exception  RemoteException In case of network/rmi error 
   */  
  public void sfStart() throws SmartFrogException, RemoteException {
      super.sfStart();
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
          listener = new SocketListener();
          listener.setPort(listenerPort);
          listener.setHost(serverHost);
          jettyHelper.addAndStartListener(listener);
	  } catch (UnknownHostException unex) {
		   throw SmartFrogException.forward(unex);	
	  }
  } 	
}
