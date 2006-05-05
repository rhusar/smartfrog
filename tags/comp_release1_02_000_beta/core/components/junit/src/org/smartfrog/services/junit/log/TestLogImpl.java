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
package org.smartfrog.services.junit.log;

import org.smartfrog.services.junit.data.LogEntry;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.LogRemote;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a component that can listen to log events.
 * It declares that all levels are active (which they are), and saves all
 * logged events to buffers for later examination.
 * created 27-Apr-2006 12:53:07
 */

public class TestLogImpl extends PrimImpl implements TestLog, LogRemote {


    private List logEntries = new ArrayList();

    public TestLogImpl() throws RemoteException {
    }


    /**
     * Add an entry to the buffer
     *
     * @param entry
     */
    public void addEntry(LogEntry entry) {
        logEntries.add(entry);
    }

    /**
     * Log something at a level
     *
     * @param level  level to log at, {@link LogLevel}
     * @param text   message text
     * @param thrown 0ptional thrown fault
     */
    public void log(int level, String text, Throwable thrown) {
        addEntry(new LogEntry(level, text, thrown));
    }

    /**
     * <p> Is debug logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than debug. </p>
     */
    public boolean isDebugEnabled() throws RemoteException {
        return true;
    }

    /**
     * <p> Is error logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than error. </p>
     */
    public boolean isErrorEnabled() throws RemoteException {
        return true;
    }

    /**
     * <p> Is fatal logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than fatal. </p>
     */
    public boolean isFatalEnabled() throws RemoteException {
        return true;
    }

    /**
     * <p> Is info logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than info. </p>
     */
    public boolean isInfoEnabled() throws RemoteException {
        return true;
    }

    /**
     * <p> Is trace logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than trace. </p>
     */
    public boolean isTraceEnabled() throws RemoteException {
        return true;
    }

    /**
     * <p> Is warning logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than warn. </p>
     */
    public boolean isWarnEnabled() throws RemoteException {
        return true;
    }

    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     */
    public void trace(Object message) throws RemoteException {
        trace(message, null);
    }

    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void trace(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_TRACE, message.toString(), t);
    }

    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     */
    public void debug(Object message) throws RemoteException {
        debug(message, null);
    }

    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void debug(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_DEBUG, message.toString(), t);

    }

    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     */
    public void info(Object message) throws RemoteException {
        info(message, null);
    }

    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void info(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_INFO, message.toString(), t);

    }

    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     */
    public void warn(Object message) throws RemoteException {
        warn(message, null);
    }

    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void warn(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_WARN, message.toString(), t);
    }

    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     */
    public void error(Object message) throws RemoteException {
        error(message, null);
    }

    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void error(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_ERROR, message.toString(), t);

    }

    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     */
    public void fatal(Object message) throws RemoteException {
        fatal(message, null);
    }

    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void fatal(Object message, Throwable t) throws RemoteException {
        log(LogLevel.LOG_LEVEL_FATAL, message.toString(), t);
    }

}
