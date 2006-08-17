package org.smartfrog.sfcore.updatable;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogUpdateException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.util.Iterator;


/**
 * warning: handle in a thread that is not part of a lifecycle of another component...
 */
public class UpdatablePrim extends PrimImpl implements Update, Prim {
    boolean updateAbandoned = false;

    public UpdatablePrim() throws RemoteException {

    }

    /* flow update lifecycle */

    /**
     * Inform component (and children, typically) that an update is about to take place.
     * Normally a component would quiesce its activity
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException - not OK to update
     */
    public synchronized void sfPrepareUpdate() throws RemoteException, SmartFrogException {
        // iterate over all children, preparing them for update.
        // if an exception is returned, trigger an abandon downwards and retun an exception
        updateAbandoned = false;
    }

    Context newContext;

    /**
     * Validate whether the component (and its children) can be updated
     * @param newCxt - the data that will replace the original context
     * @return true - OK to update, false - OK to terminate and redeploy, exception - not OK to update
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException - failure, not OK to update
     */
    public synchronized boolean sfUpdateWith(Context newCxt) throws RemoteException, SmartFrogException {
        if (updateAbandoned) throw new SmartFrogUpdateException("update already abandoned");
        // validate the description, return false if it requires termination, exception to fail
        // cache context
        // check children that exist already
        //     identify those that should be terminated  (returned false)
        //     those to be updated (return true)
        // return true
        newContext = (Context) newCxt.copy();

        // check that all sf attributes are well defined...
        for (Iterator  i = newContext.sfAttributes(); i.hasNext(); ) {
            String key = i.next().toString();
            if (key.startsWith("sf")) {
                try {
                    Object myValue = sfResolve(key, true);
                    if (!myValue.equals(newContext.get(key))) {
                        return false;  // non matching sf attribute
                    }
                } catch (SmartFrogResolutionException e) {
                    return false;  // there is a new sf attribute
                } catch (RemoteException e) {
                    sfAbandonUpdate();
                    throw new SmartFrogUpdateException("remote error during update", e);
                }
            }
        }

        // if they are, then make sure that all sf attributes in the current comopnent are in the
        // new context
        for (Iterator  i = sfContext.sfAttributes(); i.hasNext(); ) {
            String key = i.next().toString();
            if (key.startsWith("sf")) {
                newContext.put(key, sfContext.get(key));
            }
        }

        return true;
    }

    /**
     * Carry out the context update - no roll back from this point on.
     * Terminates children that need terminating, create and deployWith children that need to be
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException - failure, to be treated like a normal lifecycle error, by default with termination
     */
    public synchronized void sfUpdate() throws RemoteException, SmartFrogException {
        if (updateAbandoned) throw new SmartFrogUpdateException("update already abandoned");
        // update context
        sfContext = newContext;
        // failure considered terminal
    }


    /**
     * Next phase of start-up after update - includes calling sfDeply on new children
     * Errors are considered terminal unless behaviour overridden.
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */

    public synchronized void sfUpdateDeploy() throws RemoteException, SmartFrogException {
        if (updateAbandoned) throw new SmartFrogUpdateException("update already abandoned");
    }

    /**
     * Final phase of startup after update - includes calling sfStart on new children
     * Errors are considered terminal unless behaviour overridden.
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public synchronized void sfUpdateStart() throws RemoteException, SmartFrogException {
        if (updateAbandoned) throw new SmartFrogUpdateException("update already abandoned");
    }

    /**
     * Can occur after prepare and check, but not afterwards to roll back from actual update process.
     * @throws java.rmi.RemoteException
     */
    public synchronized void sfAbandonUpdate() throws RemoteException {
        // notify all children of the abandon, ignoring all errors?
        // only occurs after failure of prepare or updatewith, future failure considered fatal
        if (updateAbandoned) return;
        updateAbandoned = true;
    }

    /**
     * Control of complete update process for a component, running through all the above phases.
     * @param desc
     * @throws java.rmi.RemoteException
     * @throws org.smartfrog.sfcore.common.SmartFrogUpdateException
     */
    public void sfUpdateComponent(ComponentDescription desc) throws RemoteException, SmartFrogUpdateException {
        boolean ready;

        try {
            System.out.println("preparing");
            this.sfPrepareUpdate();
            System.out.println("preparing done");

            System.out.println("update with");
            ready = this.sfUpdateWith(desc.sfContext());
            if (!ready) throw new SmartFrogUpdateException("top level component must accept update", null);
            System.out.println("update with done");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                System.out.println("abandoning");
                this.sfAbandonUpdate();
                System.out.println("abandoning done");
            } catch (RemoteException e1) {
                // ignore?
            }

            if (e instanceof SmartFrogUpdateException)
                throw (SmartFrogUpdateException) e;
            else
                throw new SmartFrogUpdateException("error in update, abandoning", e);
        }

        if (ready) {
            try {
                System.out.println("update");
                this.sfUpdate();
                System.out.println("update done\nupdate deploy");
                this.sfUpdateDeploy();
                System.out.println("update deploy done\nupdate start");
                this.sfUpdateStart();
                System.out.println("update start done");
            } catch (Exception e) {
                System.out.println("failed");
                e.printStackTrace();
                try {
                    this.sfTerminate(TerminationRecord.abnormal("fatal error in update - terminated comopnents", sfCompleteNameSafe(), e));
                } catch (Exception e1) {
                    // ignore?
                }
                throw new SmartFrogUpdateException("fatal error in update, terminating application", e);
            }
        }
    }
}
