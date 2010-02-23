/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.dependencies;

/**
 * Utility class to spin and timeout
 * </p>
 * After timeout, any exception set with {@link #setLastThrown(Throwable)} will be included
 * as a nested exception, for more meaningful stack traces
 *
 * THIS IS A QUICK HACK - Will be moved into SF once talked to Steve...
 */

public class Spinner {
    private String operation;
    private long waitInterval;
    private long endtime;
    private Throwable lastThrown;

    /**
     * Spin until timeout. The timeout is constructed from now+timeout, so create
     * this object immediately before you need it
     * @param operation operation to be used in error messages
     * @param waitInterval interval in milliseconds for a sleep
     * @param timeout timeout in milliseconds.
     */
    public Spinner(String operation, long waitInterval, long timeout) {
        this.operation = operation;
        this.waitInterval = waitInterval;
        endtime = System.currentTimeMillis() + timeout;
    }

    /**
     * Sleep for a defined period of time
     * @throws Exception if we have already timed out. This check occurs before any sleep
     */
    public void sleep() throws Exception {
        if (System.currentTimeMillis() > endtime) {
            throw new Exception(operation + " timed out", lastThrown);
        }
        try {
            Thread.sleep(waitInterval);
        } catch (InterruptedException e) {
            throw new Exception(operation + " was interrupted", lastThrown);
        }
    }

    /**
     * Record the last thrown exception
     * @param lastThrown an exception to remember.
     */
    public void setLastThrown(Throwable lastThrown) {
        this.lastThrown = lastThrown;
    }
}