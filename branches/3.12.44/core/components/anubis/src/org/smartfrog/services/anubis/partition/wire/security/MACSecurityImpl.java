package org.smartfrog.services.anubis.partition.wire.security;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.crypto.ShortBufferException;

import org.smartfrog.services.anubis.partition.wire.Wire;
import org.smartfrog.services.anubis.partition.wire.WireFormException;
import org.smartfrog.services.anubis.partition.wire.WireMsg;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.MACData;

public class MACSecurityImpl extends PrimImpl implements Prim, WireSecurity {
    
    private MACData macData = null;

    public MACSecurityImpl() throws RemoteException {
        super();
    }
    
    public void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy();
        macData = new MACData();
    }

    public WireMsg fromWireForm(byte[] wireForm) throws WireSecurityException, WireFormException {
        try {
            
            WireMsg msg = Wire.fromWire(wireForm); 
            macData.checkMAC(wireForm, 0, msg.getSize()-1);
            
            return msg;
        } catch (ClassNotFoundException e) {
            throw new WireFormException("Unable to unmarshall message", e);
        } catch (IOException e) {
            throw new WireFormException("Unable to unmarshall message", e);
        } catch(SecurityException e) {
            throw new WireSecurityException("Security violation unmarshalling message", e);
        }
    }

    public byte[] toWireForm(WireMsg msg) throws WireFormException {
        try {
            
            msg.setTrailerSize(macData.getMacSize());
//            msg.setTrailerSize(100);
            byte[] wireForm = msg.toWire(); 
            macData.addMAC(wireForm, 0, msg.getSize()-1);
            return wireForm;
            
        } catch (IOException e) {
            throw new WireFormException("Unable to marshall message", e);
        } catch (ShortBufferException e) {
            throw new WireFormException("Unable to marshall message - buffer not large enough for MAC security data", e);
        } catch (SecurityException e) {
            throw new WireFormException("Unable to marshall message - security issue", e);
        }
    }

}
