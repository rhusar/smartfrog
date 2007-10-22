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
package org.smartfrog.test;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.OptionSet;

import java.rmi.RemoteException;
import java.rmi.ConnectException;

/**
 * This class helps tests; it runs daemons and contains other code which is used across different
 * TestBase and Test classes.
 * @author steve loughran
 */

public class TestHelper {

    /**
     * name of a property naming a directory.
     * {@value}
     */
    public static final String CLASSESDIR = "test.smartfrog.classesdir";
    /**
     * name of a property naming the host.
     * {@value}
     */
    public static final String HOSTNAME = "test.smartfrog.hostname";

    /**
     * get any test property; these are (currently) extracted from the JVM props
     * @param property system property
     * @param defaultValue default value
     * @return the system property value or the default, if that is undefined
     */
    public static String getTestProperty(String property, String defaultValue) {
        return System.getProperty(property, defaultValue);
    }

    /**
     * get any test property; these are (currently) extracted from the JVM props
     *
     * @param property     system property
     * @param defaultValue default value
     * @return the system property value or the default, if that is undefined
     */
    public static int getTestPropertyInt(String property, int defaultValue) {
        return Integer.getInteger(property, defaultValue).intValue();
    }

    /**
     * get a mandatory property for the test,
     * @param property system property
     * @return the value
     * @throws RuntimeException if the property was not found
     */
    public static String getRequiredTestProperty(String property) {
        String result = getTestProperty(property, null);
        if (result == null) {
            throw new RuntimeException("Property " + property + " was not set");
        }
        return result;
    }

    /**
     * start a local daemon, return a reference to it that must be disposed when we are done
     * @param args daemon arguments
     * @return a local daemon
     * @throws Exception if something went wrong
     */
    public static LocalTestDaemon startLocalDaemon(String[] args)
            throws Exception {
        return new LocalTestDaemon(args);
    }

    /**
     * start a local daemon, return a reference to it that must be disposed when we are done
     * @param options options for the daemon
     * @return running daemon
     * @throws Exception if something went wrong
     */
    public static LocalTestDaemon startLocalDaemon(OptionSet options)
            throws Exception {
        return new LocalTestDaemon(options);
    }

    /**
     * a cached daemon
     */

    static private LocalTestDaemon daemon;


    /**
     * start a new daemon on demand
     * @param options options for the daemon
     * @return the daemon
     * @throws Exception if something went wrong
     */
    public static synchronized LocalTestDaemon demandStartDaemon(OptionSet options)
            throws Exception {
        if (daemon != null) {
            return daemon;
        }
        daemon = startLocalDaemon(options);
        return daemon;
    }

    /**
     * start a new daemon on demand
     * @param args daemon arguments
     * @throws Exception if something went wrong
     * @return the daemon
     */
    public static synchronized LocalTestDaemon demandStartDaemon(String[] args)
            throws Exception {
        OptionSet options = new OptionSet(args);
        return demandStartDaemon(options);
    }

    /**
     * Get the running daemon, or null if there is none yet.
     * @return the running daemon
     */
    public static LocalTestDaemon getDaemon() {
        return daemon;
    }

}
