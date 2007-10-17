/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.common.Logger;


/**
 *
 *  Logs log info into a Prim that implements Log interface
 *
 */

public class LogToPrimImpl extends LogToStreamsImpl implements LogToPrim {


   //Configuration parameters

    /** Prim component that implements Log. */
    LogRemote logTo = null;
    /** Config attribute for LogTo */
    Object logToAttribute = null;

    boolean init =false;

    /** Add local log information to the message? */
    boolean tagMessage = false;

    private boolean debug = false;


    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param initialLogLevel level to log at
     * @throws SmartFrogException if failed to construct the log
     */
    public LogToPrimImpl (String name, Integer initialLogLevel) throws SmartFrogException{
        this (name,null,initialLogLevel);
    }

    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param componentComponentDescription A component description to overwrite class configuration
     * @param initialLogLevel level to log at
     * @throws SmartFrogException if failed to construct the log
     */
    public LogToPrimImpl (String name, ComponentDescription componentComponentDescription, Integer initialLogLevel) throws SmartFrogException {
        super(name,initialLogLevel);

        readSFPrimAttributes(classComponentDescription);

        readSFPrimAttributes(componentComponentDescription);

        String logToName = "unknownLogToName";

        if (logToAttribute == null) {
           throw new SmartFrogResolutionException( "LogTo component for logging not found!");
        }

//        setLevel(initialLogLevel.intValue());
    }

    /**
     * Get the destination log
     * @return the destination for logging messages.
     */
    public LogRemote getLogTo() {
        return logTo;
    }

    /**
     * Set the destination log
     * @param logTo destination log to set
     */
    public void setLogTo(LogRemote logTo) {
        this.logTo = logTo;
    }

    /**
     *  Reads optional and mandatory attributes.
     * @param cd cd ComponentDescription A component description to read attributes from
     * @throws SmartFrogResolutionException error while reading attributes
     */
    protected void readSFPrimAttributes(ComponentDescription cd) throws SmartFrogResolutionException {
        if (cd==null) return;
        //Optional attributes.
        try {
          logToAttribute = (cd.sfResolve(ATR_LOG_TO, false));
          tagMessage = cd.sfResolve(ATR_TAG_MESSAGE, tagMessage, false);
          debug = cd.sfResolve(ATR_DEBUG,debug,false);
        } catch (SmartFrogResolutionException ex){
           //this.warn(ex);
           throw ex;
        }
    }

    /**
     * Set the log
     * @return log
     */
    private LogRemote logTo()  {

        if (logTo!=null) return logTo;
        if (!(Logger.initialized())) return null;
        try {
            try {
                //This class needs to use special debug flag because is can be used as a logger for the log system an be
                // used during the boot strap of the logging when the daemon is started.
                if (debug) System.out.println("Trying logToAttribute - "+logToAttribute);
                Prim logToPrim = null;
                ProcessCompound pc = org.smartfrog.sfcore.processcompound.SFProcess.getProcessCompound();
                if (debug) System.out.println("PC - "+pc);
                if (pc==null) return null;
                if (debug) System.out.println("logToAttribute.toString() - "+logToAttribute);
                Object found = pc.sfResolveWithParser(logToAttribute.toString());
                init=true;
                if (debug) System.out.println("Got for logging object: "+ found );
                if (found==null) return null;
                if (debug) System.out.println("Got for logging object class: "+ found.getClass() );
                logTo = (LogRemote)(found);
                if (debug) System.out.println("   Finally using for logging logTo: "+ logTo);
            } catch (Exception re) {
                //throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(re);
                re.printStackTrace();
                return null;
            }
            if (!(logTo instanceof LogRemote)||!(logTo instanceof Prim)) {
                throw new SmartFrogResolutionException("Found wrong component for logging: "+((Prim)logTo).sfCompleteName().toString()+", "+ logTo.getClass().getName());
            }
            if (isDebugEnabled()&& this.getClass().toString().endsWith("LogToPrimImpl")) {
                //This will go to the std output.
                debug("LogToPrimImpl using component: "+((Prim)logTo).sfCompleteName().toString()+", "+ logTo.getClass().getName());
            }
            return logTo;
        } catch (Exception ex) {
            if (debug){
                System.err.println("Error in LogToPrimImpl.logTo(): "+ ex.toString());
                //if (Logger.logStackTrace) ex.printStackTrace();
                ex.printStackTrace();
            }
        }
         return null;
    }


    /**
     * <p> Log a message with debug log level.</p>
     * @param message log this message
     */
    public void debug(Object message) {
        try {
            if (logTo()==null) {
                return;
            }
            logTo().debug(message);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log an error with debug log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, Throwable t) {
        try {
            if ((!init)||(logTo()==null)) { return; }
            logTo().debug(message, t);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log a message with trace log level.</p>
     * @param message log this message
     */
    public void trace(Object message) {
        try {
            if ((!init)||(logTo()==null)) { return; }
            Throwable t = null;
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_TRACE, message, t);
            }
            logTo().trace(message);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log an error with trace log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, Throwable t) {
        try {
            if ((!init)||(logTo()==null)) { return; }
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_TRACE, message, t);
            }
            logTo().trace(message, t);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log a message with info log level.</p>
     * @param message log this message
     */
    public void info(Object message) {
        try {
            if ((!init)||(logTo()==null)) {
                return;
            }
            Throwable t = null;
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_INFO, message, t);
            }
            logTo().info(message);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log an error with info log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, Throwable t) {
        try {
            if ((!init)||(logTo()==null)) { return; }
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_INFO, message, t);
            }
            logTo().info(message, t);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log a message with warn log level.</p>
     * @param message log this message
     */
    public void warn(Object message) {
        try {
            if ((!init)||(logTo()==null)) { return;}
            Throwable t = null;
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_WARN, message, t);
            }
            logTo().warn(message);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log an error with warn log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, Throwable t) {
        try {
            if ((!init)||(logTo()==null)) { return; }
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_WARN, message, t);
            }
            logTo.warn(message, t);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log a message with error log level.</p>
     * @param message log this message
     */
    public void error(Object message) {
        try {
            if ((!init)||(logTo()==null)) { return;}
            Throwable t = null;
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_ERROR, message, t);
            }
            logTo().error(message);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log an error with error log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, Throwable t) {
        try {
            if ((!init)||(logTo()==null)) { return; }
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_ERROR, message, t);
            }
            logTo().error(message, t);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log a message with fatal log level.</p>
     * @param message log this message
     */
    public void fatal(Object message) {
        try {
            if ((!init)||(logTo()==null)) { return; }
            Throwable t = null;
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_FATAL, message, t);
            }
            logTo().fatal(message);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Log an error with fatal log level.</p>
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, Throwable t) {
        try {
            if ((!init)||(logTo()==null)) { return; }
            if (tagMessage) {
                message = logToText(LogLevel.LOG_LEVEL_FATAL, message, t);
            }
            logTo().fatal(message, t);
        } catch (RemoteException ex) {
        }
    }

    /**
     * <p> Are debug messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if debug level is currently enabled
     */
    public boolean isDebugEnabled() {
        try {
            if ((!init)||(logTo()==null)) { return false; }
            return logTo().isDebugEnabled();
        } catch (RemoteException ex) {
            return false;
        }
    }

    /**
     * <p> Are error messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if error level is currently enabled
     */
    public boolean isErrorEnabled() {
        try {
            if ((!init)||(logTo()==null)) { return false; }
            return logTo().isErrorEnabled();
        } catch (RemoteException ex) {
            return false;
        }
    }

    /**
     * <p> Are fatal messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if fatal level is currently enabled
     */
    public boolean isFatalEnabled() {
        try {
            if ((!init)||(logTo()==null)) { return false; }
            return logTo().isFatalEnabled();
        } catch (RemoteException ex) {
            return false;
        }
    }


    /**
     * <p> Are info messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if info level is currently enabled
     */
    public boolean isInfoEnabled() {
        try {
            if ((!init)||(logTo()==null)) { return false; }
            return logTo().isInfoEnabled();
        } catch (RemoteException ex) {
            return false;
        }
    }

    /**
     * <p> Are trace messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if trace level is currently enabled
     */
    public boolean isTraceEnabled() {
        try {
            if ((!init)||(logTo()==null)) { return false; }
            return logTo().isTraceEnabled();
        } catch (RemoteException ex) {
            return false;
        }
    }

    /**
     * <p> Are warn messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     * @return boolean true if warn level is currently enabled
     */
    public boolean isWarnEnabled() {
        try {
            if ((!init)||(logTo()==null)) { return false; }
            return logTo().isWarnEnabled();
        } catch (RemoteException ex) {
            return false;
        }
    }

}
