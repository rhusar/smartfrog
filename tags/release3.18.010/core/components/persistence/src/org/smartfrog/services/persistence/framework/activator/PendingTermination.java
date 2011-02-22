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

import org.smartfrog.services.persistence.rcomponent.RComponent;

/**
 * This interface is used for managing the list of recoverable components which
 * are pending for termination. Due to locking issues, recoverable components
 * are detached and then terminated in two separate transactions. the pending
 * termination list is a list of components that have been detached but not yet 
 * terminated. If a failure happens in between these two steps the pending components
 * will be recovered as orphans and so terminated automatically then.
 */
public interface PendingTermination {
    
	/**
	 * Add a component to the list of components pending termination.
	 * @param rcomponent the component that is pending termination
	 */
    public void add(RComponent rcomponent);
    
    /**
     * Remove a component from the list of components pending termination.
     * @param rcomponent the component to remove
     */
    public void remove(RComponent rcomponent);
}
