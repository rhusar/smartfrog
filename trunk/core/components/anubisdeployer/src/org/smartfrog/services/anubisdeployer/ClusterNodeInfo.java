package org.smartfrog.services.anubisdeployer;

import java.rmi.*;

import java.net.*;

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
	hr.setHostNodeDescription(data);
    }


    public synchronized void sfTerminateWith(TerminationRecord tr) {
	super.sfTerminateWith(tr);
    }

}
