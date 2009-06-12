package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 */
public interface StateChangeNotification extends Remote {
   //child down to State, where it is handled
   public void handleStateChange() throws RemoteException;
   public String getStatusAsString() throws RemoteException;
}
