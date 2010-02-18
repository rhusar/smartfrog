/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;

/**
 *  A test has completed
 *  There's no reference here,
 */

public class TestCompletedEvent extends LifecycleEvent {

    private String description=null;
    private boolean succeeded = false;
    private boolean forcedTimeout = false;
    private boolean skipped = false;


    public TestCompletedEvent() {
    }


    public TestCompletedEvent(Prim component,
                              boolean succeeded,
                              boolean forcedTimeout,
                              boolean skipped,
                              TerminationRecord status,
                              String description) {
        super(component,status);
        //forget about the component value so there are no
        //serialization problems
        resetComponent();
        this.succeeded = succeeded;
        this.forcedTimeout = forcedTimeout;
        this.skipped = skipped;
        this.description=description;
    }



    public boolean isSucceeded() {
        return succeeded;
    }

    public boolean isForcedTimeout() {
        return forcedTimeout;
    }

    public boolean isFailed() {
        return !succeeded;
    }
    public boolean isSkipped() {
        return skipped;
    }

    /**
     * {@inheritDoc}
     */
    public String getEventName() {
        return "TestCompletedEvent";
    }

    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder(super.toString());
        if (description != null && description.length() > 0) {
            buffer.append('\n');
            buffer.append(description);
            buffer.append('\n');
        }
        buffer.append("\nsucceeded:").append(succeeded);
        buffer.append("\nforcedTimeout:").append(forcedTimeout);
        buffer.append("\nskipped:").append(skipped);
        buffer.append('\n');
        return buffer.toString();
    }
}
