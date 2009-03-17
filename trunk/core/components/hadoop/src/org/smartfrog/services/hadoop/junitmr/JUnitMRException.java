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
package org.smartfrog.services.hadoop.junitmr;

/**
 * Created 17-Mar-2009 14:11:27
 */

public class JUnitMRException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public JUnitMRException() {
    }

    /**
     * {@inheritDoc}
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     */
    public JUnitMRException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A
     *                <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public JUnitMRException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     *              value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public JUnitMRException(Throwable cause) {
        super(cause);
    }
}
