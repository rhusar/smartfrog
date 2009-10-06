package org.smartfrog.services.cddlm.cdl.base;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 */
public interface LifecycleSource extends Remote {
    /**
     * bind to something listening for lifecycle events.
     * No events are raised at this point.
     *
     * @param uri job ID
     * @param target remote interface to forward events
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     * @throws java.rmi.RemoteException
     */
    void subscribe(String uri, LifecycleListener target) throws
            SmartFrogException,
            RemoteException;

    /**
     * unsubscribe. This is idempotent
     * @param subscriber
     * @throws SmartFrogException
     * @throws RemoteException
     * @return true if the listener was unsubscribed
     */
    boolean unsubscribe(LifecycleListener subscriber) throws
            SmartFrogException,
            RemoteException;
}

