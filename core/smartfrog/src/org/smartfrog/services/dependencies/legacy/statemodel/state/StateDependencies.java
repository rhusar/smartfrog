package org.smartfrog.services.dependencies.legacy.statemodel.state;


import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.services.dependencies.legacy.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.legacy.statemodel.exceptions.SmartFrogStateLifecycleException;

/**

 */
public interface StateDependencies extends Remote {
    public void register(DependencyValidation d) throws RemoteException, SmartFrogStateLifecycleException;
    public void deregister(DependencyValidation d) throws RemoteException, SmartFrogStateLifecycleException;
}

