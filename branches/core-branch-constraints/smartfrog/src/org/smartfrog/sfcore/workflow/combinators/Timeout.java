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
 * Timeout is a modified compound which differs in that the single
 * sub-component is given a certain amount of time to terminate and if it has
 * not, the timeout combinator terminates it and itself abnormally. The
 * sub-component may, of course, be a further combinator such as a Sequence.
 * The timeout combinator creates its sub-componentent during the sfStart
 * phase The Timeout combinator waits for its sub-components to terminate
 * normally at which point it too terminates normally. If an error occurs at
 * any point, or a sub-component terminates abnormally, or the timeout fires
 * before its sub-component terminates, the Timeout combinator terminates
 * abnormally.
 *
 * <p>
 * The file timeout.sf contains the SmartFrog configuration file for the base
 * Timeout combinator. This file conatins the details of the attributes which
 * may be passed to Timeout.
 * </p>
 */
public class Timeout extends EventCompoundImpl implements Compound {
    private static Reference timeRef = new Reference("time");
    private int time;
    private Thread timer;
    private boolean terminated = false;

    /**
     * Constructs Timeout.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Timeout() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Deploys the component and reads configuration attributes.
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
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while  starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException,
        RemoteException {
        super.sfStart();

        // let any errors be thrown and caught by SmartFrog for abnormal
        // termination  - including empty actions
        timer = new Thread(new Runnable() {
            public void run() {
                if (time>0) {
                    try {
                        Thread.sleep(time);
                    } catch (Exception e) {
                    }

                    if (!terminated)
                        sfTerminate(TerminationRecord.abnormal("timeout occurred", name));
                }
            }
        });
        timer.start();
        sfCreateNewChild(name+"_timeoutActionRunning", action, null);
    }

    /**
     * Terminates the component. It is invoked by sub-components on
     * termination. If normal termiantion, Timeout behaviour is to terminate
     * normally, otherwise abnormally.
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        if (sfContainsChild(comp)) {
            if (timer != null) {
                try {
                    terminated = true;
                    timer.interrupt();
                } catch (Exception e) {
                }
            }
        }
        //relay up to the super class
        super.sfTerminatedWith(status, comp);
    }
}
