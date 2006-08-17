package org.smartfrog.sfcore.updatable;

import java.rmi.RemoteException;
import java.rmi.Remote;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Update interface for a composite and its contents to match a new description
 */
public interface Update extends Remote {
    public void sfPrepareUpdate() throws RemoteException, SmartFrogException;

    public boolean sfUpdateWith(Context newContext) throws RemoteException, SmartFrogException;

    public void sfUpdate() throws RemoteException, SmartFrogException;

    public void sfUpdateDeploy() throws RemoteException, SmartFrogException;

    public void sfUpdateStart() throws RemoteException, SmartFrogException;

    public void sfAbandonUpdate() throws RemoteException;

    public void sfUpdateComponent(ComponentDescription desc) throws RemoteException, SmartFrogException;
}
