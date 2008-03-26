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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * The Workflow thread will terminate its owner when finishing.
 *
 */

public class WorkflowThread extends SmartFrogThread {
    private Prim owner;
    private Reference ownerID;
    private boolean workflowTermination;

    /**
     * termination message: {@value}
     */
    public static final String WORKER_THREAD_COMPLETED = "Worker thread completed";

    /**
     * Allocates a new <code>SmartFrogThread</code> object.
     *
     * @param owner owner obj
     * @param notifyObject to notify afterwards. If null, "this" is used
     * @param workflowTermination should workflow (optional) termination rules be used? If false, this thread
     * always terminates the parent when finished
     */
    public WorkflowThread(Prim owner, boolean workflowTermination,Object notifyObject) {
        super(notifyObject);
        this.owner = owner;
        this.workflowTermination=workflowTermination;
        try {
            ownerID = owner.sfCompleteName();
        } catch (RemoteException e) {
            ownerID = null;
        }
    }

    /**
     * Create a basic thread. Notification is bound to a local notification object.
     *
     * @param owner owner thread
     * @param workflowTermination is workflow termination expected
     */
    public WorkflowThread(Prim owner, boolean workflowTermination) {
        this(owner,workflowTermination, new Object());
    }

    /**
     * Runs the {@link #execute()} method, catching any exception it throws and storing it away for safe keeping After
     * the run, the notify object is notified, and we trigger a workflow termination
     */
    public void run() {
        super.run();
        TerminationRecord tr = new TerminationRecord(
                getThrown() == null ? TerminationRecord.NORMAL : TerminationRecord.ABNORMAL,
                getTerminationMessage(),
                ownerID,
                getThrown());
        ComponentHelper helper = new ComponentHelper(owner);
        if (workflowTermination) {
            //put up for workflow termination
            helper.targetForWorkflowTermination(tr);
        } else {
            //put up for termination
            helper.targetForTermination(tr, false, false, false);
        }
    }

    /**
     * Override point: the termination message
     * @return {@link #WORKER_THREAD_COMPLETED}
     */
    protected String getTerminationMessage() {
        return WORKER_THREAD_COMPLETED;
    }
}
