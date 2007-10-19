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
package org.smartfrog.services.logger;

import java.rmi.RemoteException;
import java.util.logging.LogRecord;

/**
 * This class is a logger proxy for (potentially) better performance
 * when logging.
 * This proxy initially just caches various settings, but reserver
 * created 15-Apr-2004 11:35:46
 */

public class LoggerProxy implements SFLogger {

    /**
     * the potentially remote logger we log to
     */
    private SFLogger log;

    /**
     * cached debug flag
     */
    private boolean debug;

    /**
     * bind to a log. Throws an assertion if the log is null.
     * @param log
     * @throws RemoteException
     */
    public LoggerProxy(SFLogger log) throws RemoteException {
        assert log!=null;
        this.log = log;
        refresh();
    }

    /**
     * refresh the local cached settings
     * @throws RemoteException
     */
    public void refresh() throws RemoteException {
        debug=log.isDebug();
    }

    /**
     * flush any pending logs
     * @throws RemoteException
     */
    public void flush() throws RemoteException {
        //this is a noop unless and until any caching is implemented.
    }

    /**
     * Logs Info message.
     *
     * @param msg the log message
     * @throws java.rmi.RemoteException if there is any network or RMI error
     */
    public void logInfo(String msg) throws RemoteException {
        log.logInfo(msg);
    }

    /**
     * Logs Warning message.
     *
     * @param msg the log message
     * @throws java.rmi.RemoteException if there is any network or RMI error
     */
    public void logWarning(String msg) throws RemoteException {
        log.logWarning(msg);

    }

    /**
     * Logs Error message.
     *
     * @param msg the log message
     * @throws java.rmi.RemoteException if there is any network or RMI error
     */
    public void logError(String msg) throws RemoteException {
        log.logError(msg);

    }

    /**
     * Logs a log record
     *
     * @param logRec the log record
     * @throws java.rmi.RemoteException if there is any network or RMI error
     * @see java.util.logging.LogRecord
     */
    public void log(LogRecord logRec) throws RemoteException {
        log.log(logRec);
    }

    /**
     * log at debug level
     *
     * @param msg
     * @throws java.rmi.RemoteException
     * @see #isDebug()
     */


    public void logDebug(String msg) throws RemoteException {
        if(isDebug()) {
            log.logDebug(msg);
        }
    }

    /**
     * message which determines whether debug logging is enabled
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public boolean isDebug() throws RemoteException {
        return debug;
    }
}
