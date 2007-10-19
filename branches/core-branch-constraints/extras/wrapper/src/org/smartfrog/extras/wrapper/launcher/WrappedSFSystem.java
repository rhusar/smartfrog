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

package org.smartfrog.extras.wrapper.launcher;

import org.smartfrog.SFSystem;
import org.smartfrog.extras.wrapper.WrappedEntryPoint;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;

/**
 * This is a wrapped entry point to the SFSystem runtime. The core differences
 * are <ol> <li>we run in a separate thread <li>Instead of calling
 * system.exit(), we throw an {@link ExitException} <li>That gets caught and
 * saved as the exit code </ol>
 *
 * @since 01-Oct-2004
 */
public class WrappedSFSystem extends SFSystem implements Runnable,
        WrappedEntryPoint {

    private String args[];

    private Thread thread;

    private int exitCode;

    private boolean systemExitOnRootProcessTermination = true;
    private static final int EXPECTED_SHUTDOWN_TIME = 60;

    public WrappedSFSystem() {
    }

    public WrappedSFSystem(String[] args) {
        this.args = args;
    }

    /**
     * give the expected time to shut down the system.
     *
     * @return
     * @todo make this configurable, somehow.
     */
    public int getExpectedShutdownTime() {
        return EXPECTED_SHUTDOWN_TIME;
    }

    /**
     * start the system
     */
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * stop smartfrog
     */
    public void stop() {
        ProcessCompound rootProcess = super.getRootProcess();
        terminateSystem("wrapper initiated shutdown");
    }

    /**
     * this is a brute force operation, not how you are encouraged to shut
     * things down
     */
    public void emergencyStop() {
        thread.stop();
    }

    /**
     * get the thread
     *
     * @return
     */
    public Thread getThread() {
        return thread;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public boolean isSystemExitOnRootProcessTermination() {
        return systemExitOnRootProcessTermination;
    }

    public void setSystemExitOnRootProcessTermination(
            boolean systemExitOnRootProcessTermination) {
        this.systemExitOnRootProcessTermination =
                systemExitOnRootProcessTermination;
    }

    /**
     * entry point for a new thread
     *
     * @see Thread#run()
     */
    public void run() {
        try {
            execute(args);
        } catch (ExitException e) {
            exitCode = e.getExitCode();
        }
    }

    //ignore an exception
    private void ignore(Throwable t) {

    }


    /**
     * create a root process with no termination.
     * TODO: fix this with the now static createRootProcess
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     */
    protected ProcessCompound createRootProcessBroken() throws SmartFrogException,
            RemoteException {
        ProcessCompound rootProcess = SFProcess.deployProcessCompound(false);
        rootProcess.systemExitOnTermination(systemExitOnRootProcessTermination);
        return rootProcess;
    }

    /**
     * Block till stopping has finished.
     *
     * @param seconds timeout;
     * @return true if we were successful
     */
    public boolean waitTillStopped(long seconds) {
        if (thread == null) {
            //implicitly halted
            return true;
        }
        try {
            if (seconds == 0) {
                thread.join();
            } else {
                thread.join(seconds * 1000);
            }
            return thread.isAlive();
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * Exits from the system. This implementation throws an runtime exception
     * for a container to catch
     *
     * @throws ExitException with the supplied error code.
     */
    protected void exit(int code) {
        throw new ExitException(code);
    }


    /**
     * Exception thrown we want to stop
     */
    private static class ExitException extends RuntimeException {

        /**
         * Constructs a new runtime exception with <code>null</code> as its
         * detail message.  The cause is not initialized, and may subsequently
         * be initialized by a call to {@link #initCause}.
         *
         * @param exitCode our exit code.
         */
        public ExitException(int exitCode) {
            this.exitCode = exitCode;
        }

        /**
         * the exit code
         */
        private int exitCode = 0;

        /**
         * get the exit code
         *
         * @return
         */
        public int getExitCode() {
            return exitCode;
        }
    }
}
