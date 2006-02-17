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

import java.io.PrintStream;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import java.rmi.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.common.Logger;


/**
 *
 *  Logs log info into a Prim that implements Log interface
 *
 */

public class LogToPrimImpl extends LogToStreamsImpl implements LogToPrim {


        /**
         * This class redirect InputStream to logTo.info or logTo.err (outputStream).
         */
        public class StreamGobbler extends Thread {
            InputStream is;
            boolean out = true;

            /**
             * Constructs StreamGobbler with the input stream and type of stream
             *
             * @param is Imput stream to gobble
             * @param typeS Type of the stream
             */
            public StreamGobbler(InputStream is, String typeS) {
                this.setName("StreamGobbler("+typeS+")");
                this.is = is;

                if (typeS.equals("err")) {
                    this.out = false;
                }
            }

            /**
             * Reads an inputStream and shows the content in the System.out.
             * Overrides Thread.run.
             */
            public void run() {
                try {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                   try {
                       while (!br.ready()) {
                           this.sleep(500);
                       }
                   } catch (InterruptedException ex) {
                   }

                    while ((line = br.readLine()) != null) {
                        if (out) {
                            if (logTo()!=null){
                                (logTo()).info("[stdout]"+line);
                            } else {
                                System.out.println(line);
                            }
                        } else {
                            if (logTo()!=null){
                                (logTo()).error("[stderr]"+line);
                            } else {
                                System.err.println(line);
                            }
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

   //Configuration parameters

    /** Prim component that implements Log. */
    LogRemote logTo = null;
    /** Config attribute for LogTo */
    Object logToAttribute = null;

    /** Redirect system.out and system.err */
    boolean redirectSystemOutputs = false;
    boolean init =false;

    /** Add local log information to the message? */
    boolean tagMessage = false;

    private boolean debug = false;


    /**
     * Construct a simple log with given name and log level
     * and log to output level
     * @param name log name
     * @param initialLogLevel level to log at
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
     */
    public LogToPrimImpl (String name, ComponentDescription componentComponentDescription, Integer initialLogLevel) throws SmartFrogException {
        super(name,initialLogLevel);

        readSFPrimAttributes(classComponentDescription);

        readSFPrimAttributes(componentComponentDescription);

        String logToName = "unknowLogToName";

        if (logToAttribute == null) {
           throw new SmartFrogResolutionException( "LogTo component for logging not found!");
        }

        if (redirectSystemOutputs){
            try {
                redirectOutputs();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }
//        setLevel(initialLogLevel.intValue());
    }


    /**
     *  Reads optional and mandatory attributes.
     *
     * @exception  SmartFrogException error while reading attributes
     */
    protected void readSFPrimAttributes(ComponentDescription cd) throws SmartFrogResolutionException {
        if (cd==null) return;
        //Optional attributes.
        try {
          logToAttribute = (cd.sfResolve(ATR_LOG_TO, false));
          redirectSystemOutputs = cd.sfResolve(ATR_REDIRECT_SYSTEM_OUTPUTS,redirectSystemOutputs, false);
          tagMessage = cd.sfResolve(ATR_TAG_MESSAGE, tagMessage, false);
          debug = cd.sfResolve(ATR_DEBUG,debug,false);
        } catch (SmartFrogResolutionException ex){
           //this.warn(ex);
           throw ex;
        }
    }

    private LogRemote logTo()  {

        if (logTo!=null) return logTo;
        if (!(Logger.initialized())) return null;
        try {
            try {
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
     * Redirects system outputs to a file.
     * @throws Exception if any io error
     */
    public void redirectOutputs() throws Exception {

        PrintStream originalOut = System.out; //OutputStream
        PrintStream originalErr = System.err;

       // writerOut -> pipedWriter(outStream)--->pipedReader (InputStream)
       PipedInputStream pipeReaderOut = new PipedInputStream();
       PipedOutputStream pipeWriter = new PipedOutputStream(pipeReaderOut);
        /** Direct brand new outs to log.info*/
       PrintStream writerOut = new PrintStream(pipeWriter);
              // writerOut -> pipedWriter(outStream)--->pipedReader (InputStream)
       PipedInputStream pipeReaderErr = new PipedInputStream();
       PipedOutputStream pipeWriterErr = new PipedOutputStream(pipeReaderErr);
        /** Direct brand new err to log.err*/
       PrintStream writerErr = new PrintStream(pipeWriterErr);

        try {
            System.setErr(writerErr);
            StreamGobbler errorGobbler = new StreamGobbler(pipeReaderErr,"err");
            System.setOut(writerOut);
            StreamGobbler outputGobbler = new StreamGobbler(pipeReaderOut,"out");
            // kick them off
            errorGobbler.setName(logName+".errorGobbler");
            outputGobbler.setName(logName+".outputGobbler");
            errorGobbler.start();
            outputGobbler.start();
        } catch (Exception e) {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    /**
     * <p> Log a message with debug log level.</p>
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
