package org.smartfrog.osgi.test;

import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

public interface MessageProvider extends Prim {
    Object getMessage() throws RemoteException;
}
