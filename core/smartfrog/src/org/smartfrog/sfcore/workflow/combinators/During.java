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
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;


/**
 * During is a modified compound which differs in that the single sub-component
 * is given a certain amount of time to terminate and if it has not, the
 * timeout combinator terminates it and itself normally. The sub-component
 * may, of course, be a further combinator such as a Sequence. The during
 * combinator creates its sub-componentent during the sfStart phase The During
 * combinator waits for its sub-components to terminate normally at which
 * point it too terminates normally. If an error occurs at any point, or a
 * sub-component terminates abnormall the During combinator terminates
 * abnormally.
 *
 * <p>
 * The file during.sf contains the SmartFrog configuration file for the base
 * During combinator. This file conatins the details of the attributes which
 * may be passed to During.
 * </p>
 */
public class During extends EventCompoundImpl implements Compound {

    public static final String ATTR_TIME = "time";
    /**
     * Reference for attribute time.
     */
    static final Reference timeRef = new Reference(ATTR_TIME);
    /**
     * Time taken.
     */
    private int time;

    /**
     * Terminator thread
     */
    private DelayedTerminator terminator;

    /**
     * Constructs During.
     *
     * @throws java.rmi.RemoteException The required exception in case of
     *            comms failure
     */
    public During() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Reads the basic configuration of the component and deploys it.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        checkActionDefined();
        time = ((Integer) sfResolve(timeRef)).intValue();
    }

    /**
     * Deploys and manages the component and starts the timer.
     * Overrides CompoundImpl.sfStart.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        terminator=new DelayedTerminator(this,time,sfLog(),null,true);
        terminator.start();
        sfCreateNewChild(name+"_duringActionRunning", action, null);
    }


    /**
     * Deregisters from all current registrations.
     *
     * @param status Termination  Record
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        killTimer();
    }

    /**
     * Kill the timer if it is running. does nothing otherwise
     */
    private synchronized void killTimer() {
        if (terminator != null) {
            terminator.shutdown(false);
        }
    }


    /**
     * This is an override point; it is where subclasses get to change their workflow
     * depending on what happens underneath.
     * It is only called outside of component termination, i.e. when {@link #isWorkflowTerminating()} is
     * false, and when the comp parameter is a child, that is <code>sfContainsChild(comp)</code> holds.
     * If the the method returns true, the event is forwarded up the object heirarchy, which
     * will eventually trigger a component termination.
     * <p/>
     * Always return false if you start new components from this method!
     * </p>
     *
     * @param status exit record of the component
     * @param comp   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        killTimer();
        return true;
    }

/* this is the old runnable factored out to show what the logic was.
   as of 3.10.0 it was broken to the extent that if time<= 0, the terminator
   would not terminate the component.

    private class DuringRunnable implements Runnable {

        public void run() {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("Timer set:" + time + ". Going to sleep " + name);
            }
            if (time > 0) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    if (abortTimer) {
                        return;
                    }
                }
                String terminationMessage = "Timer '" + time + "' expired. Terminating " + name;
                if (sfLog().isDebugEnabled()) {
                    sfLog().debug(terminationMessage);
                }
                sfTerminate(new TerminationRecord(TerminationRecord.NORMAL, terminationMessage, null));
            }
        }
    }
    */
}
