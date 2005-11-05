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
package org.smartfrog.services.ssh;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import java.rmi.RemoteException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.UserInfo;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.Log;
//import org.smartfrog.services.ssh.FilePasswordProvider;
/**
 * SmartFrog component to executes a command on a remote machine via ssh. 
 * It is a wrapper around jsch-0.1.14
 * @author Ashish Awasthi
 * @see http://www.jcraft.com/jsch/
 * 
 */
public class SSHExecAuthPubKeyImpl extends PrimImpl implements SSHExec{

    private static final String TIMEOUT_MESSAGE = "Connection timed out";
    private static final int SSH_PORT = 22;
    private long timeout = 0;
    private boolean append = false;   
    private String host;
    private String userName;
    private String passphrase;
    private String keyFile;
    private int port = SSH_PORT;
    private String logFile = null;
    private FileOutputStream fout = null;
    private boolean failOnError = true;
    private UserInfoImpl userInfo;
    private Vector commandsList;
    private Reference pwdProviderRef = new Reference("passwordProvider");
    private PasswordProvider pwdProvider;
    private boolean trustAllCerts = true ; 
    private boolean  shouldTerminate = true;
    private Thread waitThread = null;
    private Session session = null;
    private Log log;
    
    /**
     * Constructs SSHExecImpl object.
     */
    public SSHExecAuthPubKeyImpl() throws RemoteException{
        super();
    }
    

    /**----------------SmartFrog Life Cycle Methods Begin--------------------*/

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
        log = sfGetApplicationLog();
        assert log != null;
        readSFAttributes();
        userInfo = new UserInfoImpl(trustAllCerts);
        userInfo.setName(userName);
        log.info("User Name: "+ userName);
	userInfo.setPassphrase(passphrase);
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
     *@param tr Termination record
     *
     */ 
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        super.sfTerminateWith(tr);
        if (session != null) {
            session.disconnect();
        }
    }
    /**----------------SmartFrog Life Cycle Methods End ---------------------*/

    /**
     * Reads SmartFrog attributes.
     * @throws SmartFrogResolutionException if failed to read any 
     * attribute or a mandatory attribute is not defined.
     * @throws RemoteException in case of network/rmi error
     */ 
    private void readSFAttributes() throws SmartFrogException, RemoteException{
        // Mandatory attributes
        host = sfResolve(HOST, host, true);
        userName = sfResolve(USER, userName, true);
        keyFile = sfResolve(KEYFILE,keyFile, true);
        pwdProvider = (PasswordProvider) sfResolve(pwdProviderRef);
        passphrase = pwdProvider.getPassword();
        commandsList = sfResolve(COMMANDS, commandsList, true);

        //optional attributes
        port = sfResolve(PORT, port, false);
	logFile = sfResolve(LOG_FILE, logFile, false);
        timeout = sfResolve(TIMEOUT, timeout, false);
        failOnError = sfResolve(FAIL_ON_ERROR, failOnError, false);
        shouldTerminate = sfResolve(TERMINATE, shouldTerminate, false);
        // TODO: trust onle pre-configured hosts
        //trustAllCerts = sfResolve(TRUST_ALL_CERTIFICATES, trustAllCerts, false);
    }

    /**
     * Opens a SSH session.
     * @return SSH Session
     * @thows JSchException if unable to open SSH session
     * @see com.jcraft.jsch.Session
     */
    private Session openSession() throws JSchException {
        JSch jsch = new JSch();
	jsch.addIdentity(keyFile);
        Session session = jsch.getSession(userInfo.getName(), host, port);
        session.setUserInfo(userInfo);
      //  session.setPassword(password);
        log.info("Connecting to " + host + " at Port:" + port);
        session.connect();
        return session;
    }
    private boolean getFailOnError() {
        return failOnError;
    }
    /**
     * Logs debug message
     * @param msg debug message
     */
    private void logDebugMsg(String msg) {
        if (log.isDebugEnabled()) {
            log.debug(msg);
        }
    }
    /**
     * Logs ignored exception
     * @param msg debug message
     */
    private void ignore(Exception e) {
        if (log.isDebugEnabled()) {
            log.debug("Ignoring Exception:" + e);
        }
    }
}

