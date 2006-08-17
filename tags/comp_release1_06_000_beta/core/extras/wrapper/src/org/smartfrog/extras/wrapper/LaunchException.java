/*
 * Copyright  2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.smartfrog.extras.wrapper;

/**
 * Signals an error condition during launching Derived from apache ant
 */
public class LaunchException extends Exception {

    /**
     * what our exit code should be
     */
    int exitCode;

    /**
     * the exit code if none is specified
     */
    public static final int DEFAULT_EXIT_CODE = -1;

    /**
     * Constructs an exception with the given descriptive message.
     *
     * @param message A description of or information about the exception.
     *                Should not be <code>null</code>.
     */
    public LaunchException(String message) {

        this(DEFAULT_EXIT_CODE, message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param exitCode exit code
     * @param message  the detail message (which is saved for later retrieval by
     *                 the {@link #getMessage()} method).
     * @since 1.4
     */
    public LaunchException(int exitCode,
            String message) {
        super(message);
        this.exitCode = exitCode;
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param exitCode exit code
     * @param message  the detail message (which is saved for later retrieval by
     *                 the {@link #getMessage()} method).
     * @param cause    the cause (which is saved for later retrieval by the
     *                 {@link #getCause()} method).  (A <tt>null</tt> value is
     *                 permitted, and indicates that the cause is nonexistent or
     *                 unknown.)
     * @since 1.4
     */
    public LaunchException(int exitCode,
            String message,
            Throwable cause) {
        super(message, cause);
        this.exitCode = exitCode;
    }

}
