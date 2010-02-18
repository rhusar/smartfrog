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


package org.smartfrog.services.logging.log4j;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogImpl;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.LogMessage;
import org.smartfrog.sfcore.logging.LogUtils;
import org.smartfrog.sfcore.prim.TerminationRecord;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Logger that wraps Log4J.
 */
public class LogToLog4JImpl implements LogToLog4J, Log, LogMessage, LogLevel {

    public static final String[] LOG4J_LEVELS =
            {"ALL", "DEBUG", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"};

    private static final String ATR_CONFIGURE_AND_WATCH = "configureAndWatch";
    /**
     * LogToLog4JImpl configuration description.
     */
    private ComponentDescription classComponentDescription = null;

    /**
     * Log to this logger
     */
    private Logger logger = null;

    /**
     * Logger name
     */
    private String logName = null;

    /**
     * Output stream to print to.
     * Bonded at construct time, and usually system.err unless
     * otherwise chosen
     */
    private PrintStream outstream;

    /**
     * The LogToLog4J class name.
     */
    private static final String clazz = LogImpl.class.getName();
    /**
     * URL for Log4J configuration file
     */
    private Object configuratorURL = null;

    /**
     * Method setLogLevel should ignore any call
     */
    private boolean ignoreSetLogLevel = false;
    /**
     * Should we call setLogLevel during initialization
     */
    private boolean setIniLog4JLoggerLevel = false;

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     */
    protected LogToLog4JImpl() {
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     *
     * @param name            log name
     * @param initialLogLevel level to log at
     * @throws Exception if it cannot be created
     */
    public LogToLog4JImpl(String name, Integer initialLogLevel) throws Exception {
        this(name, null, initialLogLevel, System.out);
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     *
     * @param name                          log name
     * @param componentComponentDescription A component description to overwrite class configuration
     * @param initialLogLevel               level to log at
     * @throws Exception if it cannot be created
     */
    public LogToLog4JImpl(String name, ComponentDescription componentComponentDescription, Integer initialLogLevel)
            throws Exception {
        this(name, componentComponentDescription, initialLogLevel, System.out);

    }


    /**
     * Construct a simple log with given name and log level
     * and log to output level
     *
     * @param name            log name
     * @param initialLogLevel level to log at
     * @param out             output stream to log to
     * @param componentComponentDescription component description to use
     * @throws Exception if something went wrong
     */
    public LogToLog4JImpl(String name, ComponentDescription componentComponentDescription, Integer initialLogLevel,
                          PrintStream out) throws Exception {
        try {

            assert name != null;
            assert initialLogLevel != null;

            setOutstream(out);

            logName = name;
            if (logger == null) {
                logger = Logger.getLogger(logName);
            }
            //Initial configurator to get output in case of failures
            BasicConfigurator.configure();

            if (setIniLog4JLoggerLevel) {
                setLevel(initialLogLevel.intValue());
            }

            //Check Class and read configuration...including system.properties
            String traceMessage="";
            try {
                classComponentDescription = ComponentDescriptionImpl.getClassComponentDescription(this, true, null);
                final String classname = getClass().toString();
                traceMessage = classname + " '" + logName + "' using ComponentDescription:\n"
                        + classComponentDescription.toString();
            } catch (SmartFrogException ex) {
                if (isWarnEnabled()) warn(ex);
                throw ex;
            }

            readSFAttributes(classComponentDescription);

            readSFAttributes(componentComponentDescription);

            configureLog4JLogger(configuratorURL);

            //at this point Log4J exists and its methods can be used.

            // Set initial log level after reading configuration to the lowest one
            // the most verbose of the two
            if (setIniLog4JLoggerLevel) {
                if (getLevel() >= initialLogLevel.intValue()) {
                    setLevel(initialLogLevel.intValue());
                }
            }

            trace(traceMessage);

            if (isInfoEnabled()) {
                info("logger: \"" + logger.getName() + "\" (" + logger.getEffectiveLevel().toString()
                        + ")\nwith configuration " + configuratorURL + "\nsetIniLog4JLoggerLevel: "
                        + setIniLog4JLoggerLevel + ", ignoreSetLogLevel: " + ignoreSetLogLevel);
            }
        } catch (Exception ex1) {
            error("", ex1);
            throw ex1;
        }
    }


    /**
     * Configure the logger
     * @param configurationURL URL or string. to the configuration
     */

    private void configureLog4JLogger(Object configurationURL) {

        if (configurationURL == null) {
            error("LogToLog4JImpl: Failed to find configuration. Using Log4J.BasicConfigurator");
            return;
        }
        try {
            if (configurationURL instanceof URL) {
                configureWithURL((URL) configurationURL);
            } else if (configurationURL instanceof String) {
                configureWithFilename((String) configurationURL);
            } else {
                warn("LogToLog4JImpl: Using Log4J.BasicConfigurator");
            }
        } catch (FactoryConfigurationError ex3) {
            err("", ex3);
        }
    }

    /**
     * bind the logger to a URL
     *
     * @param configurationURL URL of the configuration
     * @throws FactoryConfigurationError if the configuration is invalid
     */
    private void configureWithURL(URL configurationURL) throws FactoryConfigurationError {
        if (configurationURL.getFile().endsWith(".xml")) {
            //Initial configurator is removed
            BasicConfigurator.resetConfiguration();
            DOMConfigurator.configure(configurationURL);
            if (isTraceEnabled()) {
                out("LogToLog4JImpl: Using Log4J.xml.DOMConfigurator with URL " + configurationURL
                        .toString());
            }
        } else {
            //Initial configurator is removed
            BasicConfigurator.resetConfiguration();
            PropertyConfigurator.configure(configurationURL);
            if (isTraceEnabled()) {
                out("LogToLog4JImpl: Using Log4J.PropertyConfigurator with URL " + configurationURL
                        .toString());
            }
        }
    }

    /**
     * configure against a string
     * @param filename filename
     * @throws FactoryConfigurationError
     */
    private void configureWithFilename(String filename) throws FactoryConfigurationError {
        if (filename.endsWith(".xml")) {
            //Initial configurator is removed
            BasicConfigurator.resetConfiguration();
            DOMConfigurator.configure(filename);
            if (isTraceEnabled()) {
                out("LogToLog4JImpl: Using Log4J.xml.DOMConfigurator with " + filename);
            }
        } else {
            //Initial configurator is removed
            BasicConfigurator.resetConfiguration();
            PropertyConfigurator.configure(filename);
            if (isTraceEnabled()) {
                out("LogToLog4JImpl: Using Log4J.PropertyConfigurator with " + filename);
            }
        }
    }

    /**
     * Reads optional and mandatory attributes.
     *
     * @param cd ComponentDescription A component description where to read configuration from
     * @throws SmartFrogException error while reading attributes
     */
    protected void readSFAttributes(ComponentDescription cd) throws SmartFrogException {
        try {
            if (cd == null) {
                return;
            }
            try {
                URL url = null;
                configuratorURL = cd.sfResolve(ATTR_CONFIGURATOR_FILE, url, true);
            } catch (SmartFrogException sex) {
                //if it is not a URL or not present then try againg with a String attribute.
                configuratorURL = cd.sfResolve(ATTR_CONFIGURATOR_FILE, configuratorURL, false);
            }

            //if there resource is set then it is used to provide
            //the source of the configuration data
            String resourceName = null;
            resourceName = cd.sfResolve(ATTR_RESOURCE, resourceName, false);
            if (resourceName != null) {
                //use log4J helper class to load the resource. This
                //guarantees the same logic as for a normal classload
                configuratorURL = Loader.getResource(resourceName);
            }
            /**
             * if sf attritute ATR_CONFIGURATOR_FILE not defined, it will try to use the system.property log4j.configuarion
             */
            if (configuratorURL == null) {
                //Optional attributes.
                configuratorURL = System.getProperty("log4j.configuration");
                if (isTraceEnabled()) {
                    this.trace("ConfigURL (from log4j.configuration sys property): " + configuratorURL);
                }
                try {
                    configuratorURL = new URL((String) configuratorURL);
                    if (isTraceEnabled()) {
                        this.trace("ConfigURL (from URL): " + configuratorURL);
                    }
                } catch (MalformedURLException ignored) {
                    //ignore
                    //ex.printStackTrace();
                }
            }

            ignoreSetLogLevel = (cd.sfResolve(ATTR_IGNORE_SET_LOG_LEVEL, ignoreSetLogLevel, false));
            setIniLog4JLoggerLevel = (cd.sfResolve(ATTR_SET_INI_LOG4J_LOGGER_LEVEL, setIniLog4JLoggerLevel, false));

            try {
                //true so that if it is present we can issue a warning. configureAndWatch not supported in Log4J new versions.
                boolean configureAndWatch = false;

                configureAndWatch = cd.sfResolve(ATR_CONFIGURE_AND_WATCH, configureAndWatch, true);
                if (isWarnEnabled()) {
                    warn(ATR_CONFIGURE_AND_WATCH + " not supported any more");
                }
                //        configureAndWatchDelay = (long)delay;
            } catch (SmartFrogResolutionException ignored) {
                //ignore. This attribute should not be there.
            }

        } catch (Exception ex1) {
            throw (SmartFrogLogException) SmartFrogLogException.forward(ex1);
        }
    }

    /**
     * set the output stream for logging.
     *
     * @param outstream output stream - must not be null
     */
    public void setOutstream(PrintStream outstream) {
        assert (outstream != null);
        this.outstream = outstream;
    }

    /**
     * Set the logging level>
     * It only does the change when it is different
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel) {
        //Only do the change when it is different
        if (ignoreSetLogLevel) return;
        int level = getLevel();
        if (level != currentLogLevel) {
            //Level TRACE and DEBUG are the same in Log4J
            if (!(((currentLogLevel == 1) || (currentLogLevel == 2)) && ((level == 1) || (level == 2)))) {
                logger.setLevel(Level.toLevel(LOG4J_LEVELS[currentLogLevel]));
            }
        }
    }

    /**
     * Get the logging level.
     */
    public int getLevel() {
        Level levelLog4J = logger.getEffectiveLevel();
        if (levelLog4J == null) {
            return 0; //ALL
        }
        int i = 0;
        int level = 0;
        while (i < LOG4J_LEVELS.length) {
            if (LOG4J_LEVELS[i].equals(levelLog4J.toString())) {
                level = i;
                break;
            }
            i++;
        }
        return level;
    }

// -------------------------------------------------------- Log Implementation

    protected void log(Level level, Object message, Throwable t) {
        if (logger == null) {
            logger = Logger.getLogger(logName);
        }
        logger.log(clazz, level, message, t);
    }

    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     */
    public boolean isLevelEnabled(int logLevel) {
// log level are numerically ordered so can use simple numeric
// comparison
        return (logLevel >= getLevel());

    }

// --------------------------------------------------END -- Log Implementation


    /**
     * <p> Log a message with debug log level.</p>
     */
    public final void debug(Object message) {
        log(Level.DEBUG, message, null);
    }

    /**
     * <p> Log an error with debug log level.</p>
     */
    public final void debug(Object message, Throwable t) {
        log(Level.DEBUG, message, t);
    }

    /**
     * <p> Log a message with trace log level.</p>
     */
    public final void trace(Object message) {
        log(Level.DEBUG, message, null);
    }

    /**
     * <p> Log an error with trace log level.</p>
     */
    public final void trace(Object message, Throwable t) {
        log(Level.DEBUG, message, t);
    }

    /**
     * <p> Log a message with info log level.</p>
     */
    public final void info(Object message) {
        log(Level.INFO, message, null);
    }

    /**
     * <p> Log an error with info log level.</p>
     */
    public final void info(Object message, Throwable t) {
        log(Level.INFO, message, t);
    }

    /**
     * <p> Log a message with warn log level.</p>
     */
    public final void warn(Object message) {
        log(Level.WARN, message, null);
    }

    /**
     * <p> Log an error with warn log level.</p>
     */
    public final void warn(Object message, Throwable t) {
        log(Level.WARN, message, t);
    }

    /**
     * <p> Log a message with error log level.</p>
     */
    public final void error(Object message) {
        log(Level.ERROR, message, null);
    }

    /**
     * <p> Log an error with error log level.</p>
     */
    public final void error(Object message, Throwable t) {
        log(Level.ERROR, message, t);
    }

    /**
     * <p> Log a message with fatal log level.</p>
     */
    public final void fatal(Object message) {
        log(Level.FATAL, message, null);
    }

    /**
     * <p> Log an error with fatal log level.</p>
     */
    public final void fatal(Object message, Throwable t) {
        log(Level.FATAL, message, t);
    }

    /**
     * <p> Are debug messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * <p> Are error messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isErrorEnabled() {

        return logger.isEnabledFor(Level.ERROR);
    }

    /**
     * <p> Are fatal messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isFatalEnabled() {

        return logger.isEnabledFor(Level.FATAL);
    }

    /**
     * <p> Are info messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * <p> Are trace messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isTraceEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * <p> Are warn messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
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
        this.info(message);
    }

    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     */
    public void err(Object message) {
        err(message, null);
    }

    /**
     * <p> Log an error with message log level. </p>
     * <p> Same as info messages but without Labels.</p>
     *
     * @param message log this message
     * @param t       log this cause
     */
    public void err(Object message, Throwable t) {
        outstream.println(message.toString());
        this.error(message, t);
        if (t != null) {
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
        err(message, LogUtils.extractCause(t, tr));
        outstream.println(LogUtils.stringify(tr));
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
