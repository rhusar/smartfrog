package org.smartfrog.services.jetty.contexts.handlers;

import java.rmi.RemoteException;
import java.util.logging.Handler;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.jetty.contexts.ServletContextIntf;
import org.smartfrog.services.jetty.JettyHelper;
import org.mortbay.http.handler.DumpHandler;
import org.mortbay.http.HttpHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;

/**
 * A Dump handler class for jetty server
 * @author Ritu Sabharwal
 */


public class Dump extends HandlerImpl implements DumpIntf {

    /** Standard RMI constructor */
   public Dump() throws RemoteException {
	   super();
   }
   
   /** 
   * sfDeploy: adds the Dump Handler to ServetletHttpContext of jetty server 
   * @exception  SmartFrogException In case of error while deploying  
   * @exception  RemoteException In case of network/rmi error  
   */  
   public void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();   
        addHandler(new DumpHandler());
   }

}
