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

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * FireBreak is a modified Parallel that when a sub-components terminates,
 * it simply removes itself from its parent and then terminates.
 * <p>
 * The file firebreak.sf contains the SmartFrog configuration file for the base
 * FireBreak combinator. This file contains the details of the attributes which
 * may be passed to FireBreak.
 * </p>
 */

public class FireBreak extends Parallel implements Compound {

    /**
     * Constructs Container.
     *
     * @throws java.rmi.RemoteException In case of network or RMI failure.
     */
    public FireBreak() throws java.rmi.RemoteException {
        super();
    }



    /**
     * It simply detaches itself from its parent and then terminates
     *
     *
     * @param status exit record of the component
     * @param comp   child component that is terminating
     * @return true if the termination event is to be forwarded up the chain.
     */
    protected boolean onChildTerminated(TerminationRecord status, Prim comp) {
        try {
            sfDetachAndTerminate(status);
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
     * comes from the parent. If the failure comes from one of the children, the component
     * removes itself from its parent and then terminates.
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
                sfDetachAndTerminate(TerminationRecord.abnormal("liveness error", sfCompleteNameSafe(), failure));
            } catch (Exception ignored) {
            }
        }
    }
}
