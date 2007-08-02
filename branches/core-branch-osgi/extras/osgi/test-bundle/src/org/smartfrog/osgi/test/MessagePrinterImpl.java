package org.smartfrog.osgi.test;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

public class MessagePrinterImpl extends PrimImpl {

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        Compound subprocess = (Compound) sfResolve(Reference.fromString("ATTRIB subprocess"));
        System.out.println("subprocess: " + subprocess);
        try {
            Object messageSource = sfResolve("messageSource");
        }
        // Remote call        
        System.out.println(messageSource);
    }
}
