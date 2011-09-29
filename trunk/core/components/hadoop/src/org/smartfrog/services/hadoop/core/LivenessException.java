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
package org.smartfrog.services.hadoop.core;

import java.io.IOException;

/**
 * Created 26-Aug-2009 17:05:45
 */


/**
 * This is an exception that can be raised on a liveness failure.
 */
public class LivenessException extends IOException {

    /**
     * Constructs an exception with {@code null} as its error detail message.
     */
    public LivenessException() {
    }

    /**
     * Constructs an Exception with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public LivenessException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with the specified detail message and cause.
     *
     * <p> The detail message associated with {@code cause} is only incorporated into this exception's detail message
     * when the message parameter is null.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).  (A null value
     *                is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public LivenessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an exception with the specified cause and a detail message of {@code cause.toString())}. A null cause
     * is allowed.
     *
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method). Can be null.
     */
    public LivenessException(Throwable cause) {
        super(cause);
    }
}
