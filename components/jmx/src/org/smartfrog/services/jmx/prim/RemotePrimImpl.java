/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.prim;

import java.rmi.*;
import org.smartfrog.sfcore.prim.*;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class RemotePrimImpl extends PrimImpl implements Prim {

    /**
     *  Constructor for the RemotePrimImpl object
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public RemotePrimImpl() throws RemoteException {
        super();
    }


//    public void sfStart() throws RemoteException, Exception {
//        super.sfStart();
//    try {
//      Naming.bind("rmi://localhost:3800/"+this.sfCompleteName().toString(), (Prim)this);
//      String[] list = Naming.list("rmi://localhost:3800/");
//      for (int i=0; i<list.length; i++) {
//        System.out.println("Bound object "+i+": "+list[i]);
//      }
//    }
//    catch (Exception e) {
//      e.printStackTrace();
//      java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.createRegistry(3800);
//      Naming.bind("rmi://localhost:3800/"+this.sfCompleteName().toString(), this);
//    }
//    }

}
