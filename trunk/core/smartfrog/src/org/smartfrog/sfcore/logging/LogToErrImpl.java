/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
  Cut version of SimpleLog: Using this one for intial tests!
*/

package org.smartfrog.sfcore.logging;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.PrintStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * <p>Simple implementation of Log that sends all enabled log messages,
 * for all defined loggers, to System.err.  The following system properties
 * are supported to configure the behavior of this logger:</p>
 * <ul>
 * <li><code>defaultlog</code> -
 * Default logging detail level for all instances of SimpleLog.
 * Must be one of ("trace", "debug", "info", "warn", "error", or "fatal").
 * If not specified, defaults to "info". </li>
 * <li><code>showlogname</code> -
 * Set to <code>true</code> if you want the Log instance name to be
 * included in output messages. Defaults to <code>false</code>.</li>
 * <li><code>showShortLogname</code> -
 * Set to <code>true</code> if you want the last component of the name to be
 * included in output messages. Defaults to <code>true</code>.</li>
 * <li><code>showdatetime</code> -
 * Set to <code>true</code> if you want the current date and time
 * to be included in output messages. Default is false.</li>
 * </ul>
 */
public class LogToErrImpl implements LogToErr, Log, LogMessage, LogLevel, Serializable {

  //Configuration for LogImpl class
   ComponentDescription classComponentDescription = null;

  /** Include the instance name in the log message? */
  protected boolean showLogName = true;

  /** Include the short name ( last component ) of the logger in the log
      message. Default to true - otherwise we'll be lost in a flood of
      messages without knowing who sends them.
  */
  protected boolean showShortName = false;

  /** Include the current time in the log message */
  protected static boolean showDateTime = true;

  /** Include thread name in the log message */
  protected boolean showThreadName = true;

  /** Include package name in the log message */
  protected boolean showMethodCall = true;

  /** Include package name in the log message */
  protected boolean showStackTrace =true;

  /** Used to format times */
  protected static DateFormat dateFormatter = null;

    // Override with system properties.
    static {
        if(showDateTime) {
            dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");
        }
    }

    /** The name of this simple log instance */
    protected String logName = null;
    /** The current log level */
    protected int currentLogLevel=0;
    /** The short name of this simple log instance */
    private String shortLogName = null;



    /** To get from where this Log was called */
    private static CallDetective detective = CallDetective.Factory.makeCallDetective();

    /** Depth in StackTrace it will depend on how this Log is used and connected to LogImpl */
    int callDepth = 8;

    /** output stream to print to. Bonded at construct time, and usually system.err unless
     * otherwise chosen
     */

    protected PrintStream outstream;

    /**
     * buffer size
     */
    private static final int STACK_BUFFER_SIZE = 1024;

    /**
     * Construct a simple log with given name and log level
     * and log to output level

     */
    protected LogToErrImpl() {
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param intialLogLevel level to log at
     */
    public LogToErrImpl(String name, Integer initialLogLevel) {
       this(name,initialLogLevel,System.err);
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param intialLogLevel level to log at
     * @param out output stream to log to
     */

    public LogToErrImpl(String name, Integer initialLogLevel,PrintStream out) {
        setOutstream(out);
        setLevel(initialLogLevel.intValue());
        //Check Class and read configuration...including system.properties
        try {
          classComponentDescription = LogImpl.getClassComponentDescription(this, true);
        } catch (SmartFrogException ex) {
           this.warn(ex.toString());
        }
        try {
          readSFAttributes();
        } catch (SmartFrogException ex1) {
           this.error("",ex1);
        }
        assert name != null;
        logName = name;
        // Set initial log level
        setLevel(initialLogLevel.intValue());
    }


    /**
     *  Reads optional and mandatory attributes.
     *
     * @exception  SmartFrogException error while reading attributes
     * @exception  RemoteException In case of network/rmi error
     */
    protected void readSFAttributes() throws SmartFrogException {
        if (classComponentDescription==null) return;

        //Optional attributes.
        try {
          showLogName =    classComponentDescription.sfResolve(ATR_SHOW_LOG_NAME,showLogName, false);
          showShortName =  classComponentDescription.sfResolve(ATR_SHOW_SHORT_NAME,showShortName, false);
          showDateTime =   classComponentDescription.sfResolve(ATR_SHOW_DATE_TIME,showDateTime, false);
          showThreadName = classComponentDescription.sfResolve(ATR_SHOW_THREAD_NAME,showThreadName, false);
          showMethodCall = classComponentDescription.sfResolve(ATR_SHOW_METHOD_CALL,showMethodCall, false);
          showStackTrace = classComponentDescription.sfResolve(ATR_SHOW_STACK_TRACE,showStackTrace, false);
        } catch (Exception sex){
           this.warn("",sex);;
        }

        try{
          dateFormatter= new SimpleDateFormat(classComponentDescription.sfResolve
             (ATR_DATE_FORMAT,"yyyy/MM/dd HH:mm:ss:SSS zzz",false));
        } catch (Exception ex){
           this.err("dateFormatter",ex);
        }

    }


    /**
     * set the output stream for logging. must not be null
     * @param outstream
     */
    public void setOutstream(PrintStream outstream) {
        assert(outstream != null);
        this.outstream = outstream;
    }

    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel) {
        this.currentLogLevel = currentLogLevel;
    }


    /**
     * <p> Get logging level. </p>
     */
    public int getLevel() {
        return currentLogLevel;
    }


    /**
     * text for error levels
     */
    protected static final String ERROR_NAMES[]={
        "ALL  ",
        "TRACE",
        "DEBUG",
        "INFO ",
        "WARN ",
        "ERROR",
        "FATAL",
        "NONE "
    };

    // -------------------------------------------------------- Logging Methods


    /**
     * <p> Do the actual logging.
     * This method assembles the message
     * and then calls <code>write()</code> to cause it to be written.</p>
     *
     * @param type One of the LOG_LEVE_XXX constants defining the log level
     * @param message The message itself (typically a String)
     * @param t The exception whose stack trace should be logged
     */
    protected void log(int type, Object message, Throwable t) {
        // Use a string buffer for better performance
        StringBuffer buf = new StringBuffer();

        // Append date-time if so configured
        if(showDateTime) {
            buf.append(dateFormatter.format(new Date()));
            buf.append(" ");
        }

        // Append a readable representation of the log level
        buf.append('[');
        if(type>=0 && type<ERROR_NAMES.length) {
            buf.append(ERROR_NAMES[type]);
        } else {
            //show out of range stuff as an int
            buf.append(type);
        }
        buf.append(']');

        if ((showThreadName)&&(message!=null)) {
            buf.append("["+Thread.currentThread().getName()+"] ");
        }

        // Append the name of the log instance if so configured
        if (showShortName) {
            if (shortLogName==null) {
                // Cut all but the last component of the name for both styles
                shortLogName = logName.substring(logName.lastIndexOf(".")+1);
                shortLogName =
                    shortLogName.substring(shortLogName.lastIndexOf("/")+1);
            }
            buf.append(String.valueOf(shortLogName)).append(" - ");
        } else if (showLogName) {
            buf.append(String.valueOf(logName)).append(" - ");
        }

        // Append the message
        buf.append(String.valueOf(message));

        if ((showMethodCall)&&(message!=null)) {
            buf.append(" ** "+detective.findCaller(callDepth)+" ** ");
        }

//        if ((showPackageName)&&(message!=null)) {
//         // Append the package name
//            String className = null;
//            String classBaseName = null;
//            if (sourceObject instanceof Class) {
//                // for trace from static methods
//                className = ((Class)source).getName();
//            } else {
//                className = source.getClass().getName();
//            }
//
//            int lastDotIndex = className.lastIndexOf('.');
//            if (lastDotIndex!=-1) {
//                thisPackageName = className.substring(0, lastDotIndex);
//                classBaseName = className.substring(lastDotIndex+1,
//                    className.length());
//            }
//        }

// Append stack trace if not null
        if(t != null) {
            buf.append(" <");
            if (t instanceof SmartFrogException)
                 buf.append(((SmartFrogException)t).toString("\n    "));
            else
                 buf.append(t.toString());
            buf.append(">\n        ");
        }

        // Append stack trace if not null
        if(t != null) {
            if (showStackTrace || this.isLevelEnabled(LOG_LEVEL_WARN)) {
                java.io.StringWriter sw = new java.io.StringWriter(STACK_BUFFER_SIZE);
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                t.printStackTrace(pw);
                pw.close();
                buf.append(sw.toString());
            }
        }

        // Print to the appropriate destination
        write(buf);

    }


    /**
     * <p>Write the content of the message accumulated in the specified
     * <code>StringBuffer</code> to the appropriate output destination.
     *
     * this is the output stream specified in the constructor or, by default,
     * the reference to System.err <i>at the time of construction</i>. Changes
     * to System.err are not picked up.
     *
     * @param buffer A <code>StringBuffer</code> containing the accumulated
     *  text to be logged
     */
    protected void write(StringBuffer buffer) {
        outstream.println(buffer.toString());

    }


    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     */
    public boolean isLevelEnabled(int logLevel) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        //System.out.println("Local: Current "+currentLogLevel+", compared "+logLevel);
        return (logLevel >= currentLogLevel);

    }


    // -------------------------------------------------------- Log Implementation


    /**
     * <p> Log a message with debug log level.</p>
     */
    public final void debug(Object message) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_DEBUG)) {
            log(LogLevel.LOG_LEVEL_DEBUG, message, null);
        }
    }


    /**
     * <p> Log an error with debug log level.</p>
     */
    public final void debug(Object message, Throwable t) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_DEBUG)) {
            log(LogLevel.LOG_LEVEL_DEBUG, message, t);
        }
    }


    /**
     * <p> Log a message with trace log level.</p>
     */
    public final void trace(Object message) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_TRACE)) {
            log(LogLevel.LOG_LEVEL_TRACE, message, null);
        }
    }


    /**
     * <p> Log an error with trace log level.</p>
     */
    public final void trace(Object message, Throwable t) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_TRACE)) {
            log(LogLevel.LOG_LEVEL_TRACE, message, t);
        }
    }


    /**
     * <p> Log a message with info log level.</p>
     */
    public final void info(Object message) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_INFO)) {
            log(LogLevel.LOG_LEVEL_INFO,message,null);
        }
    }


    /**
     * <p> Log an error with info log level.</p>
     */
    public final void info(Object message, Throwable t) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_INFO)) {
            log(LogLevel.LOG_LEVEL_INFO, message, t);
        }
    }


    /**
     * <p> Log a message with warn log level.</p>
     */
    public final void warn(Object message) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_WARN)) {
            log(LogLevel.LOG_LEVEL_WARN, message, null);
        }
    }


    /**
     * <p> Log an error with warn log level.</p>
     */
    public final void warn(Object message, Throwable t) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_WARN)) {
            log(LogLevel.LOG_LEVEL_WARN, message, t);
        }
    }


    /**
     * <p> Log a message with error log level.</p>
     */
    public final void error(Object message) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_ERROR)) {
            log(LogLevel.LOG_LEVEL_ERROR, message, null);
        }
    }


    /**
     * <p> Log an error with error log level.</p>
     */
    public final void error(Object message, Throwable t) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_ERROR)) {
            log(LogLevel.LOG_LEVEL_ERROR, message, t);
        }
    }


    /**
     * <p> Log a message with fatal log level.</p>
     */
    public final void fatal(Object message) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_FATAL)) {
            log(LogLevel.LOG_LEVEL_FATAL, message, null);
        }
    }


    /**
     * <p> Log an error with fatal log level.</p>
     */
    public final void fatal(Object message, Throwable t) {
        if (isLevelEnabled(LogLevel.LOG_LEVEL_FATAL)) {
            log(LogLevel.LOG_LEVEL_FATAL, message, t);
        }
    }


    /**
     * <p> Are debug messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isDebugEnabled() {

        return isLevelEnabled(LogLevel.LOG_LEVEL_DEBUG);
    }


    /**
     * <p> Are error messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isErrorEnabled() {

        return isLevelEnabled(LogLevel.LOG_LEVEL_ERROR);
    }


    /**
     * <p> Are fatal messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isFatalEnabled() {

        return isLevelEnabled(LogLevel.LOG_LEVEL_FATAL);
    }


    /**
     * <p> Are info messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isInfoEnabled() {

        return isLevelEnabled(LogLevel.LOG_LEVEL_INFO);
    }

    /**
     * <p> Are trace messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isTraceEnabled() {

        return isLevelEnabled(LogLevel.LOG_LEVEL_TRACE);
    }

    /**
     * <p> Are warn messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isWarnEnabled() {

        return isLevelEnabled(LogLevel.LOG_LEVEL_WARN);
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
          outstream.println(message.toString());
          if (t!=null){
              t.printStackTrace(outstream);
          }
    }

    /**
     * <p> Log a message with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void err(Object message, SmartFrogException t, TerminationRecord tr) {
        err(message, t);
        outstream.println(tr.toString());
    }

    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void err(Object message, SmartFrogException t) {
        err(message, t);
    }

}

