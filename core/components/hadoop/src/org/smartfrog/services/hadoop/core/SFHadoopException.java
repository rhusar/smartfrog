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
package org.smartfrog.services.hadoop.core;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

/**
 *
 * Created 01-May-2008 14:37:07
 *
 */

public class SFHadoopException extends SmartFrogException {

    /**
     * Constructs a SmartFrogException with no message.
     */
    public SFHadoopException() {
    }

    /**
     * Constructs a SmartFrogException with specified message.
     *
     * @param message exception message
     */
    public SFHadoopException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogException with specified cause.
     *
     * @param cause exception causing this exception
     */
    public SFHadoopException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogException with specified message and cause.
     *
     * @param message exception message
     * @param cause   exception causing this exception
     */
    public SFHadoopException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes the exception context with component
     * details.
     *
     * @param message  exception message
     * @param sfObject The Component that has encountered the exception
     */
    public SFHadoopException(String message, Prim sfObject) {
        super(message, sfObject);
    }

    /**
     * Constructs a SmartFrogException with specified cause. Also initializes the exception context with component
     * details.
     *
     * @param cause    cause of the exception
     * @param sfObject The Component that has encountered the exception
     */
    public SFHadoopException(Throwable cause, Prim sfObject) {
        super(cause, sfObject);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes the exception context with component
     * details.
     *
     * @param message  message
     * @param cause    exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SFHadoopException(String message, Throwable cause, Prim sfObject) {
        super(message, cause, sfObject);
    }
}
