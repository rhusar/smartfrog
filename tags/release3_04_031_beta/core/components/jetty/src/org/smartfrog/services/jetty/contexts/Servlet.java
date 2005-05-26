package org.smartfrog.services.jetty.contexts;

import java.rmi.RemoteException;
import java.io.File;
import org.mortbay.http.HttpServer;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.jetty.JettyIntf;
import org.smartfrog.services.jetty.JettyHelper;
import org.mortbay.http.handler.ResourceHandler;


/**
 * A ServletHttp context class for a Jetty http server.
 * @author Ritu Sabharwal
 */

public class Servlet extends CompoundImpl implements ServletContextIntf {
    Reference contextPathRef = new Reference(CONTEXT_PATH);
    Reference resourceBaseRef = new Reference(RESOURCE_BASE);
    Reference classPathRef = new Reference(CLASSPATH);
    
    String jettyhome = ".";
    String contextPath = "/";
    String resourceBase = "\\demo\\docRoot";
    String classPath = null;
    String mapfromPath;
    String maptoPath;


    JettyHelper jettyHelper = new JettyHelper(this);

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
       sfAddAttribute(CONTEXT, context);
       server = jettyHelper.bindToServer();
       jettyhome = jettyHelper.findJettyHome();
       contextPath = sfResolve(contextPathRef, contextPath, true);
       resourceBase = sfResolve(resourceBaseRef,resourceBase, true);
       if (! new File(resourceBase).exists()){ 
         resourceBase = jettyhome.concat(resourceBase);
       }
       classPath = sfResolve(classPathRef, classPath, false);
       if ( classPath != null) {
	       if (!new File(classPath).exists())
		       classPath = jettyhome.concat(classPath);
       }
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
       jettyHelper.terminateContext(context);
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
