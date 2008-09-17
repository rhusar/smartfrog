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


package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;

import java.io.Serializable;

/**
 * A SmartFrogInitException is thrown if smartfrog initialization encounters
 * errors.
 *
 */
public class SmartFrogInitException extends SmartFrogException implements Serializable {
    /**
     * Constructs a SmartFrogInitException with specified message.
     *
     * @param message exception message
     */
    public SmartFrogInitException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogInitException with specified cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogInitException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogInitException with specified cause. Also
     * initializes the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogInitException(Throwable cause, Prim sfObject) {
        super(cause, sfObject);
    }

    /**
     * Constructs a SmartFrogInitException with specified message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogInitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogInitException with specified message. Also
     * initializes the exception context with component details.
     *
     * @param message message
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogInitException(String message, Prim sfObject) {
        super(message, sfObject);
    }

    /**
     * Constructs a SmartFrogInitException with specified message and cause.
     * Also initializes the exception context with component details.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SmartFrogInitException(String message, Throwable cause,
        Prim sfObject) {
        super(message, cause, sfObject);
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return Throwable that is a SmartFrogException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogInitException) {
            return (SmartFrogInitException)thr;
        } else {
            return new SmartFrogInitException (thr);
        }
    }

    /**
     * To forward SmartFrogInitException exceptions instead of chain them.
     * If thr is an instance of SmartFrogInitException then the exception is returned
     * without any modification, if not a new SmartFrogInitException is created
     * with message as a paramenter
     * @param message String message
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogInitException
     */
    public static SmartFrogException forward (String message, Throwable thr){
        if (thr instanceof SmartFrogInitException) {
            if (message!=null){
                ((SmartFrogException)thr).add("msg: ",message);
            }
            return (SmartFrogInitException)thr;
        } else {
            return new SmartFrogInitException(message, thr);
        }
    }

}
