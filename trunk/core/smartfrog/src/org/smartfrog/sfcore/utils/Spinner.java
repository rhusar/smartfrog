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
package org.smartfrog.sfcore.utils;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Utility class to spin and timeout. It can sleep for a predefined wait interval, and will, if interrupted,
 * throw a {@link TimedOutIOException} in such a situation. 
 * </p>
 * After timeout, any exception set with {@link #setLastThrown(Throwable)} will be included
 * as a nested exception, for more meaningful stack traces
 *
 */

public class Spinner {
    private volatile String operation;
    private final long waitInterval;
    private final long endtime;
    private Throwable lastThrown;
    public static final String TIMED_OUT = " timed out ";
    private static final String WAS_INTERRUPTED = " was interrupted";

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
     * Sleep for a defined period of time. Before sleeping, a check is made for the operation
     * having timed out already.
     * @throws InterruptedIOException if the operation was interrupted
     * @throws IOException if the last thrown exception was of type IOException
     * @throws TimedOutIOException if we have already timed out and the last thrown exception is of a different type
     */
    public void sleep() throws IOException {
        if (isTimedOut()) {
            if (lastThrown != null && lastThrown instanceof IOException) {
                throw (IOException) lastThrown;
            }
            throw new TimedOutIOException(operation + TIMED_OUT
                    + (lastThrown != null ? lastThrown : ""),
                    lastThrown);
        }
        try {
            Thread.sleep(waitInterval);
        } catch (InterruptedException e) {
            //the nested exception is the one last thrown, if non-null
            throw (InterruptedIOException) new InterruptedIOException(operation + WAS_INTERRUPTED).initCause(
                    lastThrown != null ? lastThrown : e);
        }
    }

    /**
     * Test for being timed out. 
     * @return true if the current clock time is greater than the end time 
     */
    public boolean isTimedOut() {
        return System.currentTimeMillis() > endtime;
    }

    /**
     * Record the last thrown exception
     * @param lastThrown an exception to remember.
     */
    public void setLastThrown(Throwable lastThrown) {
        this.lastThrown = lastThrown;
    }

    /**
     * Get the name of the operation 
     * @return the operation name
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Set the operation field, which is used in the exception messages
     * @param operation the text to use in timeout and interrupt exceptions
     */
    public void setOperation(final String operation) {
        this.operation = operation;
    }

    /**
     * Get the wait interval
     * @return wait interval in millis
     */
    public long getWaitInterval() {
        return waitInterval;
    }

    /**
     * Get the end time
     * @return end time in milliseconds since the epoch began
     */
    public long getEndtime() {
        return endtime;
    }

    public Throwable getLastThrown() {
        return lastThrown;
    }
}