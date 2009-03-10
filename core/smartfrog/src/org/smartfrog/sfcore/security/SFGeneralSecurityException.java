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

package org.smartfrog.sfcore.security;

import java.security.GeneralSecurityException;


/**
 * An exception thrown by the security mechanisms of SF when there is an
 * unrecoverable problem due to, e.g., cannot open my KeyStore. Note that when
 * we are notifiying an access control violation we should use a
 * SecurityException instead.
 *
 */
public class SFGeneralSecurityException extends GeneralSecurityException {
    /**
     * Class Constructor.
     */
    public SFGeneralSecurityException() {
        super();
    }

    /**
     * Class Constructor.
     *
     * @param msg exception message
     */
    public SFGeneralSecurityException(String msg) {
        super(msg);
    }

    /**
     * Creates a <code>GeneralSecurityException</code> with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     *                value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public SFGeneralSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a <code>GeneralSecurityException</code> with the specified cause and a detail message of <tt>(cause==null ?
     * null : cause.toString())</tt> (which typically contains the class and detail message of <tt>cause</tt>).
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     *              value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public SFGeneralSecurityException(Throwable cause) {
        super(cause);
    }
}
