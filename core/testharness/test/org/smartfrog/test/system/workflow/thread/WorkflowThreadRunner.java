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
package org.smartfrog.test.system.workflow.thread;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.ShouldDetachOrTerminate;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 *
 * Created 03-Apr-2008 16:02:15
 *
 */

public class WorkflowThreadRunner extends PrimImpl implements Prim {

    private WorkflowThread thread;


    public WorkflowThreadRunner() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        int delay=sfResolve("time",0,true);
        boolean workflow= sfResolve("workflow", true, true);
        thread=new SleepThread(workflow, delay);
        thread.start();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread.requestThreadTermination(thread);
    }

    public class SleepThread extends WorkflowThread {
        int delay;

        /**
         * Create a basic thread. Notification is bound to a local notification object.
         *
         * @param workflowTermination is workflow termination expected
         * @param delay how long to sleep.
         */
        public SleepThread( boolean workflowTermination, int delay) {
            super(WorkflowThreadRunner.this, workflowTermination);
            this.delay=delay;
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run object, then that <code>Runnable</code>
         * object's <code>run</code> method is called; otherwise, this method does nothing and returns. <p> Subclasses of
         * <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            boolean shouldTerminate=sfResolve(ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE,false,true);
            sfLog().info("about to sleep for "+delay+" milliseconds");
            sfLog().info("sfShouldTerminate="+shouldTerminate);
            Thread.sleep(delay);
            sfLog().info("awake");
        }
    }

}
