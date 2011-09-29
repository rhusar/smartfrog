/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.shellscript;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

public class SFProcessExecutionImpl extends PrimImpl implements Prim, SFProcessExecution, SFReadConfig {

    private String name = null;
    /**
     * Host component should start process during deploy phase
     */
    private boolean autoStart = true;
    /**
     * Exec data
     */
    private Cmd cmd = new Cmd();

    /**
     * Process Exec component
     */
    private volatile RunProcess runProcess = null;

    // For Prim

    public SFProcessExecutionImpl() throws RemoteException {

    }

    //Component and LiveCycle methods

    /**
     * Reads SF description = initial configuration. Override this to read/set properties before we read ours, but
     * remember to call the superclass afterwards
     */
    @Override
    public void readConfig() throws SmartFrogException, RemoteException {
        this.autoStart = sfResolve(ATR_AUTO_START, autoStart, false);
        this.name = sfResolve(ATR_NAME, name, false);
        if (name == null) {
            name = sfCompleteNameSafe().toString();
        }
        this.cmd = new Cmd(sfResolve(ATR_EXEC, new ComponentDescriptionImpl(null, null, false), true));
    }

    /**
     * This method retrieves the parameters from the .sf file. For the purposes of a demo default parameters could be hard
     * coded.
     *
     * @throws SmartFrogException deployment failure
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        readConfig();
        // RunProcessImpl
        if (autoStart) {
            runProcess = new RunProcessImpl(name, cmd, this);
            ((RunProcessImpl) runProcess).start();
            runProcess.waitForReady(200);
            if (sfLog().isInfoEnabled()) {
                sfLog().info("Process started");
            }
        }
    }


    /**
     * This shuts down Apache by requesting that the ApacheState variable be set to false.
     *
     * @param tr TerminationRecord object
     */
    @Override
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("Terminating.", null, tr);
            }
            kill();
        } catch (Exception ex) {
        }
        super.sfTerminateWith(tr);
    }

    /**
     * Gets the input stream of the subprocess. The stream obtains data piped from the standard output stream of the
     * process (<code>Process</code>) object. <p> Implementation note: It is a good idea for the input stream to be
     * buffered.
     *
     * @return the input stream connected to the normal output of the subprocess.
     */

    @Override
    public InputStream getStdOutStream() {
        if (runProcess != null) {
            return runProcess.getInputStream();
        } else {
            return null;
        }
    }

    /**
     * Gets the error stream of the subprocess. The stream obtains data piped from the error output stream of the process
     * (<code>Process</code>) object. <p> Implementation note: It is a good idea for the input stream to be buffered.
     *
     * @return the input stream connected to the error stream of the subprocess.
     */
    @Override
    public InputStream getStdErrStream() {
        if (runProcess != null) {
            return runProcess.getErrorStream();
        } else {
            return null;
        }
    }

    /**
     * Gets the output stream of the subprocess. Output to the stream is piped into the standard input stream of the
     * process (<code>Process</code>) object. <p> Implementation note: It is a good idea for the output stream to be
     * buffered.
     *
     * @return the output stream connected to the normal input of the subprocess.
     */
    @Override
    public OutputStream getStdInpStream() {
        if (runProcess != null) {
            return runProcess.getOutputStream();
        } else {
            return null;
        }
    }

    /**
     * Kill the process
     */
    @Override
    public synchronized void kill() {
        if (runProcess == null) {
            return;
        } else {
            runProcess.kill();
            runProcess = null;
        }
    }

    /**
     * Restarts the process
     */
    @Override
    public void restart() throws SmartFrogException {
        kill();
        try {
            readConfig();
        } catch (RemoteException rex) {
            throw SmartFrogException.forward("Problem during restart ", rex);
        }
        // RunProcessImpl
        runProcess = new RunProcessImpl(name, cmd);
        runProcess.waitForReady(200);
        sfLog().info("Restart done");

    }

    /**
     * Is the process ready?
     *
     * @return boolean
     */
    public boolean isRunning() {
        RunProcess process = runProcess;
        return process != null && process.ready();
    }

}
