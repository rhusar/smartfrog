/** (C) Copyright Hewlett-Packard Development Company, LP

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

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

/**
 * Implementation for SFLogStdStream component.
 */
public class SFLogStdStreamImpl extends PrimImpl implements Prim, SFLogStdStream {



    /**
     * This class redirect InputStream to logTo.info or logTo.err (outputStream).
     */
    public class StreamGobbler extends SmartFrogThread {
        private InputStream is;
        private boolean out = true;

        /**
         * Constructs StreamGobbler with the input stream and type of stream
         *
         * @param is Imput stream to gobble
         * @param typeS Type of the stream
         */
        public StreamGobbler(InputStream is, String typeS) {
            setName("StreamGobbler("+typeS+")");
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
                       Thread.sleep(500);
                   }
               } catch (InterruptedException ex) {
               }

                while ((line = br.readLine()) != null) {
                    if (out) {
                        if (logStdOutTag!=null){
                           ((LogImpl)sfLog()).invoke(methodOut,new Object[]{"["+logStdOutTag+"]"+line});
                        } else {
                            ((LogImpl)sfLog()).invoke(methodOut,new Object[]{line});
                        }
                    } else {
                        if (logStdErrTag!=null){
                            ((LogImpl)sfLog()).invoke(methodErr,new Object[]{"["+logStdErrTag+"]"+line});
                        } else {
                            ((LogImpl)sfLog()).invoke(methodErr,new Object[]{line});
                        }
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }



    boolean logStdErr = true;
    String logStdErrTag = null;
    String logStdErrLevel = "ERROR";
    boolean logStdOut = true;
    String logStdOutTag = null;
    String logStdOutLevel = "ERROR";

    PrintStream originalOut = null; //OutputStream
    PrintStream originalErr = null;

    public Method methodOut = null;
    public Method methodErr = null;

    /**
     *  Constructor for the Ant object.
     *
     *@exception  RemoteException In case of network/rmi error
     */
    public SFLogStdStreamImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *
     * @exception  SmartFrogException In case of error in deploying
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
       super.sfDeploy();
       readConfiguration();
       redirectOutputs();
    }

    /**
     * Redirects system outputs to a file.
     *
     */
    public void redirectOutputs(){

        try {
            StreamGobbler outputGobbler = null;
            StreamGobbler errorGobbler = null;
            if (logStdOut) {
                if (sfLog().isDebugEnabled()) sfLog().debug("Redirecting stdout to "+logStdOutLevel+" log.");
                PipedInputStream pipeReaderOut = new PipedInputStream();
                /** Direct brand new outs to log.info*/
                PipedOutputStream pipeWriterOut = new PipedOutputStream(pipeReaderOut);
                // writerOut -> pipedWriter(outStream)--->pipedReader (InputStream)
                PrintStream writerOut = new PrintStream(pipeWriterOut);
                try {
                    originalOut = System.out;
                    System.setOut(writerOut);
                    outputGobbler = new StreamGobbler(pipeReaderOut, "out");
                    // kick them off
                    outputGobbler.setName(sfCompleteNameSafe()+".outputGobbler");
                    outputGobbler.start();
                    if (sfLog().isDebugEnabled()) System.out.println("stdOut redirect working");
                } catch (Exception e) {
                    if (originalOut !=null) System.setOut(originalOut);
                    if (sfLog().isErrorEnabled()) sfLog().error(e);
                }
            }

            if (logStdErr) {
                if (sfLog().isDebugEnabled()) sfLog().debug("Redirecting stderr to "+logStdErrLevel+" log.");
                PipedInputStream pipeReaderErr = new PipedInputStream();
                PipedOutputStream pipeWriterErr = new PipedOutputStream(pipeReaderErr);
                /** Direct brand new err to log.err*/
                PrintStream writerErr = new PrintStream(pipeWriterErr);
                try {
                    originalErr = System.err;
                    System.setErr(writerErr);
                    errorGobbler = new StreamGobbler(pipeReaderErr, "err");
                    // kick them off
                    errorGobbler.setName(sfCompleteNameSafe()+".errorGobbler");
                    errorGobbler.start();
                    if (sfLog().isDebugEnabled()) System.err.println("stdErr redirect working");
                } catch (Exception e) {
                    if (originalErr !=null) System.setErr(originalErr);
                    if (sfLog().isErrorEnabled()) sfLog().error(e);
                }
            }
        } catch (IOException ex) {
            if (originalOut !=null) System.setOut(originalOut);
            if (originalErr !=null) System.setErr(originalErr);
            if (sfLog().isErrorEnabled()) sfLog().error(ex);
        }
    }

//    /**
//     *
//     * @exception  SmartFrogException In case of error while starting
//     * @exception  RemoteException In case of network/rmi error
//     */
//    public synchronized void sfStart() throws SmartFrogException, RemoteException {
//        super.sfStart();
//    }

    /**
     * @param  t TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        if (sfLog().isDebugEnabled()) sfLog().debug(" Terminating for reason: " + t.toString());
        if (originalOut !=null) System.setOut(originalOut);
        if (originalErr !=null) System.setErr(originalErr);
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods

    // Read Attributes from description
    /**
     *  Reads optional and mandatory attributes.
     *
     * @exception  SmartFrogException error while reading attributes
     * @exception  RemoteException In case of network/rmi error
     */
    protected void readConfiguration() throws SmartFrogException, RemoteException {
        //
        // Mandatory attributes. //True to Get exception thown!
        try {
            logStdOut = sfResolve(ATR_LOG_STD_OUT, logStdOut, false);
            logStdOutTag = sfResolve(ATR_LOG_STD_OUT_TAG, logStdOutTag, false);
            logStdOutLevel = sfResolve(ATR_LOG_STD_OUT_LEVEL, logStdOutLevel, false);
            logStdErr = sfResolve(ATR_LOG_STD_ERR, logStdErr, false);
            logStdErrTag = sfResolve(ATR_LOG_STD_ERR_TAG, logStdErrTag, false);
            logStdErrLevel = sfResolve(ATR_LOG_STD_ERR_LEVEL, logStdErrLevel, false);

        } catch (SmartFrogResolutionException e) {
            if (sfLog().isErrorEnabled()) sfLog().error(e);
            throw e;
        }

        methodOut = LogImpl.getObjectMethod (logStdOutLevel, new Class[] {Object.class});
        methodErr = LogImpl.getObjectMethod (logStdErrLevel, new Class[] {Object.class});
    }

    // Main component action methods

}
