/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This is something to do smartfrog logging for classes, that is, other than
 * Prim objects. Its current pass just bridges to commons logging; it is here to
 * make switching to something else easier created 06-Jun-2005 14:10:48
 */

public class ClassLogger implements org.smartfrog.sfcore.logging.Log {

    /**
     * Get a log for a class
     *
     * @param clazz
     * @return a log for a class
     */
    public static org.smartfrog.sfcore.logging.Log getLog(Class clazz) {
        return new ClassLogger(LogFactory.getLog(clazz));
    }

    /**
     * get a log by name
     *
     * @param logname name of the log
     * @returna log
     */
    public static org.smartfrog.sfcore.logging.Log getLog(String logname) {
        return new ClassLogger(LogFactory.getLog(logname));
    }

    /**
     * get the class of an instance, and return it
     *
     * @param instance (must not be null)
     * @return a new log
     */
    public static org.smartfrog.sfcore.logging.Log getLog(Object instance) {
        return new ClassLogger(LogFactory.getLog(instance.getClass()));
    }

    private ClassLogger(Log commonsLog) {
        this.commonsLog = commonsLog;
    }

    private Log commonsLog;

    /**
     * <p> Is debug logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation) when the log level is
     * more than debug. </p>
     */
    public boolean isDebugEnabled() {
        return commonsLog.isDebugEnabled();
    }

    /**
     * <p> Is error logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation) when the log level is
     * more than error. </p>
     */
    public boolean isErrorEnabled() {
        return commonsLog.isErrorEnabled();
    }

    /**
     * <p> Is fatal logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation) when the log level is
     * more than fatal. </p>
     */
    public boolean isFatalEnabled() {
        return commonsLog.isFatalEnabled();
    }

    /**
     * <p> Is info logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation) when the log level is
     * more than info. </p>
     */
    public boolean isInfoEnabled() {
        return commonsLog.isInfoEnabled();
    }

    /**
     * <p> Is trace logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation) when the log level is
     * more than trace. </p>
     */
    public boolean isTraceEnabled() {
        return commonsLog.isTraceEnabled();
    }

    /**
     * <p> Is warning logging currently enabled? </p>
     * <p/>
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation) when the log level is
     * more than warn. </p>
     */
    public boolean isWarnEnabled() {
        return commonsLog.isWarnEnabled();
    }

    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     */
    public void trace(Object message) {
        commonsLog.trace(message);
    }

    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void trace(Object message, Throwable t) {
        commonsLog.trace(message, t);

    }

    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     */
    public void debug(Object message) {
        commonsLog.debug(message);

    }

    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void debug(Object message, Throwable t) {
        commonsLog.debug(message, t);
    }

    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     */
    public void info(Object message) {
        commonsLog.info(message);
    }

    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void info(Object message, Throwable t) {
        commonsLog.info(message, t);
    }

    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     */
    public void warn(Object message) {
        commonsLog.warn(message);
    }

    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void warn(Object message, Throwable t) {
        commonsLog.warn(message, t);
    }

    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     */
    public void error(Object message) {
        commonsLog.error(message);
    }

    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void error(Object message, Throwable t) {
        commonsLog.error(message, t);
    }

    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     */
    public void fatal(Object message) {
        commonsLog.fatal(message);
    }

    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void fatal(Object message, Throwable t) {
        commonsLog.fatal(message, t);
    }
}
