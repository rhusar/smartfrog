package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;


/*
 * Based on "FutureTask" by Doug Lea with assistance from members of JCP JSR-166

 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain. Use, modify, and
 * redistribute this code in any way without acknowledgement.
 */
public class CreateNewChildThread extends Thread {

    /** State value representing that task is running */
    private static final int RUNNING = 1;
    /** State value representing that task ran */
    private static final int RAN = 2;
    /** State value representing that task was cancelled */
    private static final int CANCELLED = 4;

    /** The result to return from get() */
    private Object result = null;
    /** The exception to throw from get() */
    private Throwable exception;

    private int state;

    //Create New Child info
    /** name of attribute which the deployed component should adopt */
    private Object name = null;
    /** parent of deployed component */
    private Compound parent = null;

    /** component used to deploy component */
    private Compound deployer = null;

    /** cmp compiled component to deploy and start */
    private ComponentDescription cmp = null;
    /** parms parameters for description */
    private Context parms = null;

    /**
     * The thread running task. When nulled after set/cancel, this indicates that the results are accessible.  Must be
     * volatile, to ensure visibility upon completion.
     */
    private volatile Thread runner;

    /**
     * Creates a <tt>CreateNewChildThread</tt> that will upon running, execute the given <tt>CreateNewChild</tt>. Parent
     * cannot be null
     *
     * @param name   child name
     * @param parent parent component
     * @param cmp    component description
     * @param parms  Context
     * @throws SmartFrogException if callable is null
     */
    public CreateNewChildThread(Object name, Prim parent, ComponentDescription cmp, Context parms) throws SmartFrogException {
        this(name, parent, cmp, parms, null);
    }


    /**
     * Creates a <tt>CreateNewChildThread</tt> that will upon running, execute the given <tt>CreateNewChild</tt>. If parent
     * null then it will create a independent application but a deployer needs to be provided
     *
     * @param name     child name
     * @param parent   parent component
     * @param cmp      component description
     * @param parms    Context
     * @param deployer deployer
     * @throws SmartFrogException if callable is null
     */
    public CreateNewChildThread(Object name, Prim parent, ComponentDescription cmp, Context parms, Prim deployer) throws SmartFrogException {

        if (deployer == null) {
            if ((parent == null) || !(parent instanceof Compound)) {
                throw new SmartFrogException("Wrong parentDeployer");
            } else {
                deployer = parent;
            }
        } else {
            if (!(deployer instanceof Compound)) {
                throw new SmartFrogException("Wrong deployer");
            }
        }

        String deployerName = "parentDeployer";
        try {
            deployerName = deployer.sfCompleteName().toString();
        } catch (RemoteException ex) {
        }

        setName("CreateNewChildThread-" + deployerName + "." + name);
        this.name = name;
        this.parent = (Compound) parent;
        this.deployer = (Compound) deployer;
        this.cmp = cmp;
        this.parms = parms;
    }


    /**
     * Check if the state as CANCELLED
     *
     * @return boolean
     */
    public synchronized boolean isCancelled() {
        return state == CANCELLED;
    }

    /**
     * Checks if the tasks is done
     *
     * @return boolean
     */
    public synchronized boolean isDone() {
        return ranOrCancelled() && runner == null;
    }


    /**
     * Cancel the thread
     *
     * @param synchTermination      boolean to terminate component synchronously
     * @param mayInterruptIfRunning boolean to interrupt
     * @return boolean
     */
    public boolean cancel(boolean synchTermination, boolean mayInterruptIfRunning) {
        synchronized (this) {
            if (ranOrCancelled()) {
                return false;
            }
            state = CANCELLED;
            if (mayInterruptIfRunning) {
                Thread r = runner;
                if (r != null) r.interrupt();
            }
            runner = null;

            //Terminate child if it was deployed
            if (result != null) {
                if (synchTermination) {
                    if (sfLog().isTraceEnabled()) {
                        try {
                            sfLog().trace("SYNCTermination: " + ((Prim) result).sfCompleteName());
                        } catch (RemoteException e) {
                            sfLog().ignore(e);
                        }
                    }
                    try {
                        ((Prim) result).sfTerminate(TerminationRecord.abnormal("cancelled", null));
                    } catch (RemoteException ex) {
                        sfLog().ignore("ignoring during cancel (synch termination)", ex);
                    }

                } else {
                    if (sfLog().isTraceEnabled()) {
                        try {
                            sfLog().trace("ASYNCTermination: " + ((Prim) result).sfCompleteName());
                        } catch (RemoteException e) {
                            sfLog().ignore(e);
                        }
                    }
                    try {
                        (new TerminatorThread((Prim) result, TerminationRecord.abnormal("cancelled", null))).start();
                    } catch (Exception ex) {
                        // ignore
                        sfLog().ignore("ignoring during cancel (asynch termination)", ex);
                    }
                }

            }
            result = null;

            notifyAll();
        }

        done();
        return true;
    }

    /**
     * Wait
     *
     * @return Object
     * @throws InterruptedException if interrupted
     * @throws SmartFrogException   if failed
     */
    public synchronized Object get()
            throws InterruptedException, SmartFrogException {
        waitFor();
        return getResult();
    }

    /**
     * Wait
     *
     * @param timeout timeout
     * @return Object
     * @throws InterruptedException if interrupted
     * @throws SmartFrogException   if failed
     */
    public synchronized Object get(long timeout)
            throws InterruptedException, SmartFrogException {
        waitFor(timeout);
        return getResult();
    }

    /**
     * Protected method invoked when this task transitions to state <tt>isDone</tt> (whether normally or via cancellation).
     * The default implementation does nothing.  Subclasses may override this method to invoke completion callbacks or
     * perform bookkeeping. Note that you can query status inside the implementation of this method to determine whether
     * this task has been cancelled.
     */
    protected void done() {
    }

    /**
     * Sets the result of this Future to the given value unless this future has already been set or has been cancelled.
     *
     * @param v the value
     */
    protected void set(Object v) {
        setCompleted(v);
    }

    /**
     * Causes this future to report a <tt>SmartFrogException</tt> with the given throwable as its cause, unless this Future
     * has already been set or has been cancelled.
     *
     * @param t the cause of failure.
     */
    protected void setException(Throwable t) {
        setFailed(t);
    }

    /**
     * Get the exception that is cached away.
     *
     * @return the exception or null if none has been recorded yet
     */
    public synchronized Throwable getException() {
        return exception;
    }

    /**
     * Get the name of the child
     *
     * @return the desired name of the child
     */
    public Object getChildName() {
        return name;
    }

    /** Sets this Future to the result of computation unless it has been cancelled. */
    public void run() {
        synchronized (this) {
            if (state != 0) return;
            state = RUNNING;
            runner = Thread.currentThread();
        }
        try {
            if (sfLog().isDebugEnabled()) {
                String parentName = "no-parent";
                String deployerName = "no-deployer";
                if (parent != null) parentName = parent.sfCompleteName().toString();
                if (deployer != null) deployerName = deployer.sfCompleteName().toString();
                sfLog().debug("Creating child '" + name + "' with parent '" + parentName + "' using deployer '" + deployerName + "'");
            }
            setCompleted(deployer.sfCreateNewChild(name, parent, cmp, parms));
            if (sfLog().isDebugEnabled()) {
                String compName = name.toString();
                try {
                    compName = ((Prim) result).sfCompleteName().toString();
                } catch (Exception ex) {
                    sfLog().ignore(ex);
                }
                sfLog().debug("Child '" + compName + "' created");
            }

        }
        catch (Throwable ex) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Failed to create child '" + name + "'", ex);
            }
            setFailed(ex);
        }
    }


    // PRE: lock owned
    private boolean ranOrCancelled() {
        return (state & (RAN | CANCELLED)) != 0;
    }

    /**
     * Marks the task as completed.
     *
     * @param result the result of a task.
     */
    private void setCompleted(Object result) {
        synchronized (this) {
            if (ranOrCancelled()) return;
            this.state = RAN;
            this.result = result;
            this.runner = null;
            notifyAll();
        }

        // invoking callbacks *after* setting future as completed and
        // outside the synchronization block makes it safe to call
        // interrupt() from within callback code (in which case it will be
        // ignored rather than cause deadlock / illegal state exception)
        done();
    }

    /**
     * Marks the task as failed.
     *
     * @param exception the cause of abrupt completion.
     */
    private void setFailed(Throwable exception) {
        synchronized (this) {
            //dont log the exception if it already happened
            if (ranOrCancelled()) {
                return;
            }
            //move to the ran state
            this.state = RAN;
            //log the listener
            this.exception = exception;
            this.runner = null;
            //notify everything
            notifyAll();
        }

        // invoking callbacks *after* setting future as completed and
        // outside the synchronization block makes it safe to call
        // interrupt() from within callback code (in which case it will be
        // ignored rather than cause deadlock / illegal state exception)
        done();
    }

    /**
     * Waits for the task to complete. PRE: lock owned
     *
     * @throws InterruptedException if interrupted
     */
    private void waitFor() throws InterruptedException {
        while (!isDone()) {
            wait();
        }
    }

    /**
     * Waits for the task to complete for timeout milliseconds or throw TimeoutException if still not completed after that
     * PRE: lock owned
     *
     * @param timeout timeout
     * @throws InterruptedException if interrupted
     * @throws SmartFrogException   if timeout is not valid
     */
    private void waitFor(long timeout) throws InterruptedException, SmartFrogException {
        if (timeout < 0) throw new SmartFrogException("IllegalArgumentException");
        if (isDone()) return;
        long deadline = System.currentTimeMillis() + timeout;
        while (timeout > 0) {
            this.wait(timeout);
            if (isDone()) return;
            timeout = deadline - System.currentTimeMillis();
        }
        throw new SmartFrogException("Timeout");
    }

    /**
     * Gets the result of the task.
     *
     * PRE: task completed PRE: lock owned
     *
     * @return Object
     * @throws SmartFrogException if failed
     */
    private Object getResult() throws SmartFrogException {
        if (state == CANCELLED) {
            throw new SmartFrogException("CancellationException");
        }
        if (exception != null) {
            throw SmartFrogException.forward("ExecutionException", exception);
        }
        return result;
    }

    /**
     * To get the sfCore logger
     *
     * @return Logger implementing LogSF and Log
     */
    protected LogSF sfLog() {
        return LogFactory.sfGetProcessLog();
    }

    /**
     * Returns a string representation of this thread, including the thread's name, priority, and thread group.
     *
     * @return a string representation of this thread.
     */
        public String toString() {
            return "Worker thread deploying "+getName();
        }
}
