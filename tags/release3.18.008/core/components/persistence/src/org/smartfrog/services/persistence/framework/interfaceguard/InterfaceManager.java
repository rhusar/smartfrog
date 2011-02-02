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

package org.smartfrog.services.persistence.framework.interfaceguard;

import java.util.Vector;

import org.smartfrog.services.persistence.framework.activator.PendingTermination;
import org.smartfrog.services.persistence.storage.StorageExceptionNotification;

/**
 * This interface is used to obtain access to the interface guard, the list 
 * of recovery hosts, and the pending termination list. 
 */
public interface InterfaceManager extends StorageExceptionNotification {

    /**
     * Get the interface guard.
     * @return the interface guard
     */
    public InterfaceGuard getInterfaceGuard();
    
    /**
     * Get the vector of hosts that are used for recovery. 
     * @return the list of recovery hosts as a vector
     */
    public Vector<String> getRecoveryHosts();
    
    /**
     * Get the set of things that are pending termination
     * @return the list of components pending termination
     */
    public PendingTermination getPendingTermination();
    

}