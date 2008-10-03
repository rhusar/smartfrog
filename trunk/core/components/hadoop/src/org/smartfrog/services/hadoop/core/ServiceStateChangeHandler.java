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
package org.smartfrog.services.hadoop.core;

import org.apache.hadoop.util.Service;

/**
 * Created 01-Oct-2008 17:32:23
 */


public interface ServiceStateChangeHandler {

    /**
     * Called on the state change.
     * @see Service#onStateChange(Service.ServiceState, Service.ServiceState)
     * @param service
     * @param oldState
     * @param newState
     */
    void onStateChange(Service service, Service.ServiceState oldState, Service.ServiceState newState);
}
