package org.smartfrog.services.jetty.contexts.handlers;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.mortbay.http.handler.HTAccessHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;

/**
 * A HTAccess handler class for jetty server
 * @author Ritu Sabharwal
 */


public class HTAccess extends PrimImpl implements Prim {
   Reference accessFileRef = new Reference("accessFile");
   
   String accessFile = ".htaccess";
   
   HTAccessHandler hthandler = new HTAccessHandler(); 
   
   /** Standard RMI constructor */
   public HTAccess() throws RemoteException {
	   super();
   }
   
   /** 
   * sfDeploy: adds the HTAccess Handler to ServetletHttpContext of jetty server
   * @exception  SmartFrogException In case of error while deploying  
   * @exception  RemoteException In case of network/rmi error  
   */   
   public void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();      
       accessFile = sfResolve(accessFileRef, accessFile, false);
       hthandler.setAccessFile(accessFile);
       Prim parent = this.sfParent();
       Prim grandParent = parent.sfParent(); 
       ServletHttpContext cxt = (ServletHttpContext)grandParent.
	       				sfResolveId("Context"); 
       cxt.addHandler(hthandler); 
   }
}
