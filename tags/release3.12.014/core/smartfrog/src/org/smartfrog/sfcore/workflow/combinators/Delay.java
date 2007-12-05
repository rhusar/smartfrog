/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP
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

package org.smartfrog.sfcore.workflow.combinators;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.utils.SmartFrogThread;

/**
 * Delay is a modified compound which differs in that the single sub-component
 * is given a certain amount of time to terminate and if it has not, the
 * timeout combinator terminates it and itself abnormally. The sub-component
 * may, of course, be a further combinator such as a Sequence. The timeout
 * combinator creates its sub-componentent during the sfStart phase The Delay
 * combinator waits for its sub-components to terminate normally at which
 * point it too terminates normally. If an error occurs at any point, or a
 * sub-component terminates abnormally, or the timeout fires before its
 * sub-component terminates, the Delay combinator terminates abnormally.
 *
 * <p>
 * The file timeout.sf contains the SmartFrog configuration file for the base
 * Delay combinator. This file conatins the details of the attributes which
 * may be passed to Delay.
 * </p>
 */
public class Delay extends EventCompoundImpl implements Compound, Runnable {
    protected static final String ATTR_TIME = "time";
    /**
     * Reference for attribute time
     */
    static final Reference timeRef = new Reference(ATTR_TIME);

    /**
     * Time taken.
     */
    private int time;

    /**
     * Timer thread.
     */
    private volatile SmartFrogThread timer;

    /**
     * Indication that the component has been terminated before the time fires
     */
    private volatile boolean terminated = false;

    /**
     * Constructs Delay object.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Delay() throws RemoteException {
        super();
    }

    /**
     * Deploys and reads the basic configuration of the component.
     * Overrides EventCompoundImpl.sfDeploy.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException,
        RemoteException {
        super.sfDeploy();
        //checkActionDefined();
        time = ((Integer)sfResolve(timeRef)).intValue();
    }

    /**
     * Deploys and manages the sub-component and starts the timer.
     * Overrides EventCompoundImpl.sfStart.
     *
     * @throws SmartFrogException In case of SmartFrog system error
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
        RemoteException {
        super.sfStart();
        timer = new SmartFrogThread(this);
        timer.start();
    }


    /**
     * If normal termination, Parallel behaviour is to terminate
     * that component but leave the others running if it is the last -
     * terminate normally. if an erroneous termination -
     * terminate immediately passing on the error
     *
     *
     * @param status exit record of the component
     * @param comp   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        killTimer();
        return true;
    }

    /**
     * kill the timer.
     */
    private void killTimer() {
        //copy
        Thread t;
        //copy so that even if the timer nulls itself during termination, the cached reference is
        //still held.
        t = timer;
        if (t!=null) {
            terminated = true;
            t.interrupt();
        }
    }
    

    /**
     * This routine runs in the background thread; it sleeps until its time is up
     */
    public void run() {
        try {
            if (time > 0) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException ignored) {
                    // ignore
                }
            }
            synchronized (this) {
                if (!terminated && !isWorkflowTerminating()) {
                    if(action!=null) {
                        try {
                            sfCreateNewChild("running", action, null);
                        } catch (Exception e) {
                            sfTerminate(TerminationRecord.abnormal(
                                    "error in launching delayed component", name, e));
                        }
                    } else {
                        //no work to do; terminate
                        sfTerminate(TerminationRecord.normal(name));
                    }
                }
            }
        } finally {
            //now, cease to exit.
            timer = null;
        }
    }
}
