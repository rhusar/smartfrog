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

/**
 * Exit error codes.
 *
 */
public final class ExitCodes {

    /** Utility class */
    private ExitCodes() {
      //private constructor
    }


    // http://www.tldp.org/LDP/abs/html/exitcodes.html
    /**
     * value of the error code returned during a failed exit when general error {@value}
     */
    public static final int EXIT_CODE_SUCCESS = 0;
    // 1 - Catchall for general errors
    /**
     * value of the error code returned during a failed exit when general error {@value}
     */
    public static final int EXIT_ERROR_CODE_GENERAL = 1;
    /**
     * value of the error code returned during a failed exit when a bad argument is used {@value}
     */

    public static final int EXIT_ERROR_CODE_BAD_ARGS = 69;
    /**
     * value of the error code returned during a failed exit when Control_ALT_Del is pressed {@value}
     */
    public static final int EXIT_ERROR_CODE_CTRL_ALT_DEL = 130;

    /**
     * Exits from the system.
     */
    public static void exitWithError() {
        exitWithError(EXIT_ERROR_CODE_GENERAL);
    }

    /**
     * Exits from the system.
     * @param exitCode int
     */
    public static void exitWithError(int exitCode) {
        exit(exitCode);
    }

    /**
     * Exits from the system.
     * This is the only place in the framework where System.exit() should be used.
     * That way a subjclass can change exit behaviour (within limits)
     * @param code int
     */
    public static void exit(int code) {
        System.exit(code);
    }


}
