package org.smartfrog.services.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * SmartFrog component to executes a command on a remote machine via ssh. It is a wrapper around jsch
 *
 * Super class for SSH component implementaion for user/password and public/private key authentication mechanisms.
 *
 * @author Ritu Sabharwal
 * @see <a href="http://www.jcraft.com/jsch/">jsch</a>
 */
public class SSHExecImpl extends AbstractSSHComponent implements SSHExec {

    private FileOutputStream fout = null;
    protected Vector commandsList;
    private SmartFrogThread waitThread = null;
    protected String logFile = null;

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
     * @throws SmartFrogException in case of error in deploying or reading the attributes
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
     * @throws SmartFrogException in case of error while connecting to remote host or executing commands
     * @throws RemoteException in case of network/emi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {

        super.sfStart();
        ChannelShell channel = null;
        try {
            // open ssh session
            logDebugMsg("Getting SSH Session");
            Session newsession = openSession();
            setSession(newsession);

            if (logFile != null) {
                fout = new FileOutputStream(logFile, false);
            }
            // Execute commands

            StringBuffer buffer = new StringBuffer();
            for (Object aCommandsList : commandsList) {
                String cmd = aCommandsList.toString();
                buffer.append(cmd);
                buffer.append("\n");
            }

            byte[] bytes = buffer.toString().getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            channel = (ChannelShell) getSession().openChannel("shell");
            channel.setOutputStream(fout);
            channel.setExtOutputStream(fout);
            channel.setInputStream(bais);
            channel.connect();

            log.info("Executing commands:" + buffer.toString());

            // wait for it to finish
            waitThread = new WaitForEndOfChannel(channel);

            waitThread.start();
            waitThread.join(timeout);

            if (waitThread.isAlive()) {
                // ran out of time
                waitThread = null;
                if (failOnError) {
                    throw new SmartFrogLifecycleException(TIMEOUT_MESSAGE);
                } else {
                    log.error(TIMEOUT_MESSAGE);
                }
            } else {
                int exitStat = channel.getExitStatus();
                if (exitStat != 0) {
                    String msg = "Remote commands: " +
                            " failed with exit status " + exitStat;
                    if (failOnError) {
                        throw new SmartFrogException(msg);
                    }
                }
            }
            waitThread = null;

            // check if it should terminate by itself
            log.info("Normal termination :" + sfCompleteNameSafe());
            TerminationRecord termR = TerminationRecord.normal(
                    "SSH Session to "+getConnectionDetails()+" finished: ",
                    sfCompleteName());
            new ComponentHelper(this).targetForWorkflowTermination(termR);
        } catch (SmartFrogException sfe) {
            throw sfe;
        } catch (JSchException e) {
            log.error("When connecting to " + getConnectionDetails(),e);
            if (e.getMessage().indexOf(SESSION_IS_DOWN) >= 0) {
                String message = TIMEOUT_MESSAGE + getConnectionDetails();
                if (getFailOnError()) {
                    throw new SmartFrogLifecycleException(message, e);
                }
            } else {
                if (getFailOnError()) {
                    throw new SmartFrogLifecycleException(e);
                }
            }
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace(e);
            }
            throw SmartFrogLifecycleException.forward("When connecting to " + getConnectionDetails(),e);
        } finally {
            //clean up time
            if(channel!=null) {
                channel.disconnect();
            } else {
                //if there's no channel, we may not have closed the output stream
                FileSystem.close(fout);
            }
        }
    }


    /**
     * Reads SmartFrog attributes.
     *
     * @throws SmartFrogException if failed to read any attribute or a mandatory
     * attribute is not defined.
     * @throws RemoteException in case of network/rmi error
     */
    protected void readSFAttributes() throws SmartFrogException, RemoteException {
        // Mandatory attributes
        commandsList = sfResolve(SSHExec.ATTR_COMMANDS, commandsList, true);

        //optional attributes
        logFile = sfResolve(SSHExec.ATTR_LOG_FILE, logFile, false);
    }


    /**
     * Thread to wait for the end of the channel.
     */
    private class WaitForEndOfChannel extends SmartFrogThread {
        private final ChannelShell channel;

        private WaitForEndOfChannel(ChannelShell channel) {
            this.channel = channel;
        }

        @Override
        public void execute() throws Throwable {
            while (!channel.isEOF()) {
                if (waitThread == null) {
                    return;
                }
                sleep(500);
            }
        }
    }
}
