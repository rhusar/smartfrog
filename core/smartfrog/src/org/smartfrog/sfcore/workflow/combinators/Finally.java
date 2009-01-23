/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * This component runs an action when a termination request is received. The action is deployed on startup, but it is
 * only moved into the started state when the Try component is to be terminated.
 * <p/>
 * You can use this component to deploy some action for cleanup, such as the execution of a remote shutdown command.
 * <p/>
 * Be warned that there is no way to guarantee that the finally action will always be started. Emergency system
 * shutdowns can bypass this operation
 */
public class Finally extends EventCompoundImpl implements Compound {

    private Prim finallyPrim;
    private static final String FINALLY_CHILD_NAME = "action";

    /**
     * Constructs Try.
     *
     * @throws java.rmi.RemoteException In case of RMI or network failure.
     */
    public Finally() throws java.rmi.RemoteException {
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
     * Deploys and reads the basic configuration of the component. Overrides EventCOmpoundImpl.sfStart.
     *
     * @throws java.rmi.RemoteException In case of network/rmi error
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  In case of any error while deploying the component
     */
    public synchronized void sfDeploy() throws
            SmartFrogException, RemoteException {
        super.sfDeploy();
        checkActionDefined();
    }

    /**
     * Deploys and manages the primary subcomponent.
     *
     * @throws RemoteException    In case of network/rmi error
     * @throws SmartFrogException In case of any error while  starting the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        //Copies component description before deploying it!
        finallyPrim= deployComponentDescription(FINALLY_CHILD_NAME, action);
    }

    /**
     * When we terminate, we deploy the finally child.
     * @param status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        if(finallyPrim!=null) {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("Starting the Finally Action");
            }
            try {
                finallyPrim.sfStart();
            } catch (SmartFrogException e) {
                sfLog().error("When starting the finally action",e);
            } catch (RemoteException e) {
                sfLog().error("When starting the finally action", e);
            }
        }
        super.sfTerminateWith(status);
    }
}
