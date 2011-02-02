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
 * A SmartFrogRuntimeException is thrown when an irrecoverable error occurs
 * while running a SmartFrog component.
 *
 */
public class SmartFrogRuntimeException extends SmartFrogException implements Serializable {

    /** Attribute name for reference in exceptioncontext. */
    public static final String REFERENCE = "reference";

    /** Attribute name in exceptioncontext: The source of the exception. */
    public static final String SOURCE = "source";

    /**
     * Constructs a SmartFrogRuntimeException with message.
     *
     * @param message exception message
     */
    public SmartFrogRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogRuntimeException with cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogRuntimeException with cause. Also initializes
     * the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogRuntimeException(Throwable cause, Prim sfObject) {
        super(cause);
        init(sfObject);
    }

    /**
     * Constructs a SmartFrogRuntimeException with message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogRuntimeException with message. Also initializes
     * the exception context with component details.
     *
     * @param message message
     * @param sfObject component that encountered exception
     */
    public SmartFrogRuntimeException(String message, Prim sfObject) {
        super(message);
        init(sfObject);
    }


    /**
     * Constructs a SmartFrogRuntimeException with message and cause.
     * Also initializes  the exception context with component details.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogRuntimeException(String message, Throwable cause,
         Prim sfObject) {
        super(message, cause);
        init(sfObject);
    }

    private void init(){
    }
    /**
     * Initializes the exception context with the SmartFrog component.
     *
     * @param sfObject component that encountered exception
     */
    public void init(Prim sfObject){
        if (sfObject == null) return;
        try {
            super.init(sfObject);
        } catch (Throwable rex){
            //ignore
        }
        try {
           add(REFERENCE,sfObject.sfCompleteName().copy());
        } catch (Throwable rex){
            //ignore
        }
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return SmartFrogException that is a SmartFrogRuntimeException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogRuntimeException) {
            return (SmartFrogRuntimeException)thr;
        } else {
            return new SmartFrogRuntimeException (thr);
        }
    }


    /**
     * To forward SmartFrog exceptions instead of chain them.
     * If thr is an instance of SmartFrogRuntimeException then the exception is returned
     * without any modification, if not a new SmartFrogRuntimeException is created
     * with message as a paramenter
     * @param message message
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogRuntimeException
     */
    public static SmartFrogException forward (String message, Throwable thr){
        if (thr instanceof SmartFrogRuntimeException) {
            if (message!=null){
                ((SmartFrogRuntimeException)thr).add("msg: ",message);
            }
            return (SmartFrogRuntimeException)thr;
        } else {
            return new SmartFrogRuntimeException(message, thr);
        }
    }


    /**
     * Returns a string representation of the runtime exception.
     *
     * @param nm  Message separator (ex. "\n");
     *
     * @return reason source and ref of exception
     */
    public String toString(String nm) {
        StringBuilder strb = new StringBuilder();
        strb.append(super.toString(nm));
        strb.append ((((this.containsKey(REFERENCE))) ? (nm+REFERENCE+  ": "
                                                    + get(REFERENCE)) : "" ));
        strb.append ((((this.containsKey(SOURCE))) ? (nm+SOURCE+  ": "
                                                    + get(SOURCE)) : "" ));
        strb.append ((((this.containsKey(PRIM_CONTEXT))) ? (nm+PRIM_CONTEXT
                                            +  ": " + "included") : "" ));
        return strb.toString();
    }
}
