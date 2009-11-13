/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

//some of this code looks just like the apache Ant sshexec task
//unless/until it gets reworked, they need credit too.
/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.smartfrog.services.ssh;

import com.jcraft.jsch.ChannelShell;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.OutputStreamLog;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * SmartFrog component to executes a command on a remote machine via ssh. It is a wrapper around jsch <p/> Super class
 * for SSH component implementaion for user/password and public/private key authentication mechanisms.
 *
 * @author Ritu Sabharwal
 * @see <a href="http://www.jcraft.com/jsch/">jsch</a>
 */
public class SSHExecImpl extends AbstractSSHComponent implements SSHExec {

    private Vector<String> commandsList;
    private File logFile = null;
    private int exitCodeMax, exitCodeMin;
    private CommandExecutor executorThread;
    private static final int THREAD_SHUTDOWN_TIME = 1000;

    /**
     * Create an instance
     *
     * @throws RemoteException if the parent does
     */
    public SSHExecImpl() throws RemoteException {
    }

    /**
     * Connects to remote host over SSH and executes commands.
     *
     * @throws SmartFrogException in case of error while connecting to remote host or executing commands
     * @throws RemoteException    in case of network/emi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {

        super.sfStart();
        readSFAttributes();
        executorThread = new CommandExecutor();
        executorThread.start();
    }

    /**
     * {@inheritDoc}
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        SmartFrogThread.ping(executorThread);
    }

    /**
     * Terminate any worker thread during shutdown
     *
     * @param tr Termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        shutdownExecutor();
        //this will close the session, so we hope that the thread has finished.
        super.sfTerminateWith(tr);
    }

    private void shutdownExecutor() {
        SSHExecImpl.CommandExecutor worker = executorThread;
        if (worker != null) {
            worker.haltCommand();
            SmartFrogThread.requestAndWaitForThreadTermination(worker,
                                                               THREAD_SHUTDOWN_TIME);
        }
    }

    /**
     * Reads SmartFrog attributes.
     *
     * @throws SmartFrogException if failed to read any attribute or a mandatory attribute is not defined.
     * @throws RemoteException    in case of network/rmi error
     */
    protected void readSFAttributes()
            throws SmartFrogException, RemoteException {
        // Mandatory attributes
        commandsList = ListUtils.resolveStringList(this,
                                                   new Reference(ATTR_COMMANDS),
                                                   true);

        //optional attributes
        logFile = FileSystem.lookupAbsoluteFile(this,
                                                ATTR_LOG_FILE,
                                                logFile,
                                                null,
                                                false,
                                                null);

        exitCodeMax = sfResolve(ATTR_EXIT_CODE_MAX, exitCodeMax, true);
        exitCodeMin = sfResolve(ATTR_EXIT_CODE_MIN, exitCodeMin, true);
    }


    /** This thread executes commands down an SSH channel */
    private class CommandExecutor extends WorkflowThread {

        private volatile SshCommand command;

        CommandExecutor() {
            super(SSHExecImpl.this, true);
        }

        /** Halt the command if non null */
        void haltCommand() {
            SshCommand cmd = command;
            if (cmd != null) {
                cmd.haltOperation();
            }
        }

        @SuppressWarnings({"ProhibitedExceptionDeclared"})
        @Override
        public void execute() throws Throwable {
            OutputStream outputStream;
            String sessionInfo = "SSH Session to " + getConnectionDetails();
            if (logFile != null) {
                try {
                    outputStream = new FileOutputStream(logFile, false);
                } catch (FileNotFoundException e) {
                    throw new SmartFrogException(
                            sessionInfo + " failed to create log file "
                            + logFile,
                            e);
                }
            } else {
                outputStream = new OutputStreamLog(log, LogLevel.LOG_LEVEL_INFO);
            }
            try {
                //start the logging
                // open ssh session
                logDebugMsg(sessionInfo);
                openSession();

                command = new SshCommand(sfLog(), null);
                int exitCode = command.execute(getSession(), commandsList, outputStream, getTimeout());


                if (exitCode < exitCodeMin || exitCode > exitCodeMax) {
                    String msg = sessionInfo
                                 + " failed with exit status " + exitCode
                                 + " out of the range ["
                                 + exitCodeMin
                                 + ','
                                 + exitCodeMax + ']';
                    throw new SmartFrogException(msg);
                }

            } finally {
                command = null;
            }
        }
    }
}
