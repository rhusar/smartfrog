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

package org.smartfrog.examples.dynamicwebserver.logging;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;


/**
 * <p>
 * Description: Logger is used to log debugging information to standar out or
 * err.
 * </p>
 *
 */
public class LoggerImpl extends PrimImpl implements Prim, Logger {
    boolean logging = true;
    boolean verbose = true;

    public LoggerImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        logging = sfResolve(LOGGING, true, false);
        verbose = sfResolve(VERBOSE, false, false);
    }

    /**
     * Log message to standard err
     *
     * @param name DOCUMENT ME!
     * @param message Message to log
     */
    public void err(String name, String message) {
        if (logging) {
            System.out.print(name + ": error: ");
            System.err.println(message);
        }
    }

    /**
     * Log message to standard out
     *
     * @param name DOCUMENT ME!
     * @param message Message to log
     */
    public void log(String name, String message) {
        if (logging) {
            System.out.print(name + ": ");
            System.out.println(message);
        }
    }

    /**
     * Log message to standard out if Verbose is true
     *
     * @param name DOCUMENT ME!
     * @param message Message to log
     */
    public void logOptional(String name, String message) {
        if (logging && verbose) {
            System.out.print(name + ": ");
            System.out.println(message);
        }
    }
}
