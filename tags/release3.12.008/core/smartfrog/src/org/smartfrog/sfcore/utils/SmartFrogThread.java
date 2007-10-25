/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.rmi.RemoteException;

/**
 * This thread represents the base class for threads run under SmartFrog.
 * It contains extra methods for behaviour we want across all our threads.
 * created 13-Feb-2007 10:39:41
 * */

public class SmartFrogThread extends Thread {


    private Throwable thrown;
    private Runnable runnable;


    /**
     * Create a basic thread
     * @see Thread#Thread(ThreadGroup,Runnable,String)
     */
    public SmartFrogThread() {
    }

    /**
     * Create a basic thread bound to a runnable
     *
     * @param target the object whose <code>run</code> method is called.
     * @see Thread#Thread(ThreadGroup,Runnable,String)
     */
    public SmartFrogThread(Runnable target) {
        init(target);
    }

    /**
     * Internal initialization
     * @param target what we want to run
     */
    private void init(Runnable target) {
        runnable = target;
    }

    /**
     * Create a thread
     * @param group  the thread group.
     * @param target the object whose <code>run</code> method is called.
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group.
     * @see Thread#Thread(ThreadGroup,Runnable,String)
     */
    public SmartFrogThread(ThreadGroup group, Runnable target) {
        super(group,(Runnable) null);
        init(target);
    }

    /**
     * Create a thread
     * @param name the name of the new thread.
     * @see Thread#Thread(ThreadGroup,Runnable,String)
     */
    public SmartFrogThread(String name) {
        super(name);
    }

    /**
     * Create a thread
     *
     * @param group the thread group.
     * @param name  the name of the new thread.
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group.
     * @see Thread#Thread(ThreadGroup,Runnable,String)
     */
    public SmartFrogThread(ThreadGroup group, String name) {
        super(group, name);
    }

    /**
     * Create a thread
     *
     * @param target the object whose <code>run</code> method is called.
     * @param name   the name of the new thread.
     * @see Thread#Thread(ThreadGroup,Runnable,String)
     */
    public SmartFrogThread(Runnable target, String name) {
        super((Runnable) null, name);
        init(target);
    }

    /**
     * Create a thread
     *
     * @param group  the thread group.
     * @param target the object whose <code>run</code> method is called.
     * @param name   the name of the new thread.
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group or cannot
     *                           override the context class loader methods.
     * @see Runnable#run()
     * @see Thread#run()
     * @see Thread#setDaemon(boolean)
     * @see Thread#setPriority(int)
     * @see ThreadGroup#checkAccess()
     * @see SecurityManager#checkAccess
     */
    public SmartFrogThread(ThreadGroup group, Runnable target, String name) {
        super(group, null, name);
        init(target);
    }

    /**
     * Create a thread
     *
     * @param group     the thread group.
     * @param target    the object whose <code>run</code> method is called.
     * @param name      the name of the new thread.
     * @param stackSize the desired stack size for the new thread, or zero to indicate that this parameter is to be
     *                  ignored.
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group.
     */
    public SmartFrogThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, null, name, stackSize);
        init(target);
    }

    /**
     * Return the exception thrown by this thread.
     * @return any exception or null
     */
    public synchronized Throwable getThrown() {
        return thrown;
    }

    /**
     * Record the exception thrown by this thread
     * @param thrown the exception to record as thrown
     */
    public synchronized void setThrown(Throwable thrown) {
        this.thrown = thrown;
    }

    /**
     * Rethrow any exception as a SmartFrogException
     * @throws SmartFrogException if the thread threw an exception that we caught.
     */
    public synchronized void rethrow() throws SmartFrogException {
        if(thrown!=null) {
            throw convertThrown();
        }
    }

    /**
     * Test for having an exception at the end of the run.
     * @return true if there is an exception.
     */
    public boolean isThrown() {
        return getThrown()!=null;
    }

    /**
     * Return true if an InterruptedException was thrown and caught
     * at the end of execution.
     * @return true if there was an exception caught and it was an {@link InterruptedException}
     */
    public boolean wasInterruptedThrown() {
        Throwable t=getThrown();
        return t!=null && t instanceof InterruptedException;
    }

    /**
     * Override point: convert the exception in thrown to
     * a SmartFrogException.
     * The base class delegates to {@link SmartFrogException#forward(Throwable)}
     * @return the exception to throw
     */
    protected SmartFrogException convertThrown() {
        return SmartFrogException.forward(thrown);
    }

    /**
     * Handle pings by rethrowing any caught exception. There is special handling for
     * {@link InterruptedException}, as it is often raised when stopping a thread. It may not be
     * an error for that to have happened.
     * @param rethrowInterruptedExceptions should an InterruptedException be wrapped as a liveness failure.
     * @throws SmartFrogLivenessException containing any wrapped exception
     * @throws RemoteException if a remote exception was caught
     */
    public void ping(boolean rethrowInterruptedExceptions) throws SmartFrogLivenessException, RemoteException {
        Throwable t = getThrown();
        if (t == null) {
            //no fault
            return;
        }
        if (t instanceof RemoteException) {
            throw (RemoteException) t;
        }
        if (t instanceof InterruptedException && !rethrowInterruptedExceptions) {
            //ignore the interruption. Sometimes this is useful
            return;
        }
        throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(t);
    }

    /**
     * Runs the {@link #execute()} method, catching any exception it throws and
     * storing it away for safe keeping
     */
    public void run() {
        try {
            execute();
        } catch (Throwable throwable) {
            setThrown(throwable);
        }
    }

    /**
     * If this thread was constructed using a separate {@link Runnable} run object, then that <code>Runnable</code>
     * object's <code>run</code> method is called; otherwise, this method does nothing and returns. <p> Subclasses of
     * <code>Thread</code> should override this method.
     * @throws Throwable if anything went wrong
     */
    public void execute() throws Throwable {
        if (runnable != null) {
            runnable.run();
        }
    }
}
