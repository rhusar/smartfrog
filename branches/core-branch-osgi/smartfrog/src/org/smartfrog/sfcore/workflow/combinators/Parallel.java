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

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.CreateNewChildThread;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

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

    public static final String ATTR_ASYNCH_CREATE_CHILD = "asynchCreateChild";
    private static final Reference asynchCreateChildRef = new Reference (ATTR_ASYNCH_CREATE_CHILD);
    /** {@value} */
    public static final String ATTR_TERMINATE_IF_EMPTY = "terminateOnEmptyDeploy";
    /** {@value} */
    public static final String ATTR_TERMINATE_IF_CHILD_TERMINATES_ABNORMAL = "terminateOnAbnormalChildTermination";
    /** {@value} */
    public static final String ATTR_TERMINATE_IF_CHILD_DEPLOYS_ABNORMAL = "terminateOnAbnormalChildDeploy";
    private static final Reference terminateIfEmptyRef = new Reference(ATTR_TERMINATE_IF_EMPTY);
    private boolean asynchCreateChild=false;
    private boolean terminateIfEmpty=false;
    private boolean terminateOnAbnormalChildTermination=true;
    private boolean terminateOnAbnormalChildDeploy = true;
    private Vector asynchChildren;
    private Vector results;
    /**
     * A counter to catch (and ignore) terminations during
     * asynchronous startups, except for the last one.
     */
    private volatile int pendingDeployments=0;

    /**
     * Termination message.
     * {@value}
     */
    public static final String TERMINATION_ABNORMAL_CHILD = "Terminating normally even though a child terminated abnormally";
    /**
     * Termination message.
     * {@value}
     */
    public static final String TERMINATION_ERROR_REMOVING_THE_CHILD = "Error removing the child";
    public static final String WORKER_FAILED = "Worker failed";
    public static final String TERMINATE_FAILURE_WHILE_STARTING_SUB_COMPONENTS = "Failure while starting sub-components ";
    public static final String TERMINATE_FAILED_TO_START_SUB_COMPONENTS = "Failed to start sub-components ";
    public static final String TERMINATE_PARALLEL_COMPONENT_IS_EMPTY = "Parallel component is empty";

    /**
     * Constructs Parallel.
     *
     * @throws RemoteException In case of network or RMI failure.
     */
    public Parallel() throws RemoteException {
        super();
    }

    /*
     * These methods are here
     *
     */
    private synchronized int getPendingDeployments() {
        return pendingDeployments;
    }

    /**
     * We have pending deployments if there is at least one in the queue
     * @return true iff there was a pending deployment when the test was made
     */
    private boolean hasPendingDeployments() {
        return getPendingDeployments()>0;
    }

    private synchronized void setPendingDeployments(int pendingDeployments) {
        this.pendingDeployments = pendingDeployments;
    }

    private synchronized int decrementPendingDeployments() {
        pendingDeployments--;
        return pendingDeployments;
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
        asynchChildren = new Vector(0);
        asynchCreateChild = sfResolve(asynchCreateChildRef,asynchCreateChild,false);
        terminateIfEmpty = sfResolve(terminateIfEmptyRef, terminateIfEmpty, false);
        terminateOnAbnormalChildTermination = sfResolve(ATTR_TERMINATE_IF_CHILD_TERMINATES_ABNORMAL,
                terminateOnAbnormalChildTermination, true);
        terminateOnAbnormalChildDeploy = sfResolve(ATTR_TERMINATE_IF_CHILD_DEPLOYS_ABNORMAL,
                terminateOnAbnormalChildDeploy, true);
        if(!terminateOnAbnormalChildDeploy && !asynchCreateChild) {
            sfLog().warn("The attribute "+ATTR_TERMINATE_IF_CHILD_DEPLOYS_ABNORMAL+" is only valid with "+ATTR_ASYNCH_CREATE_CHILD);
        }
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
                if (!asynchCreateChild) {
                    sfLog().debug(" Parallel Synch");
                    synchCreateChildren();
                } else {
                    sfLog().debug(" Parallel Asynch");
                    asynchCreateChildren();
                }
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) {
                    sfLog().error(getName() + " - Failed to start sub-components ", ex);
                }
                terminationRecord = TerminationRecord
                        .abnormal(TERMINATE_FAILED_TO_START_SUB_COMPONENTS + ex, getName(), ex);
            }
        } else {
            //no actions. Maybe terminate 
            if (terminateIfEmpty) {
                terminationRecord = new TerminationRecord(TerminationRecord.NORMAL,
                        TERMINATE_PARALLEL_COMPONENT_IS_EMPTY, getName());
            }
        }
        if (terminationRecord != null) {
            //something went wrong; act on it
            maybeTerminate(terminationRecord, true);
        }

    }


    /**
     * A factoring out of all handling for startup failures
     * Log the event or queue for termination, depending upon the fail parameter
     * @param terminationRecord non null termination record
     * @param fail set to true to terminate; false to log
     * @return true if we started a terminator thread
     */
    private void maybeTerminate(TerminationRecord terminationRecord, boolean fail) {
        logChildDeployFailure(terminationRecord);
        if (fail) {
            new ComponentHelper(this).targetForWorkflowTermination(terminationRecord);
        }
    }

    private void logChildDeployFailure(TerminationRecord terminationRecord) {
        sfLog().err("Failed to start child", null, terminationRecord);
    }


    /**
     * Create the children of parallel, each in their own thread.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    protected void asynchCreateChildren() throws RemoteException, SmartFrogException {
        int size=actions.size();
        asynchChildren = new Vector(size);
        results = new Vector(size);
        actionKeys = actions.keys();
        setPendingDeployments(size);
        try {
            while (actionKeys.hasMoreElements()) {
                Object key = actionKeys.nextElement();
                ComponentDescription act = (ComponentDescription) actions.get(key);
                ParallelWorker thread = new ParallelWorker(this, key, act, null);
                asynchChildren.add(thread);
                if (sfLog().isDebugEnabled()) sfLog().debug("Creating " + key);
                decrementPendingDeployments();
                thread.start();
            }
        } catch (NoSuchElementException childless) {
            throw new SmartFrogRuntimeException(ERROR_NO_CHILDREN_TO_DEPLOY, this);
        } finally {
            setPendingDeployments(0);
        }
    }


    /**
     * If normal termination, Parallel behaviour is to terminate
     * that component but leave the others running if it is the last -
     * terminate normally. if an erroneous termination -
     * act on the value of {@link #terminateOnAbnormalChildTermination}
     *
     * There's a lot of complex logic in here, as it has to deal with both sync and async deployments,
     * 
     * @param record exit record of the component
     * @param child   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    protected boolean onChildTerminated(TerminationRecord record, Prim child) {
        boolean shouldTerminate = false;
        boolean normalRecord = record.isNormal();
        try {
            sfRemoveChild(child);
        } catch (SmartFrogRuntimeException e) {
            //this exception gets ignored by CompoundImpl's implementation, but there is
            //a risk something changes in the future
            sfLog().warn("Ignoring error on child termination ", e);
        } catch (RemoteException e) {
            //here's a network error; very unusual and merits propagating
            sfLog().error("Error handling child termination ", e);
            //failure to remove the child is always a problem
            if (normalRecord) {
                sfTerminate(TerminationRecord.abnormal(TERMINATION_ERROR_REMOVING_THE_CHILD, getName(), e));
                //bail out right now -this simplifies the logic slightly (currently).
                return false;
            } else {
                //its an error that should be forwarded. Again, bail out early
                return true;
            }
        }
        boolean lastChild = !hasActiveChildren();
        //we forward if this is the last child, or it is an abnormal and we want to forward it

        if (normalRecord) {
            shouldTerminate = lastChild;
        } else {
            //failure
            if (terminateOnAbnormalChildTermination) {
                shouldTerminate = true;
            } else {
                //we are here if this is a fault we may want to ignore
                //if this is the last child, we actually raise a new term record
                //after ignoring this one
                ignoringChildTermination(record, child);
                shouldTerminate = false;
                if (lastChild) {
                    //trigger a normal termination, even though this component terminated abnormally
                    sfTerminate(TerminationRecord.normal(TERMINATION_ABNORMAL_CHILD, getName()
                    ));
                }
            }
        }
        if (!normalRecord && !shouldTerminate) {
            //we want to to ignore this fault
            ignoringChildTermination(record, child);
        }
        return shouldTerminate;
    }

    /**
     * Cancels all remaining createChild threads. This will trigger callbacks to the
     *
     * @param status Termination  Record
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {

        // unregister from all remote registrations
        if (asynchChildren != null) {
            for (Enumeration e = asynchChildren.elements(); e.hasMoreElements();) {
                ParallelWorker worker = (ParallelWorker) e.nextElement();
                try {
                    worker.cancel(sfSyncTerminate, true);
                } catch (Exception ignored) {
                    sfLog().ignore("When canceling", ignored);
                }
            }
        }
        super.sfTerminateWith(status);
    }


    /**
     * This gets called when we ignore a child termination; the base class just logs it at the
     * err level. Override if you want more behaviour, such as saving the record somewhere
     * @param record the termination record
     * @param comp the component that has just terminated.
     */
    protected void ignoringChildTermination(TerminationRecord record, Prim comp) {
        sfLog().err("Ignoring child termination", null, record);
    }


    /**
     * Called in the worker thread, this method logs that the worker has finished by
     * removing it from our child list, which is then optionally used to trigger a failure.
     * @param worker
     */
    protected synchronized void workerFinished(ParallelWorker worker) {
        //and remove from our child list
        asynchChildren.removeElement(worker);
        if(sfLog().isDebugEnabled()) {
            sfLog().debug("Worker finished " + worker);
        }
        if (!isWorkflowTerminating()) {
            //this is only run if we arent actually shutting down, because at that point
            //it is needless
            try {
                results.add(worker.get(0));
                //if we get here, success.
            } catch (InterruptedException ignored) {
                sfLog().ignore(WORKER_FAILED + worker, ignored);
            } catch (SmartFrogException ex) {
                //failure: add the exception to the results
                results.add(ex);
                //this fault means that we failed to start up
                //create a term record
                TerminationRecord terminationRecord = TerminationRecord
                        .abnormal(TERMINATE_FAILURE_WHILE_STARTING_SUB_COMPONENTS + ex, getName(), ex);
                //and maybe terminate
                // This may be called more than once, but appears to be harmless in this case.

                maybeTerminate(terminationRecord, terminateOnAbnormalChildDeploy);
                if(!terminateOnAbnormalChildDeploy) {
                    //at this point we had an error, but we are not finishing. This may leave us with
                    //no children, which is itself a cause for termination. What we cannot do is rely on
                    //!sfChildren().hasMoreElements(); as a valid test, as there may be active threads
                    //that have not got there yet.
                    //we have to look at both
                    if(!hasActiveChildren()) {
                        //ok, ready to go
                        new ComponentHelper(this).targetForWorkflowTermination(
                                TerminationRecord.normal(getName()));
                    }
                }
            }
        }
    }

    /**
     * Test for active children.
     *  What we cannot do is rely on {@link #sfChildren()} not being empty, as there may be active threads
     * that have not got there yet...we have to look at the active thread count and return false if there
     * are threads there.
     * We also have to check for pending deployments, to prevent the race condition of SFOS-154 from arising
     * @return true if the system has active children, or children about to be deployed.
     */
    private synchronized boolean hasActiveChildren() {
        //If hasPendingDeployments() is appended here, then we check for startup problems;
        return sfChildren().hasMoreElements() || !asynchChildren.isEmpty() || hasPendingDeployments();

    }

    /**
     * Worker thread tied to this component, that notifies by calling {@link Parallel#workerFinished(ParallelWorker)}
     * after the thread is done.
     */
    private static class ParallelWorker extends CreateNewChildThread {

        /** owner class */
        private Parallel owner;


        /**
         * Creates a <tt>CreateNewChildThread</tt> that will upon running, execute the given <tt>CreateNewChild</tt>. If parent
         * null then it will create a independent application but a deployer needs to be provided
         *
         * @param owner   parent component
         * @param name     child name
         * @param cmp      component description
         * @param parms    Context
         * @throws SmartFrogException if callable is null
         */
        private ParallelWorker(Parallel owner, Object name, ComponentDescription cmp, Context parms) throws SmartFrogException {
            super(name, owner, cmp, parms, owner);
            this.owner = owner;
        }

        /**
         * Protected method invoked when this task transitions to state <tt>isDone</tt> .
         * This implementation queues the event with the parent
         */
        protected void done() {
            owner.workerFinished(this);
        }


        /**
         * get the owner log instead of the core
         *
         * @return Logger implementing LogSF and Log
         */
        protected LogSF sfLog() {
            return owner.sfLog();
        }

    }
}
