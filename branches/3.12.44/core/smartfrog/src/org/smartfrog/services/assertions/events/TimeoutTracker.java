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

/**
 * A refactoring of timeout tracking logic
 */

public final class TimeoutTracker {

    private long endtime;
    private long delay;

    /**
     * Start tracking the time for a timeout
     * A delay of -1 or less means 'forever'
     * @param delay delay in milliseconds
     */
    public TimeoutTracker(long delay) {
        this.delay = delay;
        endtime = System.currentTimeMillis() + delay;
    }

    /**
     * Test for timed out-ness
     *
     * @return true iff the current time is greater than th end time,
     */
    public boolean isTimedOut() {
        return delay>=0 && System.currentTimeMillis() > endtime;
    }
}
