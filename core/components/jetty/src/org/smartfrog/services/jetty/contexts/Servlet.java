package org.smartfrog.services.jetty.contexts;

import java.rmi.RemoteException;

import org.mortbay.http.HttpServer;
import org.smartfrog.services.jetty.contexts.Context;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.mortbay.http.handler.ResourceHandler;


/**
 * A ServletHttp context class for a Jetty http server.
 * @author Ritu Sabharwal
 */


public class Servlet extends CompoundImpl implements Compound {
   Reference contextPathRef = new Reference("contextPath");
   Reference resourceBaseRef = new Reference("resourceBase");
   Reference classPathRef = new Reference("classPath");

   String jettyhome = ".";
   String contextPath = "/";
   String resourceBase = "\\demo\\docRoot";
   String classPath = "null";
   String mapfromPath;
   String maptoPath;

   ProcessCompound process = null;

   HttpServer server = null;
   
   ServletHttpContext context;
   
   /** Standard RMI constructor */
       public Servlet() throws RemoteException {
       super();
       }
    
   /**
   * Deploy the ServletHttpContext
   * @exception  SmartFrogException In case of error while deploying  
   * @exception  RemoteException In case of network/rmi error  
   */    
   public void sfDeploy() throws SmartFrogException, RemoteException {
       context =  new ServletHttpContext();
       sfAddAttribute("Context", context);
       process = SFProcess.getProcessCompound();
       server = (HttpServer)process.sfResolveId("Jetty Server");
       jettyhome = (String)process.sfResolveId("jettyhome");
       contextPath = sfResolve(contextPathRef, contextPath, true);
       resourceBase = sfResolve(resourceBaseRef, 
		       jettyhome + resourceBase, true);
       classPath = sfResolve(classPathRef, classPath, false);
       super.sfDeploy();      
       }
   
   /**
   * sfStart: adds the ServletHttpContext to the jetty server
   * 
   * @exception  SmartFrogException In case of error while starting  
   * @exception  RemoteException In case of network/rmi error 
   */ 
   public void sfStart() throws SmartFrogException, RemoteException {
       super.sfStart();
       addcontext(contextPath,resourceBase,classPath);
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
		  Logger.log(" Interrupted on ServletHttpContext termination " + ex);
	  }
	   server.removeContext(context);
           super.sfTerminateWith(status);
   }
     
   /**
   * Add the context to the http server
   * @exception  RemoteException In case of network/rmi error 
   */ 
   public void addcontext(String contextPath, String resourceBase, String 
   classPath) throws RemoteException{
	  context.setContextPath(contextPath);
	  context.setResourceBase(resourceBase);
	  context.setClassPath(classPath);
	  context.addHandler(new ResourceHandler());
   } 

}
