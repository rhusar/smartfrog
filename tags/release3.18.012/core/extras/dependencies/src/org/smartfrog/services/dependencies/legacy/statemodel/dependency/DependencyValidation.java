package org.smartfrog.services.dependencies.legacy.statemodel.dependency;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface DependencyValidation extends Remote {
   public boolean isEnabled() throws RemoteException;
}
