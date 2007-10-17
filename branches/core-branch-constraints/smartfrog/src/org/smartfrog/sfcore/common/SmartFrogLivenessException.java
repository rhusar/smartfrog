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

import java.rmi.RemoteException;
import java.io.Serializable;

import org.smartfrog.sfcore.prim.Prim;

/**
 * A SmartFrogLivenessException is thrown when a liveness test
 * fails
 * @see org.smartfrog.sfcore.prim.Liveness#sfPing(Object)
 *
 */
public class SmartFrogLivenessException extends SmartFrogRuntimeException implements Serializable {

    /** Attribute name for reference in exceptioncontext. */
    public static final String REFERENCE = "reference";

    /** Attribute name for source in exceptioncontext. */
    public static final String SOURCE = "source";

    /** Attribute name for data in exceptioncontext. */
    public static final String DATA = "data";


    /**
     * Constructs a SmartFrogLivenessException with message.
     *
     * @param message exception message
     */
    public SmartFrogLivenessException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogLivenessException with cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogLivenessException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogLivenessException with cause. Also initializes
     * the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogLivenessException(Throwable cause, Prim sfObject) {
        super(cause);
        init(sfObject);
    }

    /**
     * Constructs a SmartFrogLivenessException with message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogLivenessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogLivenessException with message. Also initializes
     * the exception context with component details.
     *
     * @param message message
     * @param sfObject component that encountered exception
     */
    public SmartFrogLivenessException(String message, Prim sfObject) {
        super(message);
        init(sfObject);
    }


    /**
     * Constructs a SmartFrogLivenessException with message and cause.
     * Also initializes  the exception context with component details
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogLivenessException(String message, Throwable cause,
         Prim sfObject) {
        super(message, cause);
        init(sfObject);
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     * @param thr throwable object to be forwarded
     * @return SmartFrogException that is a SmartFrogLivenessException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogLivenessException) {
            return (SmartFrogLivenessException)thr;
        } else {
            return new SmartFrogLivenessException (thr);
        }
    }

    /**
     * To forward SmartFrogLivenessException exceptions instead of chain them.
     * If thr is an instance of SmartFrogLivenessException then the exception is returned
     * without any modification, if not a new SmartFrogLivenessException is created
     * with message as a paramenter
     * @param message message
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogLivenessException
     */
    public static SmartFrogException forward (String message, Throwable thr){
        if (thr instanceof SmartFrogLivenessException) {
            if (message!=null){
                ((SmartFrogException)thr).add("msg: ",message);
            }
            return (SmartFrogLivenessException)thr;
        } else {
            return new SmartFrogLivenessException(message, thr);
        }
    }


    /**
     * Initializes the exception context with the SmartFrog component.
     *
     * @param sfObject component that encountered exception
     */
    public void init(Prim sfObject){
        if (sfObject == null) return;
        super.init(sfObject);
        try {
           add(REFERENCE,sfObject.sfCompleteName());
        } catch (RemoteException rex){
            //ignore
        }
        try {
           add(REFERENCE,sfObject.sfCompleteName());
        } catch (RemoteException rex){
            //ignore
        }

    }
}


