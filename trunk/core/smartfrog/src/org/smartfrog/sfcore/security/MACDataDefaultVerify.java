package org.smartfrog.sfcore.security;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;

public class MACDataDefaultVerify extends RequireSecurityImpl {

    public MACDataDefaultVerify() throws RemoteException {
        super();
    }

    /**
     * Verify that MACData objects can be initialized. If the deamon is running in secure 
     * mode this will pick up a key from the key store, testing that the key is
     * in the key store and the key store works. If the security is not enabled, a key is 
     * statically defined locally and should always work. So this should always work unless 
     * security is enabled and there is no key defined.
     */
    @Override
    protected void checkSecurityOnStartup() throws SmartFrogLifecycleException {
        super.checkSecurityOnStartup();
        try {
            MACData macData = new MACData();
        } catch (SmartFrogException e) {
            throw (SmartFrogLifecycleException)SmartFrogLifecycleException.forward("Failed to initialise MACData object", e);
        }
    }
}
