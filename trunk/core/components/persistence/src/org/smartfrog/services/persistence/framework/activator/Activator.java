/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.framework.activator;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * The Activator interface is used to control the persistence framework. 
 * When active the persistence framework hosts recoverable components (RComponents) and manages
 * their interaction with the persistent store. When inactive it prevents access to 
 * the persistence store and will not host these components. Activation and deactivation 
 * includes reloading and unloading recoverable components respectively.
 */
public interface Activator {

    /**
     * Check sanity of the persisted components. This method provides a report in the
     * StringBuffer passed in regarding the components found in the storage. 
     * The report can only be generated when the framework has not been activated.
     * 
     * @param out the string buffer for the report
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void sanityCheck(StringBuffer out) throws RemoteException, SmartFrogException;

    /**
     * Terminate the activator.
     * Termination performs the deactivation operation and preempts all 
     * scheduled operations.
     */
    public void terminate();

    /**
     * <p>This method schedules activation. Activation is an asynchronous operation,
     * so successful completion of this method does not imply that activation 
     * has occurred. The activation status can be checked using the <code>getActivationStatus()</code>
     * method.</p>
     * <p>The method returns true if a new activation has been scheduled 
     * and false if not. An activation will not be scheduled if the persistence framework has been
     * terminated, if the framework is already active, or if an activation has already been
     * scheduled.</p>
     * 
     * @return true if activation was scheduled, otherwise false.
     */
    public boolean activate();

    /**
     * <p>This method initiates deactivation. Deactivation is an asynchronous operation,
     * but the initial steps of cutting the off the persistence store 
     * and closing the interface guard are guaranteed to have completed before this method returns. 
     * Recoverable components will unload asynchronously.</p> 
     * <p>The method returns true if a new deactivation had been scheduled and false if not.
     * A deactivation will not be scheduled if the persistence framework has been terminated,
     * if the framework is already inactive, or if a deactivation has already been 
     * scheduled. Deactivation preempts activation, so any activation operation that has been scheduled 
     * or is in progress will be cancelled.</p>
     * 
     * @return true if deactivation was scheduled, otherwise false.
     */
    public boolean deactivate();
    
    /**
     * Activation.Status provides the status of the last activation attempt.
     *  PENDING - the activation action is queued and has not started yet
     *  ACTIVATING - activation is in progress
     *  SUCCESS - activation completed successfully
     *  FAILURE - activation did not complete
     */
    public enum Status { PENDING, ACTIVATING, SUCCESS, FAILURE };
    
    /**
     * Get the activation status - this is the result of the last activation
     * attempt.
     * @return Activator.Status the activation status
     */
    public Status getActivationStatus();
    
}