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


public class Forward extends PrimImpl implements ForwardIntf {
    Reference mapfromPathRef = new Reference(MAP_FROM_PATH);
    Reference maptoPathRef = new Reference(MAP_TO_PATH);
   
   String mapfromPath = "/forward/*";
   String maptoPath = "/dump";
   
   ForwardHandler fwdhandler = new ForwardHandler();
   ServletHttpContext a;   
   
  
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
       fwdhandler.addForward(mapfromPath,maptoPath);
       Prim parent = this.sfParent();
       Prim grandParent = parent.sfParent();
       ServletHttpContext cxt = (ServletHttpContext)grandParent.
	       				sfResolveId(ServletContextIntf.CONTEXT);
       cxt.addHandler(fwdhandler);  
   }
}
