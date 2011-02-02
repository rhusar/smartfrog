/** (C) Copyright Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.anubisdeployer;

import java.rmi.*;


import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public class ClusterNodeInfo extends PrimImpl implements Prim {

    private ClusterNode hr = null;
    private ComponentDescription data = null;

    // /////////////////////////////////////////////////////
    //
    // Constructor method
    //
    // /////////////////////////////////////////////////////


    public ClusterNodeInfo() throws RemoteException {
    }

    // /////////////////////////////////////////////////////
    //
    // Template methods
    //
    // /////////////////////////////////////////////////////


    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
	super.sfDeploy();
	hr = (ClusterNode)sfResolve("clusterNodeManager", true);
	data = (ComponentDescription) sfResolve("nodeDescription", true);
	//data.sfReplaceAttribute("hostname", ((InetAddress) sfResolve("sfHost")).getCanonicalHostName());
    }


    public synchronized void sfStart() throws SmartFrogException, RemoteException {
	super.sfStart();
	hr.setHostNodeDescription((ComponentDescription) data.copy());
    }


    public synchronized void sfTerminateWith(TerminationRecord tr) {
	super.sfTerminateWith(tr);
    }

}
