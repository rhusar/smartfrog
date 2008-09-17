/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test;

import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * this is an exception that includes a termination record
 * created 13-Oct-2006 17:33:49
 */

public class TerminationRecordException extends Exception {

    private TerminationRecord record;


    /**
     * simple constructor for deserialization
     */
    public TerminationRecordException() {
    }

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * @param record the termination record
     */
    public TerminationRecordException(TerminationRecord record) {
        super(record.getCause());
        this.record = record;
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     * @param record the termination record
     */
    public TerminationRecordException(String message, TerminationRecord record) {
        super(message,record.getCause());
        this.record = record;
    }


    /**
     * Returns a short description of this throwable.
     * If this <code>Throwable</code> object was created with a non-null detail
     * message string, then the result is the concatenation of three strings:
     * <ul>
     * <li>The name of the actual class of this object
     * <li>": " (a colon and a space)
     * <li>The result of the {@link #getMessage} method for this object
     * </ul>
     * If this <code>Throwable</code> object was created with a <tt>null</tt>
     * detail message string, then the name of the actual class of this object
     * is returned.
     *
     * @return a string representation of this throwable.
     */
    public String toString() {
        return super.toString()+record.toString();
    }
}
