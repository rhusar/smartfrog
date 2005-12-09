/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.faults;

import org.ggf.cddlm.utils.FaultCodeComparator;
import org.ggf.cddlm.utils.FaultTemplate;

import javax.xml.namespace.QName;


/**
 * Base class for our exceptions
 * created 10-Jun-2005 15:29:38
 */

public class CdlException extends Exception implements FaultCodeComparator {

    private QName faultCode;

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CdlException() {
        setFaultCode(createDefaultFaultCode());
    }

    /**
     * Constructs a new exception with the specified detail message.  The cause
     * is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *                retrieval by the {@link #getMessage()} method.
     */
    public CdlException(String message) {
        super(message);
        setFaultCode(createDefaultFaultCode());
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval by
     *                the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public CdlException(String message, Throwable cause) {
        super(message, cause);
        setFaultCode(createDefaultFaultCode());
    }

    /**
     * Constructs a new exception from the fault template.
     * The error message and the fault code are extracted from the template
     */
    public CdlException(FaultTemplate template) {
        super(template.getErrorMessage());
        setFaultCode(template.getQualifiedName());
    }

    /**
     * Override point called in constructor (danger, danger),
     * to get the default fault code. This is not called when
     * creating a fault using the FaultTemplate-based ctor, but
     * is in all other cases
     */
    protected QName createDefaultFaultCode() {
        return null;
    }
    /**
    * Constructs a new exception with the specified cause and a detail message
    * of <tt>(cause==null ? null : cause.toString())</tt> (which typically
    * contains the class and detail message of <tt>cause</tt>).
    *
    * @param cause the cause (which is saved for later retrieval by the {@link
    *              #getCause()} method).  (A <tt>null</tt> value is permitted,
    *              and indicates that the cause is nonexistent or unknown.)
    */
    public CdlException(Throwable cause) {
        super(cause);
    }

    public QName getFaultCode() {
        return faultCode;
    }

    private void setFaultCode(QName faultCode) {
        this.faultCode = faultCode;
    }

    /**
     * Test for an expected fault code matching the actual value of the object
     * The concept "fault code of the object" is deliberately undefined; it could be a static
     * value, it could be the fault code of a nested fault.
     *
     * @param expected fault code to check
     * @return true if and only iff the fault code of the object matches the supplied faultCode.
     */
    public boolean isFault(QName expected) {
        if(faultCode==null) {
            return expected==null;
        }
        return faultCode.equals(expected);
    }
}
