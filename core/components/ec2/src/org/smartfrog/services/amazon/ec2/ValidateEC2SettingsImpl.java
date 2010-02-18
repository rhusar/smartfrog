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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;

/**
 * This component validates EC2 settings and the workflow
 * process itself. It stays off line
 * <p/>
 * Created: 09-Apr-2008
 */
public class ValidateEC2SettingsImpl extends EC2ComponentImpl {

    private int delay=0;
    private boolean simulateFailure;
    public static final String ATTR_SIMULATE_FAILURE = "simulateFailure";
    public static final String ATTR_DELAY = "delay";
    public static final String SIMULATED_EXCEPTION = "Simulated EC2 exception";
    public ValidateEC2SettingsImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        delay=sfResolve(ATTR_DELAY,0,true);
        simulateFailure = sfResolve(ATTR_SIMULATE_FAILURE, true, true);
        sfLog().info("Starting worker thread");
        deployWorker(new WorkerThread());
    }

    /**
     * Thread to create the instance
     */
    private class WorkerThread extends WorkflowThread {



        /**
         * Create a basic thread. Notification is bound to a local notification
         * object.
         */
        private WorkerThread() {
            super(ValidateEC2SettingsImpl.this, true);
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run
         * object, then that <code>Runnable</code> object's <code>run</code>
         * method is called; otherwise, this method does nothing and returns.
         * <p> Subclasses of <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        @Override
        public void execute() throws Throwable {
            sfLog().info("worker thread sleeping for "+delay+" ms");
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                sfLog().info("worker thread interrupted");
                throw e;
            }
            sfLog().info("worker finishing");
            if(simulateFailure) {
                sfLog().info("worker thread simulating failure");
                throw new SmartFrogEC2Exception(SIMULATED_EXCEPTION);
            }
        }

        /**
         * decide whether we passed or failed based on the termination record
         *
         * @param tr the termination record about to be passed up
         */
        @Override
        protected void aboutToTerminate(TerminationRecord tr) {
            sfLog().info("Work is completed: "+tr);
            workCompleted(tr.getCause());
        }
    }
}
