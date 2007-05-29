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
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.utils.SmartFrogThread;

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
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  in case of problems creating the child
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        interval=sfResolve(ATTR_INTERVAL,interval,true);
        timeout = sfResolve(ATTR_TIMEOUT, timeout, true);
        thread = new SmartFrogThread(this);
        thread.start();
    }


    /**
     * Handle notifications of termination
     *
     * @param status termination status of sender
     * @param comp   sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        super.sfTerminatedWith(status, comp);
        //stop the waiting
        synchronized(this) {
            if(thread!=null) {
                thread.interrupt();
            }
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {

        try {
            //pick on the current start time.
            end = System.currentTimeMillis() + timeout;

            Throwable fault=null;
            try {
                boolean timedout = false;
                boolean test;
                test = evaluate();
                while(!test && !timedout) {
                    Thread.sleep(interval);
                    test = evaluate();
                    long now = System.currentTimeMillis();
                    timedout = now > end;
                }
                //we have either timed out or the test has passed.
                //chose the branch to test
                String branch=test?ATTR_THEN:ATTR_ELSE;
                Prim prim = deployChildCD(branch, false);
                //then finish if we did not deploy anything
                if(prim==null) {
                    finish();
                }
            } catch (RemoteException e) {
                fault=e;
            } catch (SmartFrogException e) {
                fault = e;
            } catch (InterruptedException e) {
                //we have been interrupted, which implies terminated.
                //Do nothing
            }
            if(fault!=null) {
                //trouble -fail
                sfTerminate(TerminationRecord.abnormal("Trouble during WaitFor",name,fault));
            }
        } finally {
            synchronized (this) {
                thread=null;
            }
        }

    }
}
