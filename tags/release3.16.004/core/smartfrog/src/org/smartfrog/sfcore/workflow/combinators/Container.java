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
import java.util.Enumeration;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;

/**
 * Container is a modified Parallel which differs in that when any the sub-components terminates
 * or fails it simply removes the child and continues to run.
 * <p>
 * The file parallel.sf contains the SmartFrog configuration file for the base
 * Parallel combinator. This file conatins the details of the attributes which
 * may be passed to Parallel.
 * </p>
 */
public class Container extends Parallel implements Compound {

    /**
     * Constructs Container.
     *
     * @throws RemoteException In case of network or RMI failure.
     */
    public Container() throws RemoteException {
    }


    /**
     * It is invoked by sub-components at
     * termination. It simply removes the child and continues to run
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
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
        try {
            sfRemoveChild(comp);
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error(sfCompleteNameSafe() + " - error handling child termination ", e);
            }
        }
        return false;
    }


    /**
     * Handle ping failures. Default behavior is to terminate with a liveness
     * send failure record storing the name of the target of the ping when the failure
     * comes from the parent. If the failure comes from one of the children, the child is
     * removed and the component continues to run.
     *
     * @param source source of update
     * @param target target that update was trying to reach
     * @param failure error that occurred
     */
    protected void sfLivenessFailure(Object source, Object target, Throwable failure) {
        if (target.equals(sfParent)) {
            super.sfLivenessFailure(source, target, failure);
        } else {
            try {
                ((Prim)target).sfTerminate(TerminationRecord.abnormal("liveness error", sfCompleteNameSafe(), failure));
            } catch (Exception ignored) { // expected since it is supposedly dead
            }
            try {
                sfRemoveChild((Prim)target);
            } catch (SmartFrogRuntimeException e) {
                //ignore
                sfLog().info("While trying to remove the child",e);

            } catch (RemoteException e) {
                //ignore
                sfLog().info("While trying to remove the child", e);
            }
        }
    }
}
