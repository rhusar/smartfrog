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

                    while ((line = br.readLine()) != null) {
                        if (out) {
                            if (logTo!=null){
                                ((Log)logTo).info("[stdout]"+line);
                            } else {
                                System.out.println(line);
                            }
                        } else {
                            if (logTo!=null){
                                ((Log)logTo).error("[stderr]"+line);
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
    Log logTo = null;

    /** Redirect system.out and system.err */
    boolean redirectSystemOutputs = false;

    /** Add local log information to the message? */
    boolean tagMessage = false;

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

        if (logTo == null) {
           throw new SmartFrogResolutionException( "LogTo component for logging not found!");
        }

        if (!(logTo instanceof Log)||(logTo instanceof Prim) ) {
           throw new SmartFrogResolutionException( "Found wrong component for logging: " + logToName + ", " + logTo.getClass().getName());
        }

        try {
            logToName = ((Prim)logTo).sfCompleteName().toString();
        } catch (RemoteException ex) {
            throw new SmartFrogException (ex);
        }

        if (isDebugEnabled() && this.getClass().toString().endsWith("LogToPrimImpl")) {
            //This will go to the std output.
            debug("LogToPrimImpl using component: "+logToName + ", " + logTo.getClass().getName());
        }


        if (redirectSystemOutputs){
            try {
                redirectOutputs();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }
        if (isTraceEnabled() && this.getClass().toString().endsWith("LogToPrimImpl")) {
            String msg2 = "Log '"+name+"' using '"+logToName+"'"+
            "\nusing Class ComponentDescription:\n{"+classComponentDescription+
            "}\n, and using Component ComponentDescription:\n{"+ componentComponentDescription+"}";
            trace(this.getClass().toString() + " "+msg2);
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
          Prim logToPrim = null;
          logTo = (Log)(cd.sfResolve(ATR_LOG_TO, logToPrim, false));
          redirectSystemOutputs = cd.sfResolve(ATR_REDIRECT_SYSTEM_OUTPUTS,redirectSystemOutputs, false);
          tagMessage = cd.sfResolve(ATR_TAG_MESSAGE, tagMessage, false);
        } catch (SmartFrogResolutionException ex){
           this.warn(ex);
           throw ex;
        }
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
            StreamGobbler errorGobbler = new StreamGobbler(pipeReaderErr,"err");
            System.setErr(writerErr);
            StreamGobbler outputGobbler = new StreamGobbler(pipeReaderOut,"out");
            System.setOut(writerOut);
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
        logTo.debug(message);
    }

    /**
     * <p> Log an error with debug log level.</p>
     */
    public void debug(Object message, Throwable t) {
        logTo.debug(message,t);
    }

    /**
     * <p> Log a message with trace log level.</p>
     */
    public void trace(Object message) {
        logTo.trace(message);
    }

    /**
     * <p> Log an error with trace log level.</p>
     */
    public void trace(Object message, Throwable t) {
        logTo.trace(message,t);
    }

    /**
     * <p> Log a message with info log level.</p>
     */
    public void info(Object message) {
        logTo.info(message);
    }

    /**
     * <p> Log an error with info log level.</p>
     */
    public void info(Object message, Throwable t) {
        logTo.info(message,t);
    }

    /**
     * <p> Log a message with warn log level.</p>
     */
    public void warn(Object message) {
        logTo.warn(message);
    }

    /**
     * <p> Log an error with warn log level.</p>
     */
    public void warn(Object message, Throwable t) {
        logTo.warn(message,t);
    }

    /**
     * <p> Log a message with error log level.</p>
     */
    public void error(Object message) {
        logTo.error(message);
    }

    /**
     * <p> Log an error with error log level.</p>
     */
    public void error(Object message, Throwable t) {
        logTo.error(message,t);
    }

    /**
     * <p> Log a message with fatal log level.</p>
     */
    public void fatal(Object message) {
        logTo.fatal(message);
    }

    /**
     * <p> Log an error with fatal log level.</p>
     */
    public void fatal(Object message, Throwable t) {
        logTo.fatal(message,t);
    }

    /**
     * <p> Are debug messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public boolean isDebugEnabled() {
        return logTo.isDebugEnabled();
    }

    /**
     * <p> Are error messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public boolean isErrorEnabled() {
        return logTo.isErrorEnabled();
    }

    /**
     * <p> Are fatal messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public boolean isFatalEnabled() {
        return logTo.isFatalEnabled();
    }


    /**
     * <p> Are info messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public boolean isInfoEnabled() {
        return logTo.isInfoEnabled();
    }

    /**
     * <p> Are trace messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public boolean isTraceEnabled() {
        return logTo.isTraceEnabled();
    }

    /**
     * <p> Are warn messages currently enabled? </p>
     * <p/>
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public boolean isWarnEnabled() {
        return logTo.isWarnEnabled();
    }

}
