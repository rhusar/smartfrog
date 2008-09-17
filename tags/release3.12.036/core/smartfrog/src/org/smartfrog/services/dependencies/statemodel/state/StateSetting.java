package org.smartfrog.services.dependencies.statemodel.state;

import java.util.HashMap;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**

 */
public interface StateSetting extends Remote {
   //method called on "this" to inform of new state in sub-classes
   public void setState(HashMap data)  throws RemoteException;
}
