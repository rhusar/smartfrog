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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;


/**
 * Repeat is a modified compound which differs in that its single sub-component
 * is created and should it terminate normally, is recreated. This is repeated
 * a number of times or indefinitely unless the sub-component terminates
 * abnormally . A Repeat combinator creates no subcomponents until it's
 * sfStart phase at which point the subcomponent is created in the normal way.
 * The Repeat combinator terminates normally if the sub-component has
 * terminated normally a given number of times, and abnormally whenever the
 * component terminates abnormally. The file repeat.sf contains the SmartFrog
 * configuration file for the base Repeat combinator. This file conatins the
 * details of the attributes which may be passed to Repeat.
 */
public class Repeat extends EventCompoundImpl implements Compound {
    private static Reference retryRef = new Reference("repeat");
    private int repeat;
    private int currentRepeats = 1;

    /**
     * Constructs Repeat.
     *
     * @throws java.rmi.RemoteException In case of network or RMI error.
     */
    public Repeat() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Reads the basic configuration of the component and deploys the component.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of SmartFrog system
     *         deployment error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        checkActionDefined();
        repeat = ((Integer) sfResolve(retryRef)).intValue();
    }

    /**
     * Starts the component and the deploys and manages the subcomponents.
     *
     * @throws SmartFrogException In case of SmartFrog system error
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        sfCreateNewChild(name+"_RepeatActionRunning",
             (ComponentDescription) action.copy(), null);
    }


        /**
         * If abnormal termination, Repeat
         * behaviour is to terminate abnormally. On normal termination - repeat
         * unless some count is reached, in which case terminate normally.
         *
         * @param status exit record of the component
         * @param comp   child component that is terminating
         * @return true if the termination event is to be forwarded up the chain.
         */
        protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
            boolean forward = true;
            try {
                sfRemoveChild(comp);
                if (status.isNormal()) {
                    if (currentRepeats++ < repeat) {
                        sfCreateNewChild(name + "_actionRunning" + currentRepeats,
                                (ComponentDescription) action.copy(), null);
                        forward = false;
                    }
                }
            } catch (Exception e) {
                sfTerminate(TerminationRecord.abnormal(
                        "error in restarting next component", name, e));
                forward = false;
            }
            return forward;
        }
}
