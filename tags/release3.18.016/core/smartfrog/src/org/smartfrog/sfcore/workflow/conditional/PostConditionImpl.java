/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * deploys an action, then, after termination, the condition, followed by the then or else children.
 * If the parent didnt finish properly: no evaluation 
 */

public class PostConditionImpl extends ConditionCompound implements If {

    private Prim actionPrim;

    public PostConditionImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
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
        actionPrim = deployChildCD(ATTR_ACTION, true);
    }

    /**
     * If the child is the action prim and all is well:
     *
     * @param childStatus exit record of the component
     * @param child       child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    @Override
    protected boolean onChildTerminated(TerminationRecord childStatus,
                                        Prim child) {


        if (actionPrim == child && childStatus.isNormal()) {
            Exception ex;
            try {
                String branch = evaluate() ? ATTR_THEN : ATTR_ELSE;
                return deployChildCD(branch, false) == null;
            } catch (RemoteException e) {
                ex = e;
            } catch (SmartFrogException e) {
                ex = e;
            }
            sfTerminate(TerminationRecord.abnormal(
                    "Failed to start evaluate condition or start branch",
                    getName(),
                    ex));
            return false;
        }
        //forward the event
        return true;
    }
}
