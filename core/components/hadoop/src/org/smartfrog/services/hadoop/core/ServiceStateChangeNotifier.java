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
import org.smartfrog.sfcore.prim.Prim;

/**
 * Code to handle the notification tracking/registration for any of the node classes. Created 01-Oct-2008 17:34:16
 */

public class ServiceStateChangeNotifier {

    private Service service;
    private ServiceStateChangeHandler handler;

    public ServiceStateChangeNotifier(Service service, ServiceStateChangeHandler handler) {
        this.service = service;
        this.handler = handler;
    }

    /**
     * Set the handler to be non null only if the owner implements the interface
     *
     * @param service
     * @param owner
     */
    public ServiceStateChangeNotifier(Service service, Prim owner) {
        this.service = service;
        if (owner instanceof ServiceStateChangeHandler) {
            handler = (ServiceStateChangeHandler) owner;
        }
    }

    public void onStateChange(Service.ServiceState oldState, Service.ServiceState newState) {
        if (handler != null) {
            handler.onStateChange(service, oldState, newState);
        }
    }
}
