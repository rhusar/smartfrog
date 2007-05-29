/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
import java.rmi.RemoteException;

/**
 * A SmartFrogExtractedException is a special exception that can be built from existing exceptions.
 * The result is an exception that can be sent over the wire to a remote recipient without any requirement
 * for dynamic loading of all possible exceptions that can be sent at the far end.
 *
 * Many of the base methods of Throwable have been overridden to make the wrap more subtle.
 */
public class SmartFrogExtractedException extends SmartFrogException implements Serializable {

    /**
     * {@value}
     */
    public static final String CONTEXT_MSG = "msg: ";

    /** {@value} */
    public static final String EXCEPTION_STACK = "ExceptionStack";

    /** {@value} */
    public static final String EXCEPTION_MESSAGE = "ExceptionMessage";

    /** {@value} */
    public static final String EXCEPTION_CLASSNAME = "ExceptionClassname";

    /** {@value} */
    public static final String EXCEPTION_LOCALIZED_MESSAGE = "ExceptionLocalizedMessage";

    /**
     * Constructs a SmartFrogLogException with message.
     *
     * @param message exception message
     */
    public SmartFrogExtractedException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogLogException with cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogExtractedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogLogException with cause. Also initializes
     * the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param sfObject component that encountered exception
     */
    public SmartFrogExtractedException(Throwable cause, Prim sfObject) {
        super(cause);
        init(sfObject);
    }

    /**
     * Constructs a SmartFrogLogException with message and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogExtractedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogLogException with message. Also initializes
     * the exception context with component details.
     *
     * @param message message
     * @param sfObject component that encountered exception
     */
    public SmartFrogExtractedException(String message, Prim sfObject) {
        super(message,sfObject);
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return SmartFrogException that is a SmartFrogLogException
     */
    public static SmartFrogException forward (Throwable thr){
        return convert(thr);
    }

    /**
     * To forward SmartFrogExtractedException exceptions instead of chain them.
     * If thr is an instance of SmartFrogExtractedException then the exception is returned
     * without any modification, if not a new SmartFrogLogException is created
     * with message as a paramenter
     * @param message message
     * @param thr throwable object to be forwarded
     * @return Throwable that is a SmartFrogLogException
     */
    public static SmartFrogException forward (String message, Throwable thr){
        SmartFrogException exception = convert(thr);
        if (message != null) {
            exception.add(CONTEXT_MSG, message);
        }
        return exception;
    }

    /**
     * Fill in the attributes from the thrown exception
     * @param thrown the thrown exception
     */
    private void fillInFromThrowable(Throwable thrown) {
        add(EXCEPTION_CLASSNAME, thrown.getClass().getName());
        add(EXCEPTION_MESSAGE, thrown.getMessage());
        add(EXCEPTION_LOCALIZED_MESSAGE, thrown.getLocalizedMessage());
        add(EXCEPTION_STACK, thrown.getStackTrace());
        setStackTrace(thrown.getStackTrace());
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this <tt>Throwable</tt> instance (which may be <tt>null</tt>).
     */
    public String getMessage() {
        Object o = get(EXCEPTION_MESSAGE);
        if (o != null) {
            return (String) o;
        } else {
            return super.getMessage();
        }
    }


    /**
     * Creates a localized description of this throwable. Subclasses may override this method in order to produce a
     * locale-specific message.  For subclasses that do not override this method, the default implementation returns the
     * same result as <code>getMessage()</code>.
     *
     * @return The localized description of this throwable.
     * @since JDK1.1
     */
    public String getLocalizedMessage() {
        Object o = get(EXCEPTION_LOCALIZED_MESSAGE);
        if (o != null) {
            return (String) o;
        } else {
            return super.getLocalizedMessage();
        }
    }

    /**
     * Gets class name.
     *
     * @return Class name
     */
    public String shortClassName() {
        Object o = get(EXCEPTION_CLASSNAME);
        if (o != null) {
            String name=(String) o;
            return name.substring(
                    name.lastIndexOf('.') + 1);
        } else {
            return super.shortClassName();
        }

    }

    /**
     * Recursive conversion of an exception into a new exception hierarchy, for which
     * all entries are of type SmartFrogException.
     *
     * Algorithm.
     * <ol>
     * <li>Recurse down to the base nested cause.</li>
     * <li>If it is a SmartFrogException or a subclass, return it</li>
     * <li>otherwise, create a new SmartFrogExtractedException from it</li>
     * <li>If the nested cause is the same object as passed down, it means it was not changed.
     * the parent is then returned as is (if a SmartFrogException), or, if not, it is replaced by
     * a  SmartFrogExtractedException</li>
     * <ol>
     *
     * @param thrown the fault that was thrown.
     * @return a new (potentially nested) fault tree, in which all Exceptions have been converted to a serialized
     * form that the far end can hande.
     */
    public static SmartFrogException convert(Throwable thrown) {
        boolean isSmartFrogException=thrown instanceof SmartFrogException;
        Throwable cause = thrown.getCause();
        SmartFrogException exception;
        boolean hasCause=cause!=null && cause!=thrown;
        if(hasCause) {
            //if we have a cause, recursively convert us
            SmartFrogException newCause=convert(cause);
            if (isSmartFrogException && newCause == cause) {
                //no change in the child, and we are of the right type
                exception = (SmartFrogException) thrown;
            } else {
                //the child changed or we are of the wrong type
                exception = createFromThrowable(thrown, newCause);
            }
        } else {
            //no children. This is the base of the cause tree.
            if(isSmartFrogException) {
                //cast to a SmartFrogException and return unchanged.
                exception = (SmartFrogException) thrown;
            } else {
                //we need to remarshall us
                exception = createFromThrowable(thrown,null);
            }
        }
        return exception;
    }

    /**
     * Create a new exception from a throwable
     * @param thrown the thrown exception
     * @param cause any nested cause; can be null
     * @return an exception with the parame
     */
    private static SmartFrogExtractedException createFromThrowable(Throwable thrown, SmartFrogException cause) {
        SmartFrogExtractedException fault = new SmartFrogExtractedException(thrown.getMessage());
        fault.fillInFromThrowable(thrown);
        if(cause!=null) {
            fault.initCause(cause);
        }
        return fault;
    }
}
