package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**

 */
public interface NotificationLock extends Remote {
   public void lock() throws RemoteException;
   public void unlock(boolean notify) throws RemoteException;
   
   public void notifyStateChange() throws RemoteException;

   public void threadStarted() throws RemoteException;
   public void threadStopped() throws RemoteException;
}
