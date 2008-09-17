package org.smartfrog.services.dependencies.statemodel.state;

import java.util.HashMap;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**

 */
public interface StateSaving extends Remote {
   public void saveState(HashMap data)  throws RemoteException;
}
