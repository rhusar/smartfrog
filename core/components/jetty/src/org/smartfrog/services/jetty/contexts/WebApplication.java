package org.smartfrog.services.jetty.contexts;

import java.rmi.RemoteException;
import java.io.File;

import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.ServletHandler;
import org.smartfrog.services.jetty.JettyIntf;
import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Logger;
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
   

   String jettyhome = ".";
   String contextPath = "/";
   String webApp = null;
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
       server = (HttpServer)process.sfResolveId(JettyIntf.JETTY_SERVER); 
       jettyhome = (String)process.sfResolveId(JettyIntf.JETTY_HOME);
       /* no, doesnt work w/ resolveID
       jettyhome = FileImpl.lookupAbsolutePath(this, JettyIntf.JETTY_HOME,
               null,
               new File("."),
               true,
               PlatformHelper.getLocalPlatform());
               */
       contextPath = sfResolve(contextPathRef, contextPath, true);
       //fetch the webapp reference by doing filename resolution
       //if the file exists, it does not need to be anywhere
       //webApp = sfResolve(webAppRef, webApp, true);
       FileImpl.lookupAbsolutePath(this,webAppRef,null,new File(jettyhome),true,PlatformHelper.getLocalPlatform());
       File webappFile=new File(webApp);
       if(!webappFile.exists()) {

       }
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
		  Logger.log(" Interrupted on WebApplicationContext termination ",ex);
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
