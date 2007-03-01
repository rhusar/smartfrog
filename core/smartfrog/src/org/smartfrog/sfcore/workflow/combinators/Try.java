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

import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;


/**
 * Try is a modified compound which differs in that its primary sub-component
 * is deployed and when it terminates, a continuation component is selected
 * depending on the termination type. A Try combinator creates its primary
 * subcomponent in it's sfStart phase. The Try combinator waits for its
 * sub-component to terminate at which point it too terminates, normally selecting
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

    private Prim actionPrim;

    /**
     * Constructs Try.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Try() throws java.rmi.RemoteException {
        super();
    }


    /**
     * This is an override point. The original set of event components suppored the 'old' notation, in which actions
     * were listed in the {@link #ATTR_ACTIONS element} New subclasses do not need to remain backwards compatible and
     * should declare this fact by returning false from this method
     *
     * @return false
     */
    protected boolean isOldNotationSupported() {
        return false;
    }

    /**
     * Deploys and reads the basic configuration of the component.
     * Overrides EventCOmpoundImpl.sfStart.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        checkActionDefined();
    }

    /**
     * Deploys and manages the primary subcomponent.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while  starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        actionPrim = deployChildCD(ATTR_ACTION,true);
    }


    /**
     * Handle child termination.
     * Try behaviour for the primary child termination is
     * <ol>
     * <li> find the follow-on component and deploy</li>
     * <li> If starting the next component raised an error, terminate abnormally</li>
     * </ol>
     * Abnormal child terminations are relayed up.
     * @param status exit record of the component
     * @param comp child component that is terminating
     * @return true whenever a child component is not started
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        //get and reset the primary flag in a thread safe manner.

        boolean isAction;
        synchronized(this) {
            isAction = actionPrim==comp;
        }
        if (isAction) {
            try {
                sfRemoveChild(comp);
                if (sfLog().isDebugEnabled()) {
                    sfLog().debug("Try carrying out next Action for status '" + status.errorType + "'");
                }
                ComponentDescription nextAction = (ComponentDescription) sfResolve(status.errorType);
                if (sfLog().isDebugEnabled()) {
                    sfLog().debug("Try carrying out \n" + nextAction + " for status " + status.errorType);
                }
                sfCreateNewChild(name + "_" + status.errorType + "TryActionRunning", nextAction, null);
                return false;

            } catch (Exception e) {
                String message = "error in starting follow-on component for '" + status.errorType + "' try action";
                if (sfLog().isErrorEnabled()) {
                    sfLog().error(message, e);
                }
                sfTerminate(TerminationRecord.abnormal(message, name));
                return false;
            }
        } else {
            //not the action
            return true;
        }
    }
}
