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

package org.smartfrog.sfcore.security;


/**
 * A debugging utility that turns on/off tracing messages from the security
 * library in a consistent way.
 *
 */
public class SFDebug {
    /**
     * A flag that describes whether tracing messages should be printed or not.
     */
    private static boolean debugOn = false;

    /**
     * Force to initialize debugging when this class is loaded.
     */
    static {
        initDebug();
    }

    /** A prefix message added to each tracing log. */
    private String message = "";

    /**
     * Constructs SFDebug with the message prefix.
     *
     * @param message Prefix to be added to each tracing.
     */
    private SFDebug(String message) {
        this.message = message;
    }

    /**
     * Initializes the debugging utility.
     */
    private synchronized static void initDebug() {
        if (Boolean.getBoolean(SFSecurityProperties.propDebug)) {
            debugOn = true;
        } else {
            debugOn = false;
        }
    }

    /**
     * Factory method that returns a tracing object.
     *
     * @param message A prefix message to be added to the tracing.
     *
     * @return A tracing utility or null if debugging is off.
     */
    public synchronized static SFDebug getInstance(String message) {
        if (debugOn) {
            return new SFDebug(message);
        } else {
            return null;
        }
    }

    /**
     * Prints a debugging message to stderr, adding a prefix to it.
     *
     * @param s A message to be printed.
     */
    public void println(String s) {
        System.err.println(message + ": " + s);
    }

    /**
     * Prints an empty debugging message.
     */
    public void println() {
        println("");
    }
}
