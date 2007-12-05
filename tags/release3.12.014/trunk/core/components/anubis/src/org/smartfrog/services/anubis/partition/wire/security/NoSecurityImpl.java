package org.smartfrog.services.anubis.partition.wire.security;

import java.io.IOException;
import java.rmi.RemoteException;

import org.smartfrog.services.anubis.partition.wire.Wire;
import org.smartfrog.services.anubis.partition.wire.WireFormException;
import org.smartfrog.services.anubis.partition.wire.WireMsg;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

public class NoSecurityImpl extends PrimImpl implements Prim, WireSecurity {

    public NoSecurityImpl() throws RemoteException {
        super();
    }

    public WireMsg fromWireForm(byte[] wireFrom) throws WireSecurityException, WireFormException {
        try {
            return Wire.fromWire(wireFrom);
        } catch (Exception e) {
            throw new WireFormException("Unable to unmarshall message");
        }
    }

    public byte[] toWireForm(WireMsg msg) throws WireFormException {
        try {
            return msg.toWire();
        } catch (IOException e) {
            throw new WireFormException("Unable to marshall message");
        }
    }

}
