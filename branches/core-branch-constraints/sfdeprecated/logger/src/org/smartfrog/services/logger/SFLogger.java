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
 * SmartFrog Logger Interface. Defines names of Logger attributes and methods
 * for distributed logging.
 *
 * Because the logger may be running on a remote system from the caller, it is
 * important to filter out unsent messages -especially debug messages- before
 * making a call over the network. There is a method in the interface, isDebugEnabled,
 * that returns true if debug messages should be sent out.
 * Obviously, making that call before every debug message is sent does not save any time
 * either, so if it is to be used, cache the return value.
 */ 
public interface SFLogger {
    
    //SmartFrog attributes for the distributed logger
    public static final String FILE_LOGGING = "fileLogging";
    public static final String CONSOLE_LOGGING = "consoleLogging";
    public static final String LOG_DIR = "logsDir";
    public static final String LOG_FILE = "logFile";
    public static final String LOG_FORMATTER = "logFormatter";
    
    /**
     * Logs Info message.
     * @param msg the log message
     * @throws RemoteException if there is any network or RMI error
     */
    public void logInfo(String msg) throws RemoteException ;
    /**
     * Logs Warning message.
     * @param msg the log message
     * @throws RemoteException if there is any network or RMI error
     */
    public void logWarning(String msg) throws RemoteException;
    /**
     * Logs Error message.
     * @param msg the log message
     * @throws RemoteException if there is any network or RMI error
     */
    public void logError(String msg) throws RemoteException;
    /**
     * Logs a log reecord
     * @param logRec the log record
     * @throws RemoteException if there is any network or RMI error
     * @see java.util.logging.LogRecord
     */
    public void log(LogRecord logRec) throws RemoteException;

    /**
     * log at debug level
     * @param msg
     * @throws RemoteException
     * @see #isDebug()
     */


    public void logDebug(String msg) throws RemoteException;

    /**
     * message which determines whether debug logging is enabled
     * @return
     * @throws RemoteException
     */
    public boolean isDebug() throws RemoteException;
}
