/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

    /**
     * Reference for attribute action.
     */
    static Reference actionRef = new Reference("action");
    /**
     * Reference for attribute time.
     */
    static Reference timeRef = new Reference("time");
    /**
     * ComponentDescription
     */
    ComponentDescription action;
    /**
     * Time taken.
     */
    int time;
    /**
     * Component Name
     */
    Reference name;
    /**
     * Timer thread.
     */
    Thread timer;

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
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        action = (ComponentDescription) sfResolve(actionRef);
        time = ((Integer) sfResolve(timeRef)).intValue();
        name = sfCompleteNameSafe();
    }

    /**
     * Deploys and manages the component and starts the timer.
     * Overrides CompoundImpl.sfStart.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogLifecycleException In case of any error while starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        timer = new Thread(new Runnable() {
                    public void run() {
                        if (time > 0) {
                            try {
                                Thread.sleep(time);
                            } catch (Exception e) {
                                //ignore
                                //log exception
                            }
                            sfTerminate(TerminationRecord.normal(name));
                        }
                    }
                });
        timer.start();
        sfCreateNewChild(name+"_actionRunning", action, null);
    }

    /**
     * Terminates the component. This is invoked by sub-components on
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
                    timer.stop();
                } catch (Exception e) {
                }
            }

            sfTerminate(status);
        }
    }
}
