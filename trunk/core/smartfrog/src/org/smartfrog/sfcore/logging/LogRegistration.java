package org.smartfrog.sfcore.logging;


import java.rmi.Remote;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.SmartFrogLogException;


public interface LogRegistration extends Remote {

   public void register(String name,Log log)  throws RemoteException, SmartFrogLogException;

   public void register(String name,Log log, int logLevel)  throws RemoteException, SmartFrogLogException;

   public boolean deregister(String name)  throws RemoteException, SmartFrogLogException;

}
