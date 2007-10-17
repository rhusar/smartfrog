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
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.PrintStream;


/**
 * <p>Only out and err output are printed. </p>
 */
public class LogToNothingImpl implements LogToNothing, Log, LogMessage, LogLevel  {

    /** Configuration for class  */
    protected ComponentDescription classComponentDescription = null;
    /** Configuration for component */
    protected ComponentDescription componentComponentDescription = null;

    /** The name of this simple log instance */
    protected String logName = null;

    /** output stream to print to. Bonded at construct time, and usually system.err unless
     * otherwise chosen
     */
    protected PrintStream outstream;

    /** error stream to print to. Bonded at construct time, and usually system.err unless
     * otherwise chosen
     */

    protected PrintStream errstream;

    /** Send error output to normal ouput - used to simplify collecting all output */
    protected boolean errToOut = false;

    /**
     * Construct a simple log with given name and log level
     * and log to output level

     */
    protected LogToNothingImpl() {
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param initialLogLevel level to log at
     */
    public LogToNothingImpl(String name, Integer initialLogLevel) {
       this(name, initialLogLevel, System.out, System.err);
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param componentComponentDescription A component description to overwrite class configuration
     * @param initialLogLevel level to log at.
     */
    public LogToNothingImpl(String name,ComponentDescription componentComponentDescription, Integer initialLogLevel) {
        this(name, componentComponentDescription, initialLogLevel, System.out, System.err);
    }
    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param initialLogLevel level to log at.
     * @param out output stream to log to
     * @param err error stream to log to
     */

    public LogToNothingImpl(String name, Integer initialLogLevel, PrintStream out, PrintStream err) {
        this (name,null, initialLogLevel, out, err);
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param componentComponentDescription A component description to overwrite class configuration
     * @param initialLogLevel level to log at.
     * @param out output stream to log to
     * @param err error stream to log to
     */

    public LogToNothingImpl(String name,ComponentDescription componentComponentDescription, Integer initialLogLevel, PrintStream out, PrintStream err) {
        assert name != null;
        logName = name;
        setOutstream(out);
        setErrstream(err);
        try {
          classComponentDescription = ComponentDescriptionImpl.getClassComponentDescription(this, true, null);
        } catch (SmartFrogException ex) {
           this.warn(ex.toString());
        }
        try {
          readSFNothingAttributes(classComponentDescription);
        } catch (SmartFrogException ex1) {
           this.error("",ex1);
        }
        try {
          readSFNothingAttributes(componentComponentDescription);
        } catch (SmartFrogException ex1) {
           this.error("",ex1);
        }
        if (errToOut) setErrstream(outstream);
        if (isDebugEnabled() && this.getClass().toString().endsWith("LogToNothingImpl")) {
                    //This will go to the std output only if system.out is not redirected
                    out("[DEBUG] Log using LogToNothing.");
        }
    }

    /**
     * Reads optional and mandatory attributes.
     * @param cd cd ComponentDescription A component description to read attributes from
     * @throws  SmartFrogException error while reading attributes
     */
    protected void readSFNothingAttributes(ComponentDescription cd) throws SmartFrogException {
        //Optional attributes.
        if (cd==null) return;
        try {
          errToOut = cd.sfResolve(ATR_ERR_TO_OUT, errToOut, false);
        } catch (Exception sex){
           this.warn("",sex);
        }
    }


    /**
     * set the output stream for logging. must not be null
     * @param outstream Output stream to set
     */
    public void setOutstream(PrintStream outstream) {
        assert(outstream != null);
        this.outstream = outstream;
    }

    /**
     * set the output stream for logging. must not be null
     * @param errstream Error stream to set
     */
    public void setErrstream(PrintStream errstream) {
        assert(errstream != null);
        this.errstream = errstream;
    }


    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel) {
        return;
    }


    /**
     * <p> Get logging level. </p>
     * @return int log level
     */
    public int getLevel() {
        return LOG_LEVEL_OFF;
    }


    // -------------------------------------------------------- Logging Methods

    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     * @return boolean true if given log level is currently enabled
     */
    public boolean isLevelEnabled(int logLevel) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        return false;
    }


    // -------------------------------------------------------- Log Implementation


    /**
     * <p> Log a message with debug log level.</p>
     * @param message log this message
     */
    public void debug(Object message) {
        return;
    }


    /**
     * <p> Log an error with debug log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, Throwable t) {
        return;
    }


    /**
     * <p> Log a message with trace log level.</p>
     * @param message log this message
     */
    public void trace(Object message) {
        return;
    }


    /**
     * <p> Log an error with trace log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, Throwable t) {
        return;
    }


    /**
     * <p> Log a message with info log level.</p>
     * @param message log this message
     */
    public void info(Object message) {
        return;
    }


    /**
     * <p> Log an error with info log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, Throwable t) {
        return;
    }


    /**
     * <p> Log a message with warn log level.</p>
     * @param message log this message
     */
    public void warn(Object message) {
        return;
    }


    /**
     * <p> Log an error with warn log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, Throwable t) {
        return;
    }


    /**
     * <p> Log a message with error log level.</p>
     * @param message log this message
     */
    public void error(Object message) {
        return;
    }


    /**
     * <p> Log an error with error log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, Throwable t) {
        return;
    }


    /**
     * <p> Log a message with fatal log level.</p>
     * @param message log this message
     */
    public void fatal(Object message) {
        return;
    }


    /**
     * <p> Log an error with fatal log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, Throwable t) {
        return;
    }


    /**
     * <p> Are debug messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if debug level is currently enabled
     */
    public boolean isDebugEnabled() {
        return false;
    }


    /**
     * <p> Are error messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if error level is currently enabled
     */
    public boolean isErrorEnabled() {
        return false;
    }


    /**
     * <p> Are fatal messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if fatal level is currently enabled
     */
    public boolean isFatalEnabled() {
        return false;
    }


    /**
     * <p> Are info messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if info level is currently enabled
     */
    public boolean isInfoEnabled() {
        return false;
    }

    /**
     * <p> Are trace messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if trace level is currently enabled
     */
    public boolean isTraceEnabled() {
        return false;
    }

    /**
     * <p> Are warn messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if warn level is currently enabled
     */
    public boolean isWarnEnabled() {
        return false;
    }



    // Special LogMessages interface to produce output.
    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void out(Object message) {
         outstream.println(message.toString());
    }


    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void err(Object message) {
          err(message,null);
    }


    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, Throwable t) {
          errstream.println(message.toString());
          if (t!=null){
              t.printStackTrace(errstream);
          }
    }

    /**
     * <p> Log a message with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     * @param tr log this TerminationRecord
     */
    public void err(Object message, SmartFrogException t, TerminationRecord tr) {
        err(message, LogUtils.extractCause(t, tr));
        errstream.println(LogUtils.stringify(tr));
    }

    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, SmartFrogException t) {
        err(message, (Throwable)t);
    }
}

