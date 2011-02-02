package org.smartfrog.services.persistence.test.testcases;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;


public interface Hello extends Remote {
    public void hello(String client) throws RemoteException, SmartFrogException;
    public void goodbye(String client) throws RemoteException, SmartFrogException;
}
