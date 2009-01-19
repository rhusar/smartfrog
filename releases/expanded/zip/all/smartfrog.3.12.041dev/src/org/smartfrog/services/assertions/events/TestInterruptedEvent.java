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
package org.smartfrog.services.assertions.events;

import org.smartfrog.sfcore.workflow.events.LifecycleEvent;
import org.smartfrog.sfcore.prim.Prim;

/**
 * This is an even to push into the event queue to indicate that the test has been interrupted.
 * It breaks the waiting.
 * <p/>
 * Created 11-Feb-2008 15:12:41
 *
 */

public class TestInterruptedEvent extends LifecycleEvent {

    /**
     * Simple constructor
     */
    public TestInterruptedEvent() {
    }

    /**
     * Set the event to a component
     *
     * @param component component the event came from
     */
    public TestInterruptedEvent(Prim component) {
        super(component);
    }

    /**
     * {@inheritDoc}
     */
    public String getEventName() {
        return "TestInterruptedEvent";
    }
}
