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
package org.smartfrog.services.assertions;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;


/**
 * This is just a special kind of liveness exception; an assertion failure.
 * created 28-Apr-2004 11:55:42
 */
public class SmartFrogAssertionException extends SmartFrogException {
    /**
     * Constructs a SmartFrogLivenessException with message.
     *
     * @param message exception message
     */
    public SmartFrogAssertionException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogLivenessException with cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogAssertionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogLivenessException with cause. Also initializes
     * the exception context with component details.
     *
     * @param cause    exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogAssertionException(Throwable cause, Prim sfObject) {
        super(cause, sfObject);
    }

    /**
     * Constructs a SmartFrogLivenessException with message and cause.
     *
     * @param message exception message
     * @param cause   exception causing this exception
     */
    public SmartFrogAssertionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogLivenessException with message. Also initializes
     * the exception context with component details.
     *
     * @param message  message
     * @param sfObject component that encountered exception
     */
    public SmartFrogAssertionException(String message, Prim sfObject) {
        super(message, sfObject);
    }

    /**
     * Constructs a SmartFrogLivenessException with message and cause.
     * Also initializes  the exception context with component details
     *
     * @param message  message
     * @param cause    exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogAssertionException(String message, Throwable cause,
                                       Prim sfObject) {
        super(message, cause, sfObject);
    }
}
