/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.sfcore.workflow.combinators;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.NoSuchElementException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.CreateNewChildThread;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;

/**
 * Parallel is a modified compound which differs in that the sub-components
 * operate in parallel but do not share the same lifecycle, and in particular
 * the same termination. A Parallel combinator creates no subcomponents until
 * it's sfStart phase at which point all the subcomponents are created in the
 * normal way and with synchronized or asynchronized lifecycle. The Parallel combinator waits
 * for each of its sub-components to terminate normally at which point it too
 * terminates normally. If an error occurs at any point, or a sub-component
 * terminates abnormally, the Parallel combinator does too.
 *
 * <p>
 * The file parallel.sf contains the SmartFrog configuration file for the base
 * Parallel combinator. This file contains the details of the attributes which
 * may be passed to Parallel.
 * </p>
 */
public class Parallel extends EventCompoundImpl implements Compound {

    private static Reference asynchCreateChildRef = new Reference ("asynchCreateChild");
    /** {@value} */
    public static final String ATTR_TERMINATE_IF_EMPTY = "terminateOnEmptyDeploy";
    private static Reference terminateIfEmptyRef = new Reference(ATTR_TERMINATE_IF_EMPTY);
    private boolean asynchCreateChild=false;
    private boolean terminateIfEmpty=false;
    private Vector asynchChildren = null;

    /**
     * Constructs Parallel.
     *
     * @throws java.rmi.RemoteException In case of network or RMI failure.
     */
    public Parallel() throws RemoteException {
        super();
    }

    /**
     * Reads the basic configuration of the component and deploys.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        asynchCreateChild = sfResolve(asynchCreateChildRef,asynchCreateChild,false);
        terminateIfEmpty = sfResolve(terminateIfEmptyRef, terminateIfEmpty, false);
    }

    /**
     * Deploys and manages the parallel subcomponents.
     *
     * @throws RemoteException The required remote exception.
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        TerminationRecord terminationRecord = null;
        if (!actions.isEmpty()) {
// let any errors be thrown and caught by SmartFrog for abnormal termination  - including empty actions
            try {
                if (!asynchCreateChild){
                    if (sfLog().isDebugEnabled()){sfLog().debug(" Parallel Synch");};
                    synchCreateChildren();
                } else {
                    if (sfLog().isDebugEnabled()){sfLog().debug(" Parallel Asynch");};
                    asynchCreateChildren();
                }
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()){
                  sfLog().error(sfCompleteNameSafe()+" - Failed to start sub-components ",ex);
                }
                terminationRecord = TerminationRecord
                        .abnormal("Failed to start sub-components " + ex, name);
                //sfTerminate(terminationRecord);
            }
        } else {
            //no actions. Maybe terminate 
            if(terminateIfEmpty) {
                terminationRecord = new TerminationRecord(TerminationRecord.NORMAL,
                        "Parallel component is empty",name);
            }
        }
        if (terminationRecord!=null) {
            new ComponentHelper(this).targetForWorkflowTermination(terminationRecord);
        }

    }


    /**
     * Create the children of parallel, each in their own thread.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    protected void asynchCreateChildren() throws RemoteException, SmartFrogException {
            asynchChildren = new Vector();
            actionKeys = actions.keys();
            try {
                while (actionKeys.hasMoreElements()) {
                    Object key = actionKeys.nextElement();
                    ComponentDescription act = (ComponentDescription) actions.get(key);
                    //asynchChildren.add();
                    Thread thread = new CreateNewChildThread(key,this,act, null,this);
                    asynchChildren.add(thread);
                    thread.start();
                    if (sfLog().isDebugEnabled()) sfLog().debug("Creating "+key);
                }
            } catch (NoSuchElementException ignored){
               throw new SmartFrogRuntimeException ("Found no children to deploy",this);
            }
    }


    /**
     * If normal termination, Parallel behaviour is to terminate
     * that component but leave the others running if it is the last -
     * terminate normally. if an erroneous termination -
     * terminate immediately passing on the error
     *
     *
     * @param status exit record of the component
     * @param comp   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        boolean forward = true;
        if (status.isNormal()) {
            try {
                sfRemoveChild(comp);
                forward = !sfChildren().hasMoreElements();
            } catch (Exception e) {
                if (sfLog().isErrorEnabled()) {
                    sfLog().error(sfCompleteNameSafe() + " - error handling child termination ", e);
                }
                sfTerminate(TerminationRecord.abnormal("error handling child termination " + e, name));
                forward = false;
            }
        }
        return forward;
    }

    /**
     * Cancels all remaining createChild threads
     *
     * @param status Termination  Record
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {

        /* unregister from all remote registrations */
        if  (asynchChildren!=null){
            for (Enumeration e = asynchChildren.elements(); e.hasMoreElements(); ) {
                CreateNewChildThread t = (CreateNewChildThread)e.nextElement();
                boolean  sfSyncTerminate = false;
                try {
                    sfSyncTerminate = sfResolve(SmartFrogCoreKeys.SF_SYNC_TERMINATE, sfSyncTerminate, false);
                } catch (Exception sfrex){
                    //Ignore
                }
                try {
                    t.cancel(sfSyncTerminate,true);
                } catch (Exception ignored) {
                }
            }
        }
        super.sfTerminateWith(status);
    }
}
