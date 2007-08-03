package org.smartfrog.osgi.test;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

public class MessagePrinterImpl extends PrimImpl {

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        MessageProvider messageSource = (MessageProvider) sfResolve("messageSource");
        // Remote call        
        System.out.println(messageSource.getMessage());
    }
}
