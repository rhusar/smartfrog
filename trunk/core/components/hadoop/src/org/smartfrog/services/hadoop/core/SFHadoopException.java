/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.core;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.mortbay.util.MultiException;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.List;

/**
 *
 * Created 01-May-2008 14:37:07
 *
 */

public class SFHadoopException extends SmartFrogException {
    public static final String CONFIGURATION = "configuration";

    /**
     * Constructs a SmartFrogException with no message.
     */
    public SFHadoopException() {
    }

    /**
     * Constructs a SmartFrogException with specified message.
     *
     * @param message exception message
     */
    public SFHadoopException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogException with specified cause.
     *
     * @param cause exception causing this exception
     */
    public SFHadoopException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogException with specified message and cause.
     *
     * @param message exception message
     * @param cause   exception causing this exception
     */
    public SFHadoopException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes the exception context with component
     * details.
     *
     * @param message  exception message
     * @param sfObject The Component that has encountered the exception
     */
    public SFHadoopException(String message, Prim sfObject) {
        super(message, sfObject);
    }

    /**
     * Constructs a SmartFrogException with specified cause. Also initializes the exception context with component
     * details.
     *
     * @param cause    cause of the exception
     * @param sfObject The Component that has encountered the exception
     */
    public SFHadoopException(Throwable cause, Prim sfObject) {
        super(cause, sfObject);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes the exception context with component
     * details.
     *
     * @param message  message
     * @param cause    exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public SFHadoopException(String message, Throwable cause, Prim sfObject) {
        super(message, cause, sfObject);
    }

    /**
     * Dump the configuration to the {@link #CONFIGURATION} attribute
     * @param conf
     */
    public void addConfiguration(ManagedConfiguration conf) {
        add(CONFIGURATION,conf.dumpQuietly());
    }


    /**
     * Turn a MultiExcept into a nested exception with the stack traces in the body.
     * That's pretty nasty, but it stops the information getting lost
     * @param message header message
     * @param multiExcept nested exceptions
     * @param sfObject source
     * @return a new exception
     */
    public static SFHadoopException forward(String message, MultiException multiExcept, Prim sfObject) {
        List<Throwable> exceptions = multiExcept.getExceptions();
        int exCount = exceptions.size();
        if(exCount == 1) {
            //special case: one child.
            Throwable e = multiExcept.getException(0);
            return new SFHadoopException(message+"\n"
                    + e.getMessage(),
                    e,
                    sfObject);
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = null;
        pw = new PrintWriter(sw);
        try {
            for (Throwable thrown : exceptions) {
                thrown.printStackTrace(pw);
            }
        } finally {
            pw.close();
        }
        return new SFHadoopException(message
                +"\nmultiple ("+ exCount + ") nested exceptions: \n"
                + multiExcept.getMessage() + "\n"
                + sw.toString(),
                multiExcept,
                sfObject);
    }
}
