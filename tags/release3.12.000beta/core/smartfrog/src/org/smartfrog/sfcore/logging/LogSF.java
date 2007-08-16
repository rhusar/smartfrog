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

/**
 * A simple logging interface abstracting logging APIs based in Apache Jakarta
 * logging.
 *
 */
public interface LogSF extends Log, LogMessage, LogLevel {

    /** SFAttributes*/
    /** String name for optional attribute "debug". */
    final static String ATR_LOG_LEVEL = "logLevel";
    /** String name for optional attribute "debug". */
    final static String ATR_LOGGER_CLASS = "loggerClass";

    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel);

    /**
     * Get logging level.
     @return the logging level
     */
    public int getLevel();


    /**
     * Get log name.
     * @return the log name
     */
    public String getLogName();


    /**
     * <p> Is ignore logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than ignore. </p>
     * @return boolean true if ignore level is currently enabled
     */
    public boolean isIgnoreEnabled();


    /**
     * <p> Log a message with ignore log level. </p>
     *
     * @param message log this message
     */
    public void ignore(Object message);


    /**
     * <p> Log an error with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void ignore(Object message, Throwable t);


    /**
     * <p> Log a message with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     *
     */
    public void ignore(Object message, SmartFrogException t, TerminationRecord tr);


    /**
     * <p> Log an error with ignore log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void ignore(Object message, SmartFrogException t);


    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     *
     */
    public void trace(Object message, SmartFrogException t, TerminationRecord tr);


    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, SmartFrogException t);


    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     *
     */
    public void debug(Object message, SmartFrogException t, TerminationRecord tr);


    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, SmartFrogException t);


    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     *
     */
    public void info(Object message, SmartFrogException t, TerminationRecord tr);


    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, SmartFrogException t);


    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     *
     */
    public void warn(Object message, SmartFrogException t, TerminationRecord tr);


    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, SmartFrogException t);


    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     *
     */
    public void error(Object message, SmartFrogException t, TerminationRecord tr);


    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, SmartFrogException t);


    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     *
     */
    public void fatal(Object message, SmartFrogException t, TerminationRecord tr);


    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, SmartFrogException t);

}


