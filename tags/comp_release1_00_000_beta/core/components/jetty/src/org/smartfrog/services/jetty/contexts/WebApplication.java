package org.smartfrog.services.jetty.contexts;

import java.rmi.RemoteException;
import java.io.File;

import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.ServletHandler;
import org.smartfrog.services.jetty.JettyIntf;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.PlatformHelper;
/**
 * A WebApplication context class for jetty server 
 * @author Ritu Sabharwal
 */


public class WebApplication extends PrimImpl implements JettyWebApplicationContext {
    Reference contextPathRef = new Reference(CONTEXT_PATH);
    Reference webAppRef = new Reference(WEBAPP);
    Reference requestIdRef = new Reference(REQUEST_ID);
    JettyHelper jettyHelper=new JettyHelper(this);
   String jettyhome = ".";
   String contextPath = "/";
   String webApp = null;
   String serverName = null;
   boolean requestId = false;

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

   }

   /**
   * sfStart: adds the WebApplication context to the jetty server
   *
   * @exception  SmartFrogException In case of error while starting
   * @exception  RemoteException In case of network/rmi error
   */
   public void sfStart() throws SmartFrogException, RemoteException {
	   super.sfStart();
       server = jettyHelper.bindToServer();
       jettyhome = jettyHelper.findJettyHome();
       contextPath = sfResolve(contextPathRef, contextPath, true);
       //fetch the webapp reference by doing filename resolution
       //if the file exists, it does not need to be anywhere
       webApp = sfResolve(webAppRef, webApp, false);
       if (webApp != null) {
	       if (!new File(webApp).exists())
		       webApp = jettyhome.concat(webApp);
       }
       //no webapp? look for the warfile
       if (webApp == null) {
           webApp = FileImpl.lookupAbsolutePath(this, WARFILE, null, null, true, null);
       }
       //sanity check
       File webappFile = new File(webApp);
       if (!webappFile.exists()) {
           throw new SmartFrogDeploymentException("Web application " + webappFile + " was not found");
       }
       //request ID
       requestId = sfResolve(requestIdRef, requestId, false);
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
       jettyHelper.terminateContext(context);
       super.sfTerminateWith(status);
   }
  
   /**
   * Add the context to the http server
   * @exception  RemoteException In case of network/rmi error 
   */ 
   public void addcontext(String contextpath, String webApp, boolean requestId)
           throws RemoteException {
       context.setContextPath(contextPath);
       context.setWAR(webApp);
       ServletHandler servlethandler = context.getServletHandler();
       AbstractSessionManager sessionmanager = (AbstractSessionManager)
               servlethandler.getSessionManager();
       sessionmanager.setUseRequestedId(requestId);
   } 
}
