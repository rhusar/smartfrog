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

import java.io.Serializable;

import org.smartfrog.sfcore.prim.Prim;

/**
 * SmartFrogContextException is thrown when an irrecoverable error occurs
 * during processing the context atributes of smartfrog component's description.
 *
 */
public class SmartFrogContextException extends SmartFrogRuntimeException implements Serializable {

    /**
     * Constructs a SmartFrogContextException with message.
     *
     * @param message exception message
     */
  public SmartFrogContextException(String message) {
    super(message);
  }

    /**
     * Constructs a SmartFrogContextException with cause.
     *
     * @param cause exception causing this exception
     */
  public SmartFrogContextException(Throwable cause) {
    super(cause);
  }

    /**
     * Constructs a SmartFrogContextException with cause. Also initializes
     * the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     *
     */
  public SmartFrogContextException(Throwable cause, Prim sfObject) {
    super(cause, sfObject);
  }

    /**
     * Constructs a SmartFrogContextException with message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     *
     */
  public SmartFrogContextException(String message, Throwable cause) {
    super(message, cause);
  }

    /**
     * Constructs a SmartFrogContextException with message. Also
     * initializes the exception context with component details.
     *
     * @param message message
     * @param sfObject component that encountered exception
     *
     */
  public SmartFrogContextException(String message, Prim sfObject) {
    super(message, sfObject);
  }

    /**
     * Constructs a SmartFrogContextException with message and cause.
     * Also initializes the exception context with component details.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     *
     */
  public SmartFrogContextException(String message, Throwable cause, Prim sfObject) {
    super(message, cause, sfObject);
  }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return SmartFrogException that is a SmartFrogContextException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogContextException) {
            return (SmartFrogContextException)thr;
        } else {
            return new SmartFrogContextException (thr);
        }
    }


    /**
     * To forward SmartFrog exceptions instead of chain them.
     * If thr is an instance of SmartFrogContextException then the exception is returned
     * without any modification, if not a new SmartFrogContextException is created
     * with message as a paramenter
     * @param message message
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogContextException
     */
    public static SmartFrogException forward (String message, Throwable thr){
        if (thr instanceof SmartFrogContextException) {
            if (message!=null){
                ((SmartFrogContextException)thr).add("msg: ",message);
            }
            return (SmartFrogContextException)thr;
        } else {
            return new SmartFrogContextException(message, thr);
        }
    }
}
