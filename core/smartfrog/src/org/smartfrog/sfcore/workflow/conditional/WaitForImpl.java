/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.workflow.conditional;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.rmi.RemoteException;

/**
 * created 30-Nov-2006 13:35:19
 */

public class WaitForImpl extends ConditionCompound implements WaitFor, Runnable {

    private long interval;
    private long timeout;
    private long end;
    private Thread thread;


    public WaitForImpl() throws RemoteException {
    }


    /**
     * Starts the component by deploying the condition
     *
     * @throws SmartFrogException in case of problems creating the child
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        readValues();
        thread = new SmartFrogThread(this);
        thread.start();
    }

    /**
     * read the values of this component. Subclass
     * (calling super.readValues() to read in more data during the condition)
     *
     * @throws SmartFrogException problems reading in data
     * @throws RemoteException    network problems
     */
    protected void readValues() throws SmartFrogException, RemoteException {
        interval = sfResolve(ATTR_INTERVAL, interval, true);
        timeout = sfResolve(ATTR_TIMEOUT, timeout, true);
        if (sfLog().isDebugEnabled()) {
            sfLog().debug("Waiting for " + timeout
                    + " milliseconds, with a check every " + interval + " milliseconds");
        }
    }


    /**
     * shut down thread during termination
     *
     * @param status termination status of sender
     * @param comp   sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        super.sfTerminatedWith(status, comp);
        //stop the waiting
        synchronized (this) {
            if (thread != null) {
                thread.interrupt();
            }
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread. <p/> The general
     * contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {

        try {
            //pick on the current start time.
            long start = System.currentTimeMillis();
            end = start + timeout;

            Throwable fault = null;
            try {
                boolean test;
                long now = start;
                test = evaluate();
                while (!test && now < end) {
                    Thread.sleep(interval);
                    test = evaluate();
                    now = System.currentTimeMillis();
                }
                long wait = now - start;
                if (sfLog().isDebugEnabled()) {
                    sfLog().debug("WaitFor "
                            + (test ? "succeeded" : "timed out")
                            + " after " + wait + " milliseconds");
                }
                //handle the completion
                boolean toFinish = onWaitForComplete(test);
                //then finish if we did not deploy anything
                if (toFinish) {
                    finish();
                }

            } catch (RemoteException e) {
                fault = e;
            } catch (SmartFrogException e) {
                fault = e;
            } catch (InterruptedException e) {
                //we have been interrupted, which implies terminated.
                //Do nothing
            }
            if (fault != null) {
                //trouble -fail
                sfTerminate(TerminationRecord.abnormal("Trouble during WaitFor", getName(), fault));
            }
        } finally {
            synchronized (this) {
                thread = null;
            }
        }

    }

    /**
     * This is an override point; handling of post-condition operations.
     * The base class chooses a branch to spawn on success/failure
     *
     * @param success whether or not the waitfor was a success
     * @return true if the workflow should now schedule itself for completion.
     *         Any exception thrown will trigger abnormal component termination
     * @throws SmartFrogException smartfrog problems
     * @throws RemoteException    network problems.
     */
    protected boolean onWaitForComplete(boolean success)
            throws SmartFrogException, RemoteException {
        //we have either timed out or the test has passed.
        //chose the branch to test
        String branch = success ? ATTR_THEN : ATTR_ELSE;
        Prim prim = deployChildCD(branch, false);
        return prim == null;
    }
}
