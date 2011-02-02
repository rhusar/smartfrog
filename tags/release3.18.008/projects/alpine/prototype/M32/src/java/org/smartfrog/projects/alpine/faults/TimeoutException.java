/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.faults;


/**
 * created 11-Apr-2006 10:34:37
 */

public class TimeoutException extends ClientException {

    /**
     * Construct an exception.
     * @param message message
     * @param cause underlying cause
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct an exception.
     * @param cause underlying cause
     */
    public TimeoutException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct an exception.
     * @param message message
     */
    public TimeoutException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with <code>null</code> as its detail
     * message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public TimeoutException() {
    }

    /**
     * Create one from a concurrent timeout
     *
     * @param timeout concurrent exception
     * @return a new instance with the message propagating
     */
    public static TimeoutException fromConcurrentTimeout(java.util.concurrent.TimeoutException timeout) {
        return new TimeoutException(timeout.getMessage());
    }
}
