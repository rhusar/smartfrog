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
package org.smartfrog.services.xunit.log;

import org.smartfrog.services.xunit.base.LogListener;
import org.smartfrog.services.xunit.serial.LogEntry;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.LogRemote;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * created 18-May-2006 15:48:08
 */

public abstract class AbstractTestLog extends PrimImpl implements LogRemote, LogListener {


    protected AbstractTestLog() throws RemoteException {
    }


    /**
     * Log something at a level
     *
     * @param level  level to log at, {@link org.smartfrog.sfcore.logging.LogLevel}
     * @param text   message text
     * @param thrown 0ptional thrown fault
     * @throws RemoteException in case of remote/network error
     */
    public void log(int level, String text, Throwable thrown) throws RemoteException {
        log(createLogEntry(level, text, thrown));
    }

    /**
     * Create a log entry. Can be overridden for extra features
     * @param level
     * @param text
     * @param thrown
     * @return a new log entry
     * @throws RemoteException in case of remote/network error
     */
    protected LogEntry createLogEntry(int level, String text, Throwable thrown) {
        return new LogEntry(level, text, thrown);
    }

    /**
     * Always return true for this test
     * @throws RemoteException in case of remote/network error
     */
    public boolean isDebugEnabled() throws RemoteException {
        return true;
    }

    /**
     * Always return true for this test
     * @throws RemoteException in case of remote/network error
     */
    public boolean isErrorEnabled() throws RemoteException {
        return true;
    }

    /**
     * Always return true for this test
     * @throws RemoteException in case of remote/network error
     */
    public boolean isFatalEnabled() throws RemoteException {
        return true;
    }

    /**
     * Always return true for this test
     * @throws RemoteException in case of remote/network error
     */
    public boolean isInfoEnabled() throws RemoteException {
        return true;
    }

    /**
     * Always return true for this test
     * @throws RemoteException in case of remote/network error
     */
    public boolean isTraceEnabled() throws RemoteException {
        return true;
    }

    /**
     * Always return true for this test
     * @throws RemoteException in case of remote/network error
     */
    public boolean isWarnEnabled() throws RemoteException {
        return true;
    }

    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void trace(Object message) throws RemoteException {
        trace(message, null);
    }

    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void trace(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_TRACE, message.toString(), t);
    }

    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void debug(Object message) throws RemoteException {
        debug(message, null);
    }

    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void debug(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_DEBUG, message.toString(), t);

    }

    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void info(Object message) throws RemoteException {
        info(message, null);
    }

    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void info(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_INFO, message.toString(), t);

    }

    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void warn(Object message) throws RemoteException {
        warn(message, null);
    }

    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void warn(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_WARN, message.toString(), t);
    }

    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void error(Object message) throws RemoteException {
        error(message, null);
    }

    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void error(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_ERROR, message.toString(), t);

    }

    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     * @throws RemoteException in case of remote/network error
     */
    public void fatal(Object message) throws RemoteException {
        fatal(message, null);
    }

    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     * @throws RemoteException in case of remote/network error
     */
    public void fatal(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_FATAL, message.toString(), t);
    }
}
