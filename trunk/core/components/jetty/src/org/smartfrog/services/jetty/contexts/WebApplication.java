package org.smartfrog.services.jetty.contexts;

import java.rmi.RemoteException;
import org.mortbay.http.HttpServer;
import org.smartfrog.services.jetty.contexts.Context;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.ServletHandler;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.jetty.SFJetty;
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
       Prim parent = this.sfParent();
       Prim grandParent = parent.sfParent();
       jettyhome = (String)grandParent.sfResolveId("jettyhome");
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
	   SFJetty.server.addContext(context);
   }   
  
   /**
   * Termination phase
   */
   public void sfTerminateWith(TerminationRecord status) {
	   SFJetty.server.removeContext(context);
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
