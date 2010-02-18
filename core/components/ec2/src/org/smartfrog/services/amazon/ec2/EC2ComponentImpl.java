/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.amazon.ec2;

import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.TerminatingInstanceDescription;
import org.smartfrog.services.amazon.workflow.CompletableWork;
import org.smartfrog.services.passwords.PasswordHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogExtractedException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 25-Mar-2008 13:36:29
 */

public class EC2ComponentImpl extends PrimImpl implements EC2Component, CompletableWork {

    private Jec2 ec2binding;
    private String id;
    private String key;
    protected static final ArrayList<String> EMPTY_ARGUMENTS = new ArrayList<String>();
    private SmartFrogThread worker;
    private Throwable workException;
    private volatile boolean workCompleted;

    public EC2ComponentImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        id = sfResolve(ATTR_ID, "", true);
        key = PasswordHelper.resolvePassword(this, ATTR_KEY, true);
        ec2binding = bindToEC2();
    }

    /**
     * Liveness call in to check if this component is still alive.
     * Any work exception gets forwarded up here.
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness}
     * interface
     */
    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        Throwable ex = workException;
        if (ex != null) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(ex);
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        terminateWorker();
    }

    /**
     * Terminate any worker thread; synchronized. After the request is made the worker field is always null.
     */
    protected void terminateWorker() {
        SmartFrogThread thread;
        synchronized (this) {
            thread = worker;
            worker = null;
        }
        SmartFrogThread.requestThreadTermination(thread);
    }

    /**
     * record that the work has completed. If an exception
     * is passed in, assume the work failed
     * @param exception an optional exception.
     */
    protected synchronized void workCompleted(Throwable exception) {
        workCompleted = true;
        workException = SmartFrogExtractedException.convert(exception);
    }

    /**
     * record that the work has completed.
     */
    protected void workCompleted() {
        workCompleted(null);
    }

    /**
     * Poll point for work being completed
     *
     * @return true if the work is completed
     */
    public boolean isWorkCompleted() {
        return workCompleted;
    }

    /**
     * Poll for the work being successful.
     * This is defined as the exception being null
     *
     * @return true if nothing went wrong and we have completed
     */
    public synchronized boolean isWorkSuccessful() {
        return workCompleted && workException==null;
    }

    /**
     * Return any exception raised when the work failed.
     * If this is non-null, then isWorkSuccessful() must be false
     *
     * @return an exception or null
     */
    public Throwable getWorkException() {
        return workException;
    }

    /**
     * Get the current Jec2 binding
     *
     * @return the current binding
     */
    public Jec2 getEc2binding() {
        return ec2binding;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    /**
     * Create a new EC2 binding
     *
     * @return a new binding with the current key/password
     */
    protected Jec2 bindToEC2() {
        Jec2 ec2 = new Jec2(id, key);
        ec2.useSystemProxy();
        return ec2;
    }


    public SmartFrogThread getWorker() {
        return worker;
    }

    public void setWorker(SmartFrogThread worker) {
        this.worker = worker;
    }

    /**
     * Bind to and start a worker thread
     *
     * @param workflowThread worker
     */
    protected void deployWorker(SmartFrogThread workflowThread) {
        setWorker(workflowThread);
        getWorker().start();
    }

    /**
     * Log any termination records
     * @param terminating list of terminating entries
     */
    protected void logTerminationInfo(List<TerminatingInstanceDescription> terminating) {
        for (TerminatingInstanceDescription description : terminating) {
            sfLog().info(description);
        }
    }

    /**
     * Print all instances out to the log
     *
     * @param instanceList instances
     */
    protected void logInstances(InstanceList instanceList) {
        sfLog().info(instanceList.toString());
    }
}
