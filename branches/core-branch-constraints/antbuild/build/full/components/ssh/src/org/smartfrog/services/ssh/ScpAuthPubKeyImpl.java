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

import java.util.Vector;
import java.rmi.RemoteException;

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

/**
 * SmartFrog component to upload/download files to/from a remote machine over 
 * SSH.
 * It is a wrapper around jsch-0.1.14
 * @author Ashish Awasthi
 * @see <a href="http://www.jcraft.com/jsch/">jsch</a>
 * 
 */
public class ScpAuthPubKeyImpl extends PrimImpl implements IScp{
    
    /**
     * Time out message.
     */
    private static final String TIMEOUT_MESSAGE = "SSH connection timed out";
    /**
     * Default SSH Port
     */
    private static final int SSH_PORT = 22;
    /**
     * SSH Session timeout.
     */
    private long timeout = 0;
    /**
     *  SSH Host.
     */
    private String host;
    /**
     * Login ID.
     */
    private String userName;
    /**
     * Password.
     */
    private String password;

    private String passphrase;
    private String keyFile;

    /**
     * SSH Port. Default value is 22
     */
    private int port = SSH_PORT;
   
    /**
     * UserInfo required by jsch.
     * @see com.jcraft.jsch.UserInfo
     */ 
    private UserInfoImpl userInfo;
    /**
     * SmartFrog Reference to Password Provider.
     */
    private Reference pwdProviderRef = new Reference("passwordProvider");
    /**
     * Reference to password provider.
     */
    private PasswordProvider pwdProvider;
    /**
     * Trust all certs or not, Default is true
     */
    private boolean trustAllCerts = true ; 
    /**
     * Should terminate after copying or not default is true.
     */
    private boolean  shouldTerminate = true;
    /**
     * jsch session.
     * @com.jcraft.jsch.Session
     */
    private Session session = null;
    /**
     * SmartFrog Logger.
     */
    private Log log;
    private final String GET = "get";
    private final String PUT = "put";
    /**
     * Type of transfer. "get": download, "put": upload
     */
    private String transferType =  "get"; 
    /**
     * Vector of remote file names
     */
    private Vector remoteFileList =  null; 
    /**
     * Vector of local file names
     */
    private Vector localFileList =  null; 

    /**
     * Constructs ScpImpl object.
     */
    public ScpAuthPubKeyImpl() throws RemoteException{
        super();
    }
    

    /**----------------SmartFrog Life Cycle Methods Begin--------------------*/

    /**
     * Deploys ScpImpl component and reads SmartFrog attributes and .
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
     * Connects to remote host over SSH and uploads/downloads files.
     *
     * @throws SmartFrogException in case of error while connecting to remote 
     * host or executing scp command
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
            if(transferType.equalsIgnoreCase(GET)) {
                log.info("Going to start scp to download files");
                ScpFrom scpFrom = new ScpFrom(log);
                scpFrom.doCopy(session, remoteFileList, localFileList);
            }
            else if(transferType.equalsIgnoreCase(PUT)) {
                log.info("Going to start scp to upload files");
                ScpTo scpTo = new ScpTo(log);
                scpTo.doCopy(session, remoteFileList, localFileList);
            }
            else {
                throw new SmartFrogLifecycleException(
                     "Unsupported action :"+ transferType);
            }
            // check if it should terminate by itself
            if(shouldTerminate) {
                log.info(sfCompleteNameSafe() + "Terminating itself...");
                TerminationRecord termR = new TerminationRecord("normal",
                "SSH Session finished: ",sfCompleteName());
                TerminatorThread terminator = new TerminatorThread(this,termR);
                terminator.start();
            }
        }catch (SmartFrogException sfe){
            throw sfe;
        }catch (JSchException e) {
            if (e.getMessage().indexOf("session is down") >= 0) {
                    throw new SmartFrogLifecycleException(TIMEOUT_MESSAGE, e);
            } else {
                    throw new SmartFrogLifecycleException(e);
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
        remoteFileList = sfResolve(REMOTE_FILES, remoteFileList, true);
        localFileList = sfResolve(LOCAL_FILES, localFileList, true);

        //optional attributes
        port = sfResolve(PORT, port, false);
        timeout = sfResolve(TIMEOUT, timeout, false);
        shouldTerminate = sfResolve(TERMINATE, shouldTerminate, false);
        transferType = sfResolve(TRANSFER_TYPE, transferType, false);
        log.info("Transfer Type :="+ transferType);
        
        // TODO: trust onle pre-configured hosts
        //trustAllCerts = sfResolve(TRUST_ALL_CERTIFICATES, trustAllCerts, false);
    }

    /**
     * Gets a SSH session after connecting to remote host over SSH.
     * @return SSH Session
     * @thows JSchException if unable to open SSH session
     * @see com.jcraft.jsch.Session
     */
    private Session openSession() throws JSchException {
        JSch jsch = new JSch();
	jsch.addIdentity(keyFile);
        Session session = jsch.getSession(userInfo.getName(), host, port);
        session.setUserInfo(userInfo);
       // session.setPassword(password);
        log.info("Connecting to " + host + " at Port:" + port);
        session.connect();
        return session;
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
}

