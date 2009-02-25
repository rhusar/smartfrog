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
package org.smartfrog.services.hadoop.components.dfs;

import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * Created 24-Feb-2009 14:49:27
 */

public class WorkerThreadPrimImpl extends PrimImpl{

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
        super.sfTerminateWith(status);
        //shut down any non-null worker
        terminateWorker();
    }

    /**
     * Shut down any worker if running
     */
    protected synchronized void terminateWorker() {
        WorkflowThread w = worker;
        worker = null;
        SmartFrogThread.requestThreadTermination(w);
    }

    protected final synchronized WorkflowThread getWorker() {
        return worker;
    }

    protected final synchronized void setWorker(WorkflowThread worker) {
        this.worker = worker;
    }
}
