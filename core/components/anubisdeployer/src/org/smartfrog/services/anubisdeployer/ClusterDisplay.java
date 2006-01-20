package org.smartfrog.services.anubisdeployer;

import java.rmi.RemoteException;

import org.smartfrog.services.display.SFDisplay;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class ClusterDisplay extends SFDisplay implements Prim, ClusterStatus {

    ClusterMonitor clusterMonitor;

    public ClusterDisplay() throws RemoteException {
    }


    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
	super.sfDeploy();
	clusterMonitor = (ClusterMonitor)sfResolve("clusterMonitor", true);
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
	super.sfStart();
	clusterMonitor.registerForClusterStatus(this);
    }

    public synchronized void sfTerminateWith(TerminationRecord t) {
	try {
	    clusterMonitor.deregisterForClusterStatus(this);
	} catch (Exception e) {}
    }


    public void clusterStatus(ComponentDescription d) throws RemoteException {
	display.setTextScreen(d.toString());
    }
}
