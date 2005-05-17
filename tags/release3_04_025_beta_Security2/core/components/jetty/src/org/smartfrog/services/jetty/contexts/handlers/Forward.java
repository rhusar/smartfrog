package org.smartfrog.services.jetty.contexts.handlers;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.jetty.contexts.ServletContextIntf;
import org.mortbay.http.handler.ForwardHandler;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.servlet.ServletHttpContext;

/**
 * A Forward handler class for jetty server
 * @author Ritu Sabharwal
 */


public class Forward extends HandlerImpl  implements ForwardIntf {
    private Reference mapfromPathRef = new Reference(MAP_FROM_PATH);
    private Reference maptoPathRef = new Reference(MAP_TO_PATH);

    private String mapfromPath = "/forward/*";
    private String maptoPath = "/dump";

    private ForwardHandler fwdhandler = new ForwardHandler();

  
  /** Standard RMI constructor */
   public Forward() throws RemoteException {
       super();
   }
   
   /** 
   * sfDeploy: adds the Forward Handler to ServetletHttpContext of jetty server 
   * @exception  SmartFrogException In case of error while deploying  
   * @exception  RemoteException In case of network/rmi error  
   */  
   public void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();      
       
       mapfromPath = sfResolve(mapfromPathRef, mapfromPath, false);
       maptoPath = sfResolve(maptoPathRef, maptoPath, false);
       fwdhandler.addForward(mapfromPath, maptoPath);
       addHandler(fwdhandler);
   }
}
