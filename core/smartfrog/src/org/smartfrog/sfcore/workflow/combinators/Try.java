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
 * Try is a modified compound which differs in that its primary sub-component
 * is deployed and when it terminates, a continuation component is selected
 * depending on the termination type. A Try combinator creates its primary
 * subcomponent until it's sfStart phase. The Try combinator waits for its
 * sub-component to terminate at which point it too terminates normallyselects
 * the coninutation component. When this continutation component terminates,
 * it too terminates with the same termination type.
 *
 * <p>
 * The file try.sf contains the SmartFrog configuration file for the base Try
 * combinator. This file conatins the details of the attributes which may be
 * passed to Try.
 * </p>
 */
public class Try extends EventCompoundImpl implements Compound {
    static Reference actionRef = new Reference("action");
    ComponentDescription action;
    Reference name;
    int currentRetries = 0;
    boolean primary = true;

    /**
     * Constructs Try.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Try() throws java.rmi.RemoteException {
        super();
    }

    /**
     * Deploys and reads the basic configuration of the component.
     * Overrides EventCOmpoundImpl.sfStart.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        action = (ComponentDescription) sfResolve(actionRef);
        name = sfCompleteNameSafe();
    }

    /**
     * Deploys and manages the primary subcomponent.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogLifecycleException In case of any error while  starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        Prim comp = sfDeployComponentDescription("action", this, action,
                null);
        comp.sfDeploy();
        comp.sfStart();
    }

    /**
     * Terminates the component. It is invoked by sub-components on
     * termination. If it is the primary, find the follow-on component and
     * deploy. If it is not the primary, terminate propagating the termination
     * type.
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        if (sfContainsChild(comp)) {
            if (primary) {
                primary = false; // next call is not from the primary.

                try {
                    sfRemoveChild(comp);

                    ComponentDescription nextAction = (ComponentDescription) sfResolve(status.errorType);
//                    System.out.println("Try carrying out \n" + nextAction +
//                        " for status " + status.errorType);

                    Prim c = sfDeployComponentDescription(name+"_actionRunning", this,
                            nextAction, null);
                    c.sfDeploy();
                    c.sfStart();
                } catch (Exception e) {
                    sfTerminate(TerminationRecord.abnormal(
                            "error in starting follow-on component", name));
                }
            } else {
                sfTerminate(status);
            }
        }
    }
}
