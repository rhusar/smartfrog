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
import org.smartfrog.sfcore.reference.Reference;

/**
 * SmartFrogCompilationException is thrown when an irrecoverable error occurs
 * during the compilation of smartfrog component's description.
 *
 */
public class SmartFrogCompilationException extends SmartFrogException implements Serializable {

    /**
    *  Attribute name in exceptioncontext: The source that was trying to
    *  resolve the reference.
    */
    public static final String SOURCE = "source";

    /**
     * Constructs a SmartFrogCompilationException with no message.
     */
    public SmartFrogCompilationException() {
        super();
    }

    /**
     * Constructs a SmartFrogCompilationException with message.
     *
     * @param message exception message
     */
    public SmartFrogCompilationException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogCompilationException with message.
     *
     * @param message exception message
     * @param source an Object: source of this exception
     */
    public SmartFrogCompilationException(String message, Object source) {
        super(message);
        if (source!=null) put(SOURCE,source);
    }

    /**
     * Constructs a SmartFrogCompilationException with cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogCompilationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogCompilationException with cause.
     *
     * @param cause exception causing this exception
     * @param source an Object: source of this exception
     */
    public SmartFrogCompilationException(Throwable cause, Object source) {
        super(cause);
        if (source!=null) put(SOURCE,source);
    }


    /**
     * Constructs a SmartFrogCompilationException with cause. Also initializes
     * the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     * @param source an Object: source of this exception
     */
    public SmartFrogCompilationException(Throwable cause, Prim sfObject, Object source) {
        super(cause, sfObject);
        if (source!=null) put(SOURCE,source);
    }

    /**
     * Constructs a SmartFrogCompilationException with message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     * @param source an Object: source of this exception
     */
    public SmartFrogCompilationException(String message, Throwable cause, Object source) {
        super(message, cause);
        if (source!=null) put(SOURCE,source);
    }

    /**
     * Constructs a SmartFrogCompilationException with message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogCompilationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogCompilationException with message. Also
     * initializes the exception context with component details.
     *
     * @param message message
     * @param sfObject component that encountered exception
     * @param source an Object: source of this exception
     */
    public SmartFrogCompilationException(String message, Prim sfObject, Object source) {
        super(message, sfObject);
        if (source!=null) put(SOURCE,source);
    }

    /**
     * Constructs a SmartFrogCompilationException with message and cause.
     * Also initializes the exception context with component details.
     *
     * @param message message
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     * @param source an Object: source of this exception
     */
    public SmartFrogCompilationException(String message, Throwable cause,
        Prim sfObject,Object source) {
        super(message, cause, sfObject);
        if (source!=null) put(SOURCE,source);
    }

    /**
     * To forward SmartFrog exceptions instead of chaining them.
     * @param thr Throwable exception to be forwarded
     * @return SmartFrogException that is a SmartFrogCompilationException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogCompilationException) {
            return ((SmartFrogCompilationException)thr);
        } else {
            return new SmartFrogCompilationException (thr);
        }
    }

    /**
     * Returns a string representation of the compilation exception.
     *
     * @param nm Message separator (ex. "\n");
     * @return string representation of the compilation exception
     */
    public String toString(String nm){
        StringBuffer strb = new StringBuffer();
        strb.append(super.toString(nm));
        if ( (this.containsKey(SOURCE) && (this.get(SOURCE)!=null) && (this.get(SOURCE)instanceof Reference))){
        strb.append ((((((Reference)this.get(SOURCE)).size()!=0)))
                        ? (nm+SOURCE+  ": " + get(SOURCE)) : "" );
        } else {
         strb.append ((((this.containsKey(SOURCE)))
                    ? (nm+SOURCE+  ": " + get(SOURCE)) : ""));
        }
        return strb.toString();

    }

}
