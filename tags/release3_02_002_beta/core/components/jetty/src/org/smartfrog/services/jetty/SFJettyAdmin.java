package org.smartfrog.services.jetty;

import java.rmi.RemoteException;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.mortbay.http.handler.SecurityHandler;
import org.mortbay.http.HashUserRealm;
import org.mortbay.http.BasicAuthenticator;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.util.MultiException;

/**
 * A wrapper for a Jetty http server.
 * @author Ritu Sabharwal
 */

public class SFJettyAdmin extends PrimImpl implements Prim {
  Reference listenerPortRef = new Reference("listenerPort");
  Reference httpserverHostRef = new Reference("httpserverHost");
  Reference contextPathRef = new Reference("contextPath");
  
  int listenerPort;
  String httpserverHost;
  String contextPath;
  
  /** The server */
  HttpServer server;
 
  /** The Socket listener */
  SocketListener listener = new SocketListener();
  
  /** Realm context */
  ServletHttpContext realmcontext = new ServletHttpContext();
  
  /** User realm */
  HashUserRealm admin_realm = new HashUserRealm("Admin Realm");

  /** Standard RMI constructor */
  public SFJettyAdmin() throws RemoteException {
    super();
  }

  /**
   * Deploy the SFJettyAdmin component
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
    try {
    super.sfDeploy();
    server = new HttpServer();
    listenerPort = sfResolve(listenerPortRef,8081,false);
    httpserverHost = sfResolve(httpserverHostRef, "localhost", false);
    contextPath = sfResolve(contextPathRef, "/", false);
    configureHttpServer();
    } catch (Exception ex){ 
       ex.printStackTrace();
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
        	throw new SmartFrogException(mexp);
        }
    }
    
/**
 * Configure the http server
 */
  public void configureHttpServer() {
    try {
         listener.setPort(listenerPort);
         listener.setHost(httpserverHost);
         server.addListener(listener);

         
         admin_realm.put("admin","admin");
         admin_realm.addUserToRole("admin","server-administrator");
         server.addRealm(admin_realm);
         realmcontext.setContextPath(contextPath);
         realmcontext.setRealmName("Admin Realm");
         realmcontext.setAuthenticator(new BasicAuthenticator());
         realmcontext.addHandler(new SecurityHandler());
         realmcontext.addSecurityConstraint("/",
		    new SecurityConstraint("Admin","server-administrator"));
         realmcontext.addServlet("Debug","/Debug/*","org.mortbay.servlet.Debug");
         realmcontext.addServlet("Admin","/","org.mortbay.servlet.AdminServlet");
         realmcontext.setAttribute("org.mortbay.http.HttpServer",
		    realmcontext.getHttpServer());
         server.addContext(realmcontext);
         server.setAnonymous(true);
   } catch (Exception ex) {
	    ex.printStackTrace();
   }
  }

 /**
 * Termination phase
 */
  public void sfTerminateWith(TerminationRecord status) {
   server.removeListener(listener);
   server.removeContext(realmcontext);
    try {
      server.stop();
    } catch (InterruptedException ie) {
	   // throw ie;
	   Logger.log(" Interrupted on server termination " + ie);
	   ie.printStackTrace();
    // throw new SmartFrogException(" Interrupted on server termination " + ie);
    }
    super.sfTerminateWith(status);
  }
}
