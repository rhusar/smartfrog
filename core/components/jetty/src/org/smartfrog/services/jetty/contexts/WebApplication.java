package org.smartfrog.services.jetty.contexts;

import java.rmi.RemoteException;

import org.mortbay.http.HttpServer;
import org.smartfrog.services.jetty.contexts.Context;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.ServletHandler;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * A WebApplication context class for jetty server 
 * @author Ritu Sabharwal
 */


public class WebApplication extends PrimImpl implements Prim {
   Reference contextPathRef = new Reference("contextPath");
   Reference webAppRef = new Reference("webApp");
   Reference requestIdRef = new Reference("requestId");
   

   String jettyhome = ".";
   String contextPath = "/";
   String webApp = "\\demo\\webapps\\root";
   boolean requestId = false;

   ProcessCompound process = null;

   HttpServer server = null;
   
   WebApplicationContext context = new WebApplicationContext();

   /** Standard RMI constructor */
       public WebApplication() throws RemoteException {
       super();
       }
      
   /**
   * Deploy the WebApplication context
   * @exception  SmartFrogException In case of error while deploying  
   * @exception  RemoteException In case of network/rmi error  
   */ 
   public void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();     
       process = SFProcess.getProcessCompound();
       server = (HttpServer)process.sfResolveId("Jetty Server"); 
       jettyhome = (String)process.sfResolveId("jettyhome");
       contextPath = sfResolve(contextPathRef, contextPath, true);
       webApp = sfResolve(webAppRef, jettyhome + webApp, true);
       requestId = sfResolve(requestIdRef, requestId, false);
   }
   
   /**
   * sfStart: adds the WebApplication context to the jetty server
   * 
   * @exception  SmartFrogException In case of error while starting  
   * @exception  RemoteException In case of network/rmi error 
   */ 
   public void sfStart() throws SmartFrogException, RemoteException {
	   super.sfStart();
           addcontext(contextPath,webApp,requestId);
	   server.addContext(context);
	   try {
		   context.start();
	   } catch(Exception ex){
		   throw SmartFrogException.forward(ex);
	   }
   }   
  
   /**
   * Termination phase
   */
   public void sfTerminateWith(TerminationRecord status) {
	   try{
		  context.stop();
	   } catch(Exception ex){
		  Logger.log(" Interrupted on WebApplicationContext termination " + ex);
	  }
	   server.removeContext(context);
           super.sfTerminateWith(status);
   }
  
   /**
   * Add the context to the http server
   * @exception  RemoteException In case of network/rmi error 
   */ 
   public void addcontext(String contextpath, String webApp, boolean requestId) 
   throws RemoteException{
	  context.setContextPath(contextPath);
	  context.setWAR(webApp);
	  ServletHandler servlethandler = context.getServletHandler();
          AbstractSessionManager sessionmanager = (AbstractSessionManager)
		  servlethandler.getSessionManager();
          sessionmanager.setUseRequestedId(requestId);
   } 
}
