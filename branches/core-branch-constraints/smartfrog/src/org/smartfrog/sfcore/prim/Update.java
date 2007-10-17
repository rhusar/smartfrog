package org.smartfrog.sfcore.prim;

import java.rmi.RemoteException;
import java.rmi.Remote;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Update interface for a Prim to match a new description
 *
 * warning: handle in a thread that is not part of a lifecycle of another component...
 */
public interface Update extends Remote {
    /**
     * Inform component (and children, typically) that an update is about to take place.
     * Normally a component would quiesce its activity
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException - not OK to update
     */
    public void sfPrepareUpdate() throws RemoteException, SmartFrogException;

    /**
     * Validate whether the component (and its children) can be updated
     * @param newContext - the data that will replace the original context
     * @return true - OK to update, false - OK to terminate and redeploy, exception - not OK to update
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException - failure, not OK to update
     */
    public boolean sfUpdateWith(Context newContext) throws RemoteException, SmartFrogException;

    /**
     * Carry out the context update - no roll back from this point on.
     * Terminates children that need terminating, create and deployWith children that need to be
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException - failure, to be treated like a normal lifecycle error, by default with termination
     */
    public void sfUpdate() throws RemoteException, SmartFrogException;

    /**
     * Next phase of start-up after update - includes calling sfDeply on new children
     * Errors are considered terminal unless behaviour overridden.
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public void sfUpdateDeploy() throws RemoteException, SmartFrogException;

    /**
     * Final phase of startup after update - includes calling sfStart on new children
     * Errors are considered terminal unless behaviour overridden.
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public void sfUpdateStart() throws RemoteException, SmartFrogException;

    /**
     * Can occur after prepare and check, but not afterwards to roll back from actual update process.
     * @throws java.rmi.RemoteException
     */
    public void sfAbandonUpdate() throws RemoteException;

     /**
     * Control of complete update process for a component, running through all the above phases.
     * @param desc
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogUpdateException
     */
     public void sfUpdateComponent(ComponentDescription desc) throws RemoteException, SmartFrogException;
}
