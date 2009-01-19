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

/**
 * Part of the SmartFlow event infrastructure used to provide simple
 * synchronization between components Defines the extension API for EventPrim
 * and EventCompounds, providing the hooks for a component to send and receive
 * events.
 */
public interface EventBus {

    /**
     * method which forwards an event to all registered EventSinks.
     *
     * @param event java.lang.Object
     *
     * @see org.smartfrog.sfcore.workflow.eventbus.EventRegistration
     * @see org.smartfrog.sfcore.workflow.eventbus.EventSink
     */
    void sendEvent(Object event);
}
