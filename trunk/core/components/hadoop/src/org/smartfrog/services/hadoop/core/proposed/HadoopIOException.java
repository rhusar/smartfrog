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
package org.smartfrog.services.hadoop.core.proposed;

import java.io.IOException;

/**
 *
 * Created 19-May-2008 12:07:53
 *
 */

public class HadoopIOException extends IOException {
    /**
     * Constructs an {@code IOException} with {@code null} as its error detail message.
     */
    public HadoopIOException() {
    }

    /**
     * Constructs an {@code IOException} with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public HadoopIOException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code IOException} with the specified detail message and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated into this
     * exception's detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).  (A null value is
     *                permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public HadoopIOException(String message, Throwable cause) {
        super(message==null?cause.getMessage() : null);
        initCause(cause);
    }

    /**
     * Constructs an {@code IOException} with the specified cause and a detail message of {@code (cause==null ? null :
     * cause.toString())} (which typically contains the class and detail message of {@code cause}). This constructor is
     * useful for IO exceptions that are little more than wrappers for other throwables.
     *
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method).  (A null value is
     *              permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 1.6
     */
    public HadoopIOException(Throwable cause) {
        super(cause.getMessage());
        initCause(cause);
    }
}
