/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * Created 24-Feb-2009 14:49:27 - originally in the Hadoop package, but moved to the core for re-use
 */

public class WorkerThreadPrimImpl extends PrimImpl {

    private WorkflowThread worker;


    protected WorkerThreadPrimImpl() throws RemoteException {
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        //shut down any non-null worker
        terminateWorker();
        super.sfTerminateWith(status);
    }

    /**
     * Shut down any worker if running. This will set the worker field to null.
     */
    protected synchronized void terminateWorker() {
        WorkflowThread w;
        synchronized (this) {
            w = worker;
            worker = null;
        }
        if (w != null) {
            terminateWorkerThread(w);
        }
    }

    /**
     * At this point the worker attribute has been set to null, now the thread termination is requested. The base
     * implementation uses {@link SmartFrogThread#requestThreadTermination(SmartFrogThread)}; subclasses may choose an
     * alternate strategy
     *
     * @param workflowThread a non-null thread
     */
    protected void terminateWorkerThread(WorkflowThread workflowThread) {
        SmartFrogThread.requestThreadTermination(workflowThread);
    }

    /**
     * Get any worker
     *
     * @return the worker, may be null
     */
    protected final synchronized WorkflowThread getWorker() {
        return worker;
    }

    protected final synchronized void setWorker(WorkflowThread worker) {
        this.worker = worker;
    }
}
