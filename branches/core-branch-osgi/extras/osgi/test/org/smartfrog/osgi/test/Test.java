package org.smartfrog.osgi.test;

import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

public interface Test extends Prim {
    void sayHello() throws RemoteException;
}
