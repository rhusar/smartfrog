/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cddlm.cdl.base;

import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for things that want lifecycle events relayed
 * created 27-Oct-2006 16:02:55
 */


public interface LifecycleListener extends Remote {


    /**
     * enter a state, send notification if this is different from a state we
     * were in before This method is synchronous, you cannot enter a state till
     * the last one was processed.
     *
     * If you try and enter the current state, then nothing happens
     *
     * @param newState new state to enter
     * @param info string to record in the stateInfo field.
     * @throws java.rmi.RemoteException for network problems
     */
    void enterStateNotifying(LifecycleStateEnum newState,
                                                 String info) throws RemoteException;

    /**
     * terminate, send a message out
     *
     * @param record
     * @throws java.rmi.RemoteException for network problems
     */
    void enterTerminatedStateNotifying(
            TerminationRecord record) throws RemoteException;
}
