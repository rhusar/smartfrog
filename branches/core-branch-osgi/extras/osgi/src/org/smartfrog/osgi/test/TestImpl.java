package org.smartfrog.osgi.test;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

public class TestImpl extends PrimImpl implements Test {
    private String message = null;

    public TestImpl() throws RemoteException {}

    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        message = (String) sfResolve("message");
        if (message == null) message = "Default Message !"; 
    }

    public void sayHello() throws RemoteException {
        System.out.println(message);
    }
}
