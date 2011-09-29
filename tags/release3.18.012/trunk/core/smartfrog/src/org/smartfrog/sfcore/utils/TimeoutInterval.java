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
package org.smartfrog.sfcore.utils;

/**
 * Simple wrapper class to track timeout intervals; this code gets used often enough
 * that a single place for it eliminates duplication
 */

public class TimeoutInterval {

    private long endTime;
    private long startTime;
    private long delay;

    /**
     * Create a new interval
     * @param delay delay in milliseconds before timeout is declared
     */
    public TimeoutInterval(long delay) {
        startTime = System.currentTimeMillis();
        this.delay = delay;
        endTime = startTime + delay;
    }

    /**
     * When did this class get created
     * @return the time in milliseconds from which the interval is measured
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Get the delay
     * @return delay in milliseconds
     */
    public long getDelay() {
        return delay;
    }

    /**
     * Get the delay
     *
     * @return delay in seconds; rounded down
     */
    public long getDelayInSeconds() {
        return delay/1000;
    }

    /**
     * Get the time at which the interval will be deemed to have timed out
     * @return end time in milliseconds
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Check for the timeout interval expiring
     * @return true if it is now after the end time
     */
    public boolean hasTimedOut() {
        return System.currentTimeMillis() > endTime;
    }

    /**
     * Get the time since the timeout began, in millis
     * @return time since we started
     */
    public long getTimeSinceStarted() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Sleep for a period of time
     * @param interval interval to sleep
     * @return return true if the sleep completed without interruption
     */
    public boolean sleep(long interval) {
        try {
            Thread.sleep(interval);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
