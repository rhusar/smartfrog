package org.smartfrog.services.anubisdeployer;


import java.rmi.RemoteException;
import java.rmi.Remote;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public interface ClusterStatus extends Remote{

    public void clusterStatus(ComponentDescription status)
	throws RemoteException;

}
