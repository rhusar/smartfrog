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
     * name of a property naming a directory
     */
    public static final String CLASSESDIR = "test.smartfrog.classesdir";
    /**
     * name of a property naming a directory
     */
    public static final String HOSTNAME = "test.smartfrog.hostname";

    /**
     * get any test property; these are (currently) extracted from the JVM props
     * @param property
     * @param defaultValue
     * @return
     */
    public static String getTestProperty(String property, String defaultValue) {
        return System.getProperty(property, defaultValue);
    }

    /**
     * get a mandatory property for the test,
     * @param property
     * @return
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
     * @param args
     * @return
     * @throws Exception
     * @throws SmartFrogException
     * @throws RemoteException
     * @throws ConnectException
     */
    public static LocalTestDaemon startLocalDaemon(String[] args)
            throws Exception, SmartFrogException, RemoteException, ConnectException {
        return new LocalTestDaemon(args);
    }

    /**
     * start a local daemon, return a reference to it that must be disposed when we are done
     * @param options
     * @return
     * @throws Exception
     * @throws SmartFrogException
     * @throws RemoteException
     * @throws ConnectException
     */
    public static LocalTestDaemon startLocalDaemon(OptionSet options)
            throws Exception, SmartFrogException, RemoteException, ConnectException {
        return new LocalTestDaemon(options);
    }

    /**
     * a cached daemon
     */

    static private LocalTestDaemon daemon;


    /**
     * start a new daemon on demand
     * @throws Exception
     * @throws SmartFrogException
     * @throws RemoteException
     * @throws ConnectException
     */
    public static synchronized LocalTestDaemon demandStartDaemon(OptionSet options)
            throws Exception, SmartFrogException, RemoteException, ConnectException {
        if (daemon != null) {
            return daemon;
        }
        daemon = startLocalDaemon(options);
        return daemon;
    }

    /**
     * start a new daemon on demand
     * @throws Exception
     * @throws SmartFrogException
     * @throws RemoteException
     * @throws ConnectException
     */
    public static synchronized LocalTestDaemon demandStartDaemon(String[] args)
            throws Exception, SmartFrogException, RemoteException, ConnectException {
        OptionSet options = new OptionSet(args);
        return demandStartDaemon(options);
    }

    /**
     * Get the running daemon, or null if there is none yet.
     * @return
     */
    public static LocalTestDaemon getDaemon() {
        return daemon;
    }

}
