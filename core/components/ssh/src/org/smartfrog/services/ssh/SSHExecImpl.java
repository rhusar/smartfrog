package org.smartfrog.services.ssh;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import java.rmi.RemoteException;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.UserInfo;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * SmartFrog component to executes a command on a remote machine via ssh.
 * It is a wrapper around jsch-0.1.33
 * 
 * Super class for SSH component implementaion for user/password and public/private key authentication mechanisms.
 *
 * 
 * @author Ritu Sabharwal
 *         see http://www.jcraft.com/jsch/
 */
public abstract class SSHExecImpl extends PrimImpl implements SSHExec {
    private static final String TIMEOUT_MESSAGE = "Connection timed out";
    private static final int SSH_PORT = 22;

    protected long timeout = 0;
    protected String host;
    protected int port = SSH_PORT;
    protected String logFile = null;
    private FileOutputStream fout = null;
    protected boolean failOnError = true;
    protected UserInfoImpl userInfo;
    protected Vector commandsList;
    protected boolean shouldTerminate = true;
    private Thread waitThread = null;
    protected Session session = null;
    protected Log log;
    protected String userName;
    protected boolean trustAllCerts = true;

    public SSHExecImpl() throws RemoteException {
        super();
    }

     /**
     * Reads SmartFrog attributes and deploys SSHExecImpl component.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException in case of error in deploying or reading the
     *                            attributes
     * @throws java.rmi.RemoteException    in case of network/emi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
                                               RemoteException {
        super.sfDeploy();
        log = sfGetApplicationLog();
        assert log != null;
        readSFAttributes();
        userInfo = new UserInfoImpl(trustAllCerts);
        userInfo.setName(userName);
        log.info("User Name: " + userName);
    }
    
    /**
     * Connects to remote host over SSH and executes commands.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  in case of error while connecting to remote
     *                                  host or executing commands
     * @throws java.rmi.RemoteException in case of network/emi error
     */
   public synchronized void sfStart() throws SmartFrogException, 
                                                          RemoteException {
                                                                      
        super.sfStart();
        try {
            // open ssh session
            logDebugMsg("Getting SSH Session");
            session = openSession();
            session.setTimeout((int) timeout);

	    if (logFile != null) 
            	fout = new FileOutputStream(logFile, false);
            // Execute commands
	    
	    StringBuffer buffer = new StringBuffer();	
           	for (int i = 0 ; i <commandsList.size() ; i++ ) {
	    		String cmd = (String) commandsList.get(i);
	    		buffer.append(cmd);
	    		buffer.append("\n");
	   	}
		
            byte[] bytes = buffer.toString().getBytes();
	    ByteArrayInputStream bais = new ByteArrayInputStream(bytes); 
	    
	    final ChannelShell channel = (ChannelShell) session.openChannel("shell");
            channel.setOutputStream(fout);
            channel.setExtOutputStream(fout);	
	    channel.setInputStream(bais);	
            channel.connect(); 
	    
	    log.info("Executing commands:"+ buffer.toString()); 
            
		// wait for it to finish
               waitThread = new Thread() {
                            public void run() {
                                while (!channel.isEOF()) {
                                    if (waitThread == null) {
                                        return;
                                    }
                                    try {
                                        sleep(500);
                                    } catch (Exception e) {
                                        ignore(e);
                                    }
                                }
                            }
                };

                waitThread.start();
                waitThread.join(timeout);

                if (waitThread.isAlive()) {
                    // ran out of time
                    waitThread = null;
                    if (failOnError) {
                        throw new SmartFrogException(TIMEOUT_MESSAGE);
                    } else {
                        log.error(TIMEOUT_MESSAGE);
                    }
                } else {
                    int exitStat = channel.getExitStatus();
                    if (exitStat != 0) {
                        String msg = "Remote commands: "+ 
                                        " failed with exit status " + exitStat;
                        if (failOnError) {
                            throw new SmartFrogException(msg);
                        }
                    }
                }
                waitThread = null;
	    channel.disconnect();

            // check if it should terminate by itself
            if(shouldTerminate) {
                log.info("Normal termination :" + sfCompleteNameSafe());
                TerminationRecord termR = new TerminationRecord("normal",
                "SSH Session finished: ",sfCompleteName());
                TerminatorThread terminator = new TerminatorThread(this,termR);
                terminator.start();
            }
        }catch (SmartFrogException sfe){
            throw sfe;
        }catch (JSchException e) {
            if (e.getMessage().indexOf("session is down") >= 0) {
                if (getFailOnError()) {
                    throw new SmartFrogLifecycleException(TIMEOUT_MESSAGE, e);
                } else {
                    log.error(TIMEOUT_MESSAGE);
                }
            } else {
                if (getFailOnError()) {
                    throw new SmartFrogLifecycleException(e);
                }
            }
        }catch (Exception e) {
            if(log.isTraceEnabled()) {
                log.trace(e);
            }
            throw new SmartFrogLifecycleException(e);
        }
    } 

    /**
     * Life cycle method for terminating the SmartFrog component.
     *
     * @param tr Termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        super.sfTerminateWith(tr);
        if (session != null) {
            session.disconnect();
        }
    }

    protected abstract Session openSession() throws JSchException;

    /**
     * Reads SmartFrog attributes.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *                                  if failed to read any
     *                                  attribute or a mandatory attribute is not defined.
     * @throws java.rmi.RemoteException in case of network/rmi error
     */
    protected void readSFAttributes() throws SmartFrogException, RemoteException {
        // Mandatory attributes
        host = sfResolve(SSHExec.HOST, host, true);
        userName = sfResolve(SSHExec.USER, userName, true);
        commandsList = sfResolve(SSHExec.COMMANDS, commandsList, true);

        //optional attributes
        port = sfResolve(SSHExec.PORT, port, false);
        logFile = sfResolve(SSHExec.LOG_FILE, logFile, false);
        timeout = sfResolve(SSHExec.TIMEOUT, timeout, false);
        failOnError = sfResolve(SSHExec.FAIL_ON_ERROR, failOnError, false);
        shouldTerminate = sfResolve(SSHExec.TERMINATE, shouldTerminate, false);
        // TODO: trust onle pre-configured hosts
        //trustAllCerts = sfResolve(TRUST_ALL_CERTIFICATES, trustAllCerts, false);
    }

    protected boolean getFailOnError() {
        return failOnError;
    }

    /**
     * Logs debug message
     *
     * @param msg debug message
     */
    protected void logDebugMsg(String msg) {
        if (log.isDebugEnabled()) {
            log.debug(msg);
        }
    }

   /**
     * Logs ignored exception
     * @param e debug message
     */
    private void ignore(Exception e) {
        if (log.isDebugEnabled()) {
            log.debug("Ignoring Exception:" + e);
        }
    }
    
   

   
}
