package org.smartfrog.services.persistence.test.testcases;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Tester extends Remote {
    public void doTest() throws RemoteException, Exception;
}
