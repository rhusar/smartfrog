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
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.OutputStreamLog;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * SmartFrog component to executes a command on a remote machine via ssh. It is
 * a wrapper around jsch
 * <p/>
 * Super class for SSH component implementaion for user/password and
 * public/private key authentication mechanisms.
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
     * Reads SmartFrog attributes and deploys SSHExecImpl component.
     *
     * @throws SmartFrogException in case of error in deploying or reading the
     * attributes
     * @throws RemoteException in case of network/emi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        readSFAttributes();
    }


    /**
     * Connects to remote host over SSH and executes commands.
     *
     * @throws SmartFrogException in case of error while connecting to remote
     * host or executing commands
     * @throws RemoteException in case of network/emi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {

        super.sfStart();
        executorThread = new CommandExecutor();
        executorThread.start();
    }

    /**
     * {@inheritDoc}
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness}
     * interface
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
        SmartFrogThread.requestAndWaitForThreadTermination(executorThread,
                THREAD_SHUTDOWN_TIME);
        //this will close the session, so we hope that the thread has finished.
        super.sfTerminateWith(tr);
    }

    /**
     * Reads SmartFrog attributes.
     *
     * @throws SmartFrogException if failed to read any attribute or a mandatory
     * attribute is not defined.
     * @throws RemoteException in case of network/rmi error
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


    /**
     * This thread executes commands down an SSH channel
     */
    private class CommandExecutor extends SmartFrogThread {

        private static final int SPIN_DELAY_MILLIS = 500;


        @Override
        public void execute() throws Throwable {
            ChannelShell channel = null;
            OutputStream outputStream=null;
            ComponentHelper helper = new ComponentHelper(SSHExecImpl.this);
            String sessionInfo = "SSH Session to " + getConnectionDetails();
            try {
                //start the logging
                if (logFile != null) {
                    try {
                        outputStream = new FileOutputStream(logFile, false);
                    } catch (FileNotFoundException e) {
                        throw new SmartFrogLifecycleException(
                                sessionInfo + " failed to create log file "
                                        + logFile,
                                e);
                    }
                } else {
                    outputStream=new OutputStreamLog(log, LogLevel.LOG_LEVEL_INFO);
                }
                // open ssh session
                logDebugMsg(sessionInfo);
                Session newsession = openSession();

                // Execute commands

                StringBuilder buffer = new StringBuilder();
                for (Object aCommandsList : commandsList) {
                    String cmd = aCommandsList.toString();
                    buffer.append(cmd);
                    buffer.append('\n');
                }

                byte[] bytes = buffer.toString().getBytes();
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

                channel = (ChannelShell) getSession().openChannel("shell");
                channel.setOutputStream(outputStream);
                channel.setExtOutputStream(outputStream);
                channel.setInputStream(bais);
                channel.connect();

                log.info("Executing commands:" + buffer.toString());

                // wait for it to finish. This is pretty ugly
                long timeLimit=System.currentTimeMillis()+timeout;
                while (!channel.isEOF() && !isTerminationRequested()) {
                    long now= System.currentTimeMillis();
                    if(timeout>0 && now>timeLimit) {
                        //we have a timeout
                        String message = TIMEOUT_MESSAGE + getConnectionDetails();
                        log.error(message);
                        throw new SmartFrogLifecycleException(message);
                    }
                    sleep(SPIN_DELAY_MILLIS);
                }

                if(isTerminationRequested()) {
                    //we've been asked to die
                    return;
                }

                int exitCode = channel.getExitStatus();
                if (exitCode < exitCodeMin || exitCode > exitCodeMax) {
                    String msg = sessionInfo
                            + " failed with exit status " + exitCode
                            + " out of the range ["
                            + exitCodeMin
                            + ','
                            + exitCodeMax + ']';
                    throw new SmartFrogLifecycleException(msg);
                }

                // check if it should terminate by itself
                log.info("Normal termination :" + sfCompleteNameSafe());
                TerminationRecord termR = TerminationRecord.normal(
                        sessionInfo + " finished: ",
                        sfCompleteName());
                helper.sfSelfDetachAndOrTerminate(termR);
            } catch (Throwable ex) {
                SmartFrogLifecycleException lifecycleException = forward(ex);
                log.error(sessionInfo, lifecycleException);
                TerminationRecord tr = helper.createTerminationRecord(null,
                        sessionInfo,sfCompleteName(), lifecycleException);
                helper.targetForWorkflowTermination(tr);
                throw lifecycleException;
            } finally {
                //clean up time
                if (channel != null) {
                    channel.disconnect();
                } else {
                    //if there's no channel, we may not have closed the output stream
                    FileSystem.close(outputStream);
                }
            }
        }
    }
}
