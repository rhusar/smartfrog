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
public class Delay extends EventCompoundImpl implements Compound {
    /**
     * Reference for attribute action
     */
    static Reference actionRef = new Reference("action");
    /**
     * Reference for attribute time
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
     * Name of the component.
     */
    Reference name;

    /**
     * Timer thread.
     */
    Thread timer;

    /**
     * Indication that the component has been terminated before the time fires
     */
    private boolean terminated = false;

    /**
     * Constructs Delay object.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Delay() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Deploys and reads the basic configuration of the component.
     * Overrides EventCompoundImpl.sfDeploy.
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
     * Deploys and manages the sub-component and starts the timer.
     * Overrides EventCompoundImpl.sfStart.
     *
     * @throws SmartFrogException In case of SmartFrog system error
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        timer = new Thread(new Runnable() {
                public void run() {
                    if (time > 0) {
                        try {
                            Thread.sleep(time);
                        } catch (Exception e) {
                            // ignore
                        }
			if (!terminated) {
			    try {
				synchronized (this) {
				    (Delay.this).sfCreateNewChild(name+"_actionRunning", action, null);
				}
			    } catch (Exception e) {
				(Delay.this).sfTerminate(TerminationRecord.abnormal(
					    "error in launching delayed component",
					    null));
			    }
			}
                    }
                }
                });
        timer.start();
    }

    /**
     * Terminates the component. It is invoked by sub-components at
     * termination. If normal termiantion, Delay behaviour is to terminate
     * normally, otherwise abnormally.
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public synchronized void sfTerminatedWith(TerminationRecord status, Prim comp) {
        if (sfContainsChild(comp)) {
            if (timer != null) {
                try {
		    terminated = true;
                    timer.interrupt();
                } catch (Exception e) {
                    //ignore
                }
            }
            sfTerminate(status);
        }
    }
}
