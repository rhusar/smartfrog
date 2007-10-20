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

package org.smartfrog.test;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.OptionSet;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

import java.rmi.ConnectException;
import java.rmi.RemoteException;

/**
 * A test daemon runs smartfrog. When the object is disposed/finalised, it
 * disposes of the instance of smartfrog. Always use the dispose() method, as the
 * finalize() method is not guaranteed to run.
 *
 * The object is not intended to be used across threads, though it is <i>probably</i>
 * thread safe. Core methods are synchronized.
 *
 * @author steve loughran
 */

public class LocalTestDaemon {

    /**
     * Bind to an existing process. The process will be terminated
     * when this object is disposed or finalized.
     * @param process
     */
    public LocalTestDaemon(ProcessCompound process) {
        this.process = process;
    }

    /**
     * parse the command line and start a test daemon with those arguments
     * @param args standard smartfrog command line arguments
     * @throws java.lang.Exception
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     * @throws java.rmi.RemoteException
     * @throws java.rmi.ConnectException
     */
    public LocalTestDaemon(String[] args)
            throws Exception, SmartFrogException, RemoteException, ConnectException {
        OptionSet optionset = new OptionSet(args);
        start(optionset);
    }

    /**
     * start a daemon with the given options
     * @param options a set of options for the daemon
     * @throws java.lang.Exception
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     * @throws java.rmi.RemoteException
     * @throws java.rmi.ConnectException
     */
    public LocalTestDaemon(OptionSet options)
            throws Exception, SmartFrogException, RemoteException, ConnectException {
        start(options);
    }

    /**
     * empty constructor
     */
    public LocalTestDaemon() {
    }

    /**
     * emergency shutdown in case the process was
     * not properly disposed of
     */
    protected void finalize() throws Throwable {
        if(terminateOnCleanup) {
            dispose();
        }
    }

    private SFSystem system;

    /**
     * the process we are running
     */
    private ProcessCompound process;

    /**
     * cache of options
     */
    private OptionSet options;

    /**
     * flag set to tell the system to terminate when sent an exit command.
     * This is a bad thing, as the current runtime kills the entire JVM/
     */
    private boolean terminateOnCleanup;

    /**
     * bind to an existing process.
     * Any process that we already were bound to is not terminated;
     * @param compound
     */
    public synchronized void bind(ProcessCompound compound) {
        this.process=compound;
    }

    /**
     * detach from the process
     */
    public synchronized void detach() {
        this.process=null;
        this.system=null;
    }

    /**
     * start a daemon. After this point the daemon is running
     * @param optionset
     * @throws Exception if a daemon is already running.
     * @throws SmartFrogException
     * @throws RemoteException network trouble
     * @throws ConnectException
     */
    public synchronized void start(OptionSet optionset)
            throws Exception, SmartFrogException, RemoteException, ConnectException {
        if(isRunning()) {
            throw new Exception("Already running a daemon here");
        }
        this.options=optionset;
        system=new SFSystem();
        process=system.runSmartFrog(options.cfgDescriptors);
    }

    /**
     * Stop running the local daemon (but only if the terminateOnCleanup flag is set)
     * equivalent of java org.smartfrog.SFSystem -h1 $1 -t rootProcess -e
     */
    public synchronized void dispose() throws RemoteException {
        //catch already done
        if(!isRunning() ) {
            return;
        }
        system.terminateSystem("test complete");
        detach();
    }

    /**
     * test for the daemon running.
     * @return true iff there is a process reference inside this object.
     */
    public synchronized boolean isRunning() {
        return process!=null;
    }


    /**
     * query terminate flag
     * @return the current state
     */
    public boolean getTerminateOnCleanup() {
        return terminateOnCleanup;
    }

    /**
     * set terminate flag
     * @param terminateOnCleanup
     */
    public void setTerminateOnCleanup(boolean terminateOnCleanup) {
        this.terminateOnCleanup = terminateOnCleanup;
    }
}
