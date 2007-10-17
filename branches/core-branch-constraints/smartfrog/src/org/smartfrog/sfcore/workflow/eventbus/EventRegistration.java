/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.workflow.eventbus;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Part of the SmartFlow event infrastructure used to provide simple
 * synchronization between components The EventRegistration interface defines
 * the methods used for registring an EventSink with another SmartFlow
 * component.
 *
 * @see org.smartfrog.sfcore.workflow.eventbus.EventSink
 */
public interface EventRegistration extends EventSink, Remote {
    /**
     * Removes an EventSink from the forwarding list for an event.
     *
     * @param sink org.smartfrog.sfcore.workflow.eventbus.EventSink The event
     *        sink to remove
     *
     * @throws RemoteException In case of network.rmi error
     */
    void deregister(EventSink sink) throws RemoteException;

    /**
     * Adds an EventSink to the forwarding list for an event.
     *
     * @param sink org.smartfrog.sfcore.workflow.eventbus.EventSink The event
     *        sink to add
     * 
     * @throws RemoteException In case of network.rmi error
     */
    void register(EventSink sink) throws RemoteException;
}
