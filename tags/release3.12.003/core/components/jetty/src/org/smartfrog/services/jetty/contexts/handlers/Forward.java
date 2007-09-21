/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.services.jetty.contexts.handlers;

import org.mortbay.http.handler.ForwardHandler;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

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

  
    /**
     * Standard RMI constructor
     * @throws RemoteException In case of network/rmi error  
     */
   public Forward() throws RemoteException {
       super();
   }
   
   /** 
   * sfDeploy: adds the Forward Handler to ServetletHttpContext of jetty server 
   * @exception  SmartFrogException In case of error while deploying  
   * @exception  RemoteException In case of network/rmi error  
   */  
   public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();      
       
       mapfromPath = sfResolve(mapfromPathRef, mapfromPath, false);
       maptoPath = sfResolve(maptoPathRef, maptoPath, false);
       fwdhandler.addForward(mapfromPath, maptoPath);
       addHandler(fwdhandler);
   }
}
