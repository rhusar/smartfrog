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
package org.smartfrog.services.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.passwords.PasswordProvider;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * This base class handles all SSH authentication issues for any component that needs SSH auth. <p/> Created 22-Oct-2007
 * 16:04:14
 */

public abstract class  AbstractSSHComponent extends PrimImpl implements SSHComponent {

    protected LogSF log;
    protected String passphrase;
    protected String keyFile;
    protected boolean usePublicKey;
    protected UserInfoImpl userInfo;
    private static final Reference pwdProviderRef = new Reference(SSHComponent.ATTR_PASSWORD_PROVIDER);
    protected boolean trustAllCerts = true;
    protected static final String TIMEOUT_MESSAGE = "Connection timed out connecting to ";
    private static final int SSH_PORT = 22;
    protected int timeout = 0;
    protected String host;
    protected int port = SSH_PORT;

    protected boolean failOnError = true;
    protected boolean shouldTerminate = true;
    protected String userName;

    protected Vector knownHosts;
    private volatile Session session = null;
    protected static final String SESSION_IS_DOWN = "session is down";

    protected AbstractSSHComponent() throws RemoteException {
    }


    /**
     * Called after instantiation for deployment purposes. <p/> This class sets up the log, then reads in the security
     * attributes
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        log = sfLog();
        readCommonSSHAttributes();
    }

    /**
     * Read in the common attributes of SSH components: authentication parameters, username, workflow, etc.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    protected void readCommonSSHAttributes() throws RemoteException, SmartFrogException {
        //first choose the policy, or fail
        String policy = sfResolve(ATTR_AUTHENTICATION, "", true);
        if (AUTHENTICATION_PUBLICKEY.equals(policy)) {
            usePublicKey = true;
        } else if (AUTHENTICATION_PASSWORD.equals(policy)) {
            usePublicKey = false;
        } else {
            throw new SmartFrogResolutionException("Unsupported value for attribute " + ATTR_AUTHENTICATION + ": " + policy);
        }

        // Mandatory attributes
        trustAllCerts = sfResolve(ATTR_TRUST_ALL_CERTIFICATES, trustAllCerts, true);

        //create the user info to get filled in.
        userInfo = new UserInfoImpl(sfLog(), trustAllCerts);
        PasswordProvider pwdProvider = (PasswordProvider) sfResolve(pwdProviderRef);
        passphrase = pwdProvider.getPassword();

        if (usePublicKey) {
            //load in the key file
            keyFile = FileSystem.lookupAbsolutePath(this, ATTR_KEYFILE, null, null, true, null);
            userInfo.setPassphrase(passphrase);
        } else {
            userInfo.setPassword(passphrase);
            userInfo.setPassphrase(passphrase);
        }
        userName = sfResolve(ATTR_USER, userName, true);
        userInfo.setName(userName);
        
        host = sfResolve(ATTR_HOST, host, true);
        shouldTerminate = sfResolve(ATTR_SHOULD_TERMINATE, shouldTerminate, false);
        port = sfResolve(ATTR_PORT, port, true);
        failOnError = sfResolve(ATTR_FAIL_ON_ERROR, failOnError, true);

        //optional attributes
        timeout = sfResolve(ATTR_TIMEOUT, timeout, false);
        knownHosts = sfResolve(ATTR_KNOWN_HOSTS,knownHosts,false);

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
     *
     * @param e debug message
     */
    private void ignore(Exception e) {
        log.ignore("Ignoring Exception", e);
    }

    /**
     * Create a JSch connection using the current security policies.
     *
     * @return a jsch instance which will have any keyfile settings applied
     * @throws JSchException if the operation fails
     */
    protected JSch createJschInstance() throws JSchException {
        JSch jsch = new JSch();
        if (usePublicKey) {
            jsch.addIdentity(keyFile);
        }
        return jsch;
    }


    /**
     * Gets a SSH session after connecting to remote host over SSH.
     *
     * @return SSH Session
     * @throws JSchException if unable to open SSH session
     * @see Session
     */
    protected Session openSession() throws JSchException {
        JSch jsch = createJschInstance();
        Session session = createSession(jsch);
        session.setTimeout(timeout);
        session.connect();
        return session;
    }

    /**
     * Create a session with the current user policies applied
     *
     * @param jsch the sch instances
     * @return a connected session
     * @throws JSchException if things go wrong
     */
    protected Session createSession(JSch jsch) throws JSchException {
        Session session;
        session = jsch.getSession(userInfo.getName(), host, port);
        session.setUserInfo(userInfo);
        if(!usePublicKey) {
           session.setPassword(userInfo.getPassword());
        }
        log.info("Connecting to " + getConnectionDetails());
        return session;
    }

    /**
     * Provide a diagnostics string for use in error messages and the like
     * @return
     */
    public String getConnectionDetails() {
        return host + ":" + port + " as " + userInfo;
    }

    /**
     * Get the current session
     * @return the session
     */
    public synchronized Session getSession() {
        return session;
    }

    /**
     * Set the current session
     * @param session the new session, can be null
     */
    public synchronized void setSession(Session session) {
        this.session = session;
    }

    /**
     * Life cycle method for terminating the SmartFrog component.
     *
     * @param tr Termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        super.sfTerminateWith(tr);
        if (getSession() != null) {
            try {
                getSession().disconnect();
            } finally {
                session=null;
            }
        }
    }

    /**
     * Translate
     * @param ex incoming exception
     * @return a new lifecycle exception that includes connection details
     */
    public SmartFrogLifecycleException translateStartupException(JSchException ex) {
        log.error("When connecting to " + getConnectionDetails(), ex);
        if (ex.getMessage().indexOf(SESSION_IS_DOWN) >= 0) {
            String message = TIMEOUT_MESSAGE + getConnectionDetails();
            return new SmartFrogLifecycleException(message, ex);
        } else {
            return new SmartFrogLifecycleException(getConnectionDetails()+" -"+ex.getMessage(),ex);
        }
    }
}
