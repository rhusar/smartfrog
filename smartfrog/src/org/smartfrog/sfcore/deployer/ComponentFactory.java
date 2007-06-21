package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

public interface ComponentFactory extends Prim {
    Prim getComponent(ComponentDescription askedFor) throws RemoteException, SmartFrogDeploymentException;
}
