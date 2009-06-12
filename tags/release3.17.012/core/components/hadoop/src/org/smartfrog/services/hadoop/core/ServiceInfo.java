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

import java.util.List;


/**
 * Created 09-Oct-2008 13:47:59
 */


public interface ServiceInfo {

    /** {@value} */
    int PORT_UNUSED = -1;
    
    /** {@value} */
    int PORT_UNDEFINED = 0;

    /**
     * Get the port used for IPC communications
     * Return {@link #PORT_UNDEFINED} if the port is not yet set, but when the system goes
     * live, it will be defined. Return {@link #PORT_UNUSED} if this service does not use this port/protocol
     * @return the port number; not valid if the service is not LIVE
     */
    int getIPCPort();

    /**
     * Get the port used for HTTP communications.
     * Return {@link #PORT_UNDEFINED} if the port is not yet set, but when the system goes
     * live, it will be defined. Return {@link #PORT_UNUSED} if this service does not use this port/protocol
     * @return the port number; not valid if the service is not LIVE.
     */
    int getWebPort();

    /**
     * Get the current number of workers
     * @return the worker count
     */

    int getLiveWorkerCount();

    /**
     * Get a list of binding information; the names of ports in use/urls, with their configuration names
     * This can be propagated back to the live component
     * @return the binding information
     */
    List<BindingTuple> getBindingInformation();
}
