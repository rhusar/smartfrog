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

/**
 * AJPlistener class for AJPListener for jetty server.
 * @author Ritu Sabharwal
 */

public class AJPlistener extends PrimImpl implements Listener {
  Reference listenerPortRef = new Reference(LISTENER_PORT);
  Reference serverHostRef = new Reference(SERVER_HOST);

  int listenerPort = 8009;

  String serverHost = null;

  AJP13Listener listener = null;

  ProcessCompound process = null;

  HttpServer server = null;
       
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
	  listenerPort = sfResolve(listenerPortRef,listenerPort,true);
	  serverHost = sfResolve(serverHostRef, serverHost, true);
  }

  /**
   * sfStart: adds the AJPListener to the jetty server
   * 
   * @exception  SmartFrogException In case of error while starting  
   * @exception  RemoteException In case of network/rmi error 
   */
  public void sfStart() throws SmartFrogException, RemoteException {
	  super.sfStart();      
          addlistener(listenerPort, serverHost);
  }
     
  /**
   * Termination phase
   */
  public void sfTerminateWith(TerminationRecord status) {
	  try{
		  listener.stop();
	  } catch(Exception ex){
		  Logger.log(" Interrupted on AJPlistener termination " + ex);
	  }
	  server.removeListener(listener);
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
		  process = SFProcess.getProcessCompound();
		  server = (HttpServer)process.sfResolveId("Jetty Server"); 
	          server.addListener(listener);
		  try{
			  listener.start();
		  } catch(Exception ex){
			  throw SmartFrogException.forward(ex);
		  }
	  } catch (UnknownHostException unex) {
		   throw SmartFrogException.forward(unex);	
	  }
  } 	
}
