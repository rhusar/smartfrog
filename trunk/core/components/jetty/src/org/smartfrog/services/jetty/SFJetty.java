package org.smartfrog.services.jetty;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.mortbay.http.HttpServer;
import org.mortbay.http.HashUserRealm;
import org.mortbay.http.BasicAuthenticator;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.NCSARequestLog;
import org.mortbay.util.MultiException;

/**
 * A wrapper for a Jetty http server.
 * @author Ritu Sabharwal
 */

public class SFJetty extends CompoundImpl implements JettyIntf {

    protected Reference jettyhomeRef = new Reference(JETTY_HOME);
    Reference serverNameRef = new Reference(SERVER);
 
  /**  Jetty home path */
  protected String jettyhome;

  String serverName = null;
  
  /** The Http server */
  HttpServer server;

  /** Standard RMI constructor */
  public SFJetty() throws RemoteException {
    super();
  }

  /**
   * Deploy the SFJetty component
   * @exception  SmartFrogException In case of error while deploying  
   * @exception  RemoteException In case of network/rmi error  
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
    try {
	   server = new HttpServer();
	   serverName = sfResolve(serverNameRef, serverName, true);
	   ProcessCompound process = SFProcess.getProcessCompound();
	   process.sfAddAttribute(serverName, server);
	   jettyhome = sfResolve(jettyhomeRef,jettyhome,true);
           process.sfAddAttribute(jettyhome, jettyhome);
	   configureHttpServer();
           super.sfDeploy();
           
    } catch (Exception ex){ 
       throw SmartFrogDeploymentException.forward(ex);
    }
  }

  /**
   * sfStart: starts Jetty Http server.
   * 
   * @exception  SmartFrogException In case of error while starting  
   * @exception  RemoteException In case of network/rmi error 
   */
   public synchronized void sfStart() throws SmartFrogException, 
   RemoteException {
	   super.sfStart();
	   try {
		   server.start();
	   } catch (MultiException mexp) {
		   throw SmartFrogException.forward(mexp);
	   }
   }
    
  /**
   * Configure the http server
   */
  public void configureHttpServer() throws SmartFrogException{
      try {
          /*
          server.addRealm(new HashUserRealm("Jetty Demo Realm", jettyhome
                  + "/etc/demoRealm.properties"));
          server.addRealm(new HashUserRealm("Example Form-Based Authentication Area",
                  jettyhome + "/etc/examplesRealm.properties"));
          */
          NCSARequestLog requestlog = new NCSARequestLog();
          requestlog.setFilename(jettyhome +
                  "/logs/yyyy_mm_dd.request.log");
          requestlog.setBuffered(false);
          requestlog.setRetainDays(90);
          requestlog.setAppend(true);
          requestlog.setExtended(true);
          requestlog.setLogTimeZone("GMT");
          String[] paths = {"/jetty/images/*",
                            "/demo/images/*", "*.css"};
          requestlog.setIgnorePaths(paths);
          server.setRequestLog(requestlog);
      } catch (Exception ex) {
          throw SmartFrogException.forward(ex);
      }
  }

  /**
   * Termination phase
   */
  public void sfTerminateWith(TerminationRecord status) {
	  try {
		  server.stop();
	  } catch (InterruptedException ie) {
		  Logger.log(" Interrupted on server termination " , ie);
	  }
	  super.sfTerminateWith(status);
  }
}
