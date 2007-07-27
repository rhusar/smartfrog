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

package org.smartfrog.sfcore.prim;

import java.io.Serializable;

import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogExtractedException;


/**
 * Represents a termination status for components. Components use this record
 * to indicate how they have failed. This record is passed to other components
 * in the containment tree on termination.
 *
 */
public final class TerminationRecord implements Serializable {
    /** String name for errortype normal. */
    public final static String NORMAL = "normal";

    /** String name for errortype abnormal. */
    public final static String ABNORMAL = "abnormal";

    /** String name for errortype externalReferenceDead. */
    public final static String EXTERNAL_REFERENCE_DEAD = "externalReferenceDead";

    /** Errortype. */
    public String errorType;

    /** Description. */
    public String description;

    /** id of failing component. */
    public Reference id;

    /**
     *  exception causing a failure.
     * Please do not set this directly; it is only left accessible for compatibility reasons 
     */
    public Throwable cause;

    /**
     * Constructs a new termination record.
     *
     * @param errType error type, system recognized types are "normal",
     *        "abnormal" and "externalReferenceDead".
     * @param descr description of termination
     * @param id id of failing component
     */
    public TerminationRecord(String errType, String descr, Reference id) {
        errorType = errType.intern();
        description = descr;
        this.id = id;
        setCause(null);
    }

    /**
     * Constructs a new termination record.
     * The cause will be converted into a serializable form if the exception is not
     * believed to be portable
     * @see SmartFrogExtractedException
     * @param errType error type, system recognized types are "normal",
     *        "abnormal" and "externalReferenceDead".
     * @param descr description of termination
     * @param id id of failing component
     * @param  cause the exception that caused the abnormal termination
     */
    public TerminationRecord(String errType, String descr, Reference id, Throwable cause) {
        errorType = errType.intern();
        description = descr;
        this.id = id;
        setCause(cause);
    }


    /**
     * Get the cause of the exception
     * @return the cause
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * When the cause is set, it is automatically converted to an portable form.
     * @param cause the underlying exception
      @see SmartFrogExtractedException
     */
    public void setCause(Throwable cause) {
        this.cause = SmartFrogExtractedException.convert(cause);
    }

    /**
     * Utility method. Returns a normal termination record.
     *
     * @param id id of component
     *
     * @return a SFTerminationRecord
     */
    public static TerminationRecord normal(Reference id) {
        return new TerminationRecord(NORMAL, null, id);
    }

    /**
     * Utility method. Returns a normal termination record.
     *
     * @param description description of the termination
     * @param id id of component
     * @return a SFTerminationRecord
     */
    public static TerminationRecord normal(String description, Reference id) {
        return new TerminationRecord(NORMAL, description, id);
    }

    /**
     * Create a normal termination record with a nested cause.
     *
     * It may seem odd to include an exception in a normal termination, but it is not unusual in testing 
     *
     * @param description description of the termination
     * @param id          id of component
     * @param cause an exception to include in the recird
     * @return a SFTerminationRecord
     */
    public static TerminationRecord normal(String description, Reference id, Throwable cause) {
        return new TerminationRecord(NORMAL, description, id,cause);
    }

    /**
     * Utility method. Returns an abnormal termination record.
     *
     * @param descr description of abnormal failure
     * @param id id of component
     *
     * @return a SFTerminationRecord
     */
    public static TerminationRecord abnormal(String descr, Reference id) {
        return new TerminationRecord(ABNORMAL, descr, id);
    }

    /**
     * Utility method. Returns an abnormal termination record.
     *
     * @param descr description of abnormal failure
     * @param id id of component
     * @param cause the exception that caused the abnormal termination
     *
     * @return a SFTerminationRecord
     */
    public static TerminationRecord abnormal(String descr, Reference id, Throwable cause) {
        return new TerminationRecord(ABNORMAL, descr, id, cause);
    }

    /**
     * Utility method. Returns an external failure termination record.
     *
     * @param id id of component
     *
     * @return a SFTerminationRecord
     */
    public static TerminationRecord externalReferenceDead(Reference id) {
        return new TerminationRecord(EXTERNAL_REFERENCE_DEAD,
            "External reference lost", id);
    }

    /**
     * Returns string representation of termination record.
     *
     * @return string representation of termination record
     */
    public String toString() {
        //return id + "(" + errorType + ":" + description + ")";
        return "Termination Record: " +
        (((id == null) || (id.size() == 0)) ? "" : ("" + id.toString())) +
        ((errorType == null) ? "" : (",  type: " + errorType)) +
        ((description == null) ? "" : (",  description: " + description)) +
        ((cause == null) ? "" : (",  cause: " + cause)) ;
    }

    /**
     * Test for the termination being normal, by performing
     * a case-sensitive match on the error type.
     * @return true iff errortype == "normal";
     */
    public boolean isNormal() {
        return NORMAL.equals(errorType);
    }
}
