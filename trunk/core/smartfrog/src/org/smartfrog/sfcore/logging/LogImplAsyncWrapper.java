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

package org.smartfrog.sfcore.logging;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import java.lang.reflect.Method;
import java.sql.Timestamp;

/**
 * LogImplAsyncWrapper is a wrapper class for LogImpl that
 * implements asynchronous logging. All log method calls
 * are queued and executed by a worker thread instead of the
 * calling thread.
 */
public class LogImplAsyncWrapper implements LogSF {

    private        LogImpl        logImpl;
    private static LogAsyncQueue  logQueue;
    private static LogAsyncThread worker;

    static {
        logQueue = new LogAsyncQueue();
        worker   = new LogAsyncThread(logQueue);
        worker.setName("Async Logging");
        worker.setDaemon(true);
        worker.start();
    }


    public LogImplAsyncWrapper(LogImpl logImpl) {
        this.logImpl = logImpl;
    }


    public LogImplAsyncWrapper(LogSF logSF) {
        this.logImpl = (LogImpl)logSF;
    }


    /**
     * Generic method for adding requests to the log queue
     */
    private void queue(Method method, Object[] params) {
        params[0] = timeStamp() + threadName() + "- " + params[0];
        logQueue.enqueueLogRequest(logImpl, method, params);
    }


    /**
     * A string that represents the time a log message is created
     * (the logger will generate a time stamp that reflects the time
     * that the async logging thread actually writes the log)
     *
     * @return
     */
    private String timeStamp() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        return "[ActualLogTime: " + ts.toString() + "] ";
    }


    /**
     * A string that identifies the thread that created the log message.
     * @return
     */
    private String threadName() {
        return "[ActualLogThread: " + Thread.currentThread().getName() + "] ";
    }

    /**
     * <p> Get log name. </p>
     */
    public String getLogName(){
        return logImpl.getLogName();
    }

    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel) {
        logImpl.setLevel(currentLogLevel);
    }

    /**
     * <p> Get logging level. </p>
     */
    public int getLevel() {
        return logImpl.getLevel();
    }


    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     */
    public boolean isLevelEnabled(int logLevel) {
        return logImpl.isLevelEnabled(logLevel);
    }

    /**
     * <p> Is ignore logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than ignore. </p>
     */
    public boolean isIgnoreEnabled() {
        return logImpl.isIgnoreEnabled();
    }


    /**
     * <p> Is debug logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than debug. </p>
     */
    public boolean isDebugEnabled() {
        return logImpl.isDebugEnabled();
    }


    /**
     * <p> Is error logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than error. </p>
     */
    public boolean isErrorEnabled(){
        return logImpl.isErrorEnabled();
    }


    /**
     * <p> Is fatal logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than fatal. </p>
     */
    public boolean isFatalEnabled(){
        return logImpl.isFatalEnabled();
    }


    /**
     * <p> Is info logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than info. </p>
     */
    public boolean isInfoEnabled(){
        return logImpl.isInfoEnabled();
    }


    /**
     * <p> Is trace logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than trace. </p>
     */
    public boolean isTraceEnabled(){
        return logImpl.isTraceEnabled();
    }


    /**
     * <p> Is warning logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than warn. </p>
     */
    public boolean isWarnEnabled(){
        return logImpl.isWarnEnabled();
    }


    /**
     * <p> Log a message with ignore log level. </p>
     *
     * @param message log this message
     */
    public void ignore(Object message) {
        queue(LogImpl.TRACE_O, new Object[]{"IGNORE- "+message.toString()});
    }


    /**
     * <p> Log an error with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void ignore(Object message, Throwable t) {
        queue(LogImpl.TRACE_O_T,new Object[]{"IGNORE - "+message.toString(),t});
    }


    /**
     * <p> Log a message with ignore log level. </p>
     *
     * @param message log this message
     */
    public void ignore(Object message, SmartFrogException t, TerminationRecord tr) {
        ignore(message, (Throwable)t);
    }


    /**
     * <p> Log an error with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void ignore(Object message, SmartFrogException t) {
        ignore(message, (Throwable)t);
    }


    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     */
    public void trace(Object message) {
        queue(LogImpl.TRACE_O,new Object[]{message});
    }


    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, Throwable t) {
        queue(LogImpl.TRACE_O_T,new Object[]{message,t});
    }


    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     */
    public void trace(Object message, SmartFrogException t, TerminationRecord tr) {
        trace(message, (Throwable)t);
    }


    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, SmartFrogException t) {
        trace(message, (Throwable)t);
    }


    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     */
    public void debug(Object message) {
        queue(LogImpl.DEBUG_O,new Object[]{message});
    }


    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, Throwable t) {
        queue(LogImpl.DEBUG_O_T,new Object[]{message,t});
    }


    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     */
    public void debug(Object message, SmartFrogException t, TerminationRecord tr) {
        debug(message, (Throwable)t);
    }


    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, SmartFrogException t) {
        debug(message, (Throwable)t);
    }


    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     */
    public void info(Object message) {
        queue(LogImpl.INFO_O,new Object[]{message});
    }


    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, Throwable t) {
        queue(LogImpl.INFO_O_T,new Object[]{message,t});
    }


    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     */
    public void info(Object message, SmartFrogException t, TerminationRecord tr) {
        info(message, (Throwable)t);
    }


    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, SmartFrogException t) {
        info(message, (Throwable)t);
    }


    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     */
    public void warn(Object message) {
        queue(LogImpl.WARN_O,new Object[]{message});
    }


    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, Throwable t) {
        queue(LogImpl.WARN_O_T,new Object[]{message,t});
    }


    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     */
    public void warn(Object message, SmartFrogException t, TerminationRecord tr) {
        warn(message, (Throwable)t);
    }


    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, SmartFrogException t) {
        warn(message, (Throwable)t);
    }


    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     */
    public void error(Object message) {
        queue(LogImpl.ERROR_O,new Object[]{message});
    }


    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, Throwable t) {
        queue(LogImpl.ERROR_O_T,new Object[]{message,t});
    }


    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     */
    public void error(Object message, SmartFrogException t, TerminationRecord tr) {
        error(message, (Throwable)t);
    }


    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, SmartFrogException t) {
        error(message, (Throwable)t);
    }


    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     */
    public void fatal(Object message) {
        queue(LogImpl.FATAL_O,new Object[]{message});
    }


    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, Throwable t) {
        queue(LogImpl.FATAL_O_T,new Object[]{message,t});
    }


    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     */
    public void fatal(Object message, SmartFrogException t, TerminationRecord tr) {
        fatal(message, (Throwable)t);
    }


    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, SmartFrogException t) {
        fatal(message, (Throwable)t);
    }


    /**************************************************
     * LogMessage interface                           *
     * These are not allowed yet - fix later          *
     * @TODO: fix LogMessage async implementation     *
     **************************************************/

    /**
     * <p> Log a message with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void out(Object message) {
        return;
    }

    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void err(Object message) {
        return;
    }


    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, Throwable t) {
        return;
    }


    /**
     * <p> Log a message with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void err(Object message, SmartFrogException t, TerminationRecord tr) {
        return;
    }


    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, SmartFrogException t) {
        return;
    }

}
