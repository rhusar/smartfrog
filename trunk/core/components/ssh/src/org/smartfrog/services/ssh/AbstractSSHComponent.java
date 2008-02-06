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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * This base class handles all SSH authentication issues for any component that needs SSH auth.
 *
 *  <p/> Created 22-Oct-2007
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
    private static final int SSH_PORT = 22;
    protected int timeout = 0;
    protected int connectTimeout = 0;
    protected String host;
    protected int port = SSH_PORT;
    protected String userName;

    protected Vector knownHosts;

    private volatile Session session = null;

    protected static final String TIMEOUT_MESSAGE = "Connection timed out connecting to ";
    protected static final String SESSION_IS_DOWN = "session is down";
    private static final String AUTH_FAIL = "Auth fail";
    private static final String AUTH_CANCEL = "Auth cancel";
    public static final String ERROR_WRONG_PASSWORD_PROVIDER_TYPE = "The attribute "+ATTR_PASSWORD_PROVIDER+" must be a lazy reference to a class that implements the "
    +"org.smartfrog.services.passwords.PasswordProvider"+" interface -";

    /**
     * Only subclasses can instantiate this
     * @throws RemoteException if the superclass constructor raises it
     */
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
            throw new SmartFrogResolutionException("Unsupported value for attribute "
                    + ATTR_AUTHENTICATION + ": " + policy);
        }

        // Mandatory attributes
        trustAllCerts = sfResolve(ATTR_TRUST_ALL_CERTIFICATES, trustAllCerts, true);

        //create the user info to get filled in.
        userInfo = new UserInfoImpl(sfLog(), trustAllCerts);
        Prim provider = sfResolve(pwdProviderRef,(Prim)null,true);
        if(!(provider instanceof PasswordProvider)) {
            throw new SmartFrogResolutionException(
                    ERROR_WRONG_PASSWORD_PROVIDER_TYPE
                    +"what is present is an instance of "+provider.getClass()+" with value "+provider.toString());
        }
        PasswordProvider pwdProvider = (PasswordProvider) provider;
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
        port = sfResolve(ATTR_PORT, port, true);

        timeout = sfResolve(ATTR_TIMEOUT, timeout, true);
        connectTimeout = sfResolve(ATTR_CONNECT_TIMEOUT, connectTimeout, true);
        knownHosts = sfResolve(ATTR_KNOWN_HOSTS,knownHosts,false);

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
     * Create a JSch connection using the current security policies.
     *
     * @return a jsch instance which will have any keyfile settings applied
     * @throws JSchException if the operation fails
     */
    public JSch createJschInstance() throws JSchException {
        JSch jsch = new JSch();
        if (usePublicKey) {
            jsch.addIdentity(keyFile);
        }
        //set the logger up to our logging.
        JSch.setLogger(new JschLogger(log));
        return jsch;
    }


    /**
     * Gets a SSH session after connecting to remote host over SSH.
     * the value is saved in setSssion
     * @return SSH Session
     * @throws JSchException if unable to open SSH session
     * @see Session
     */
    public synchronized Session openSession() throws JSchException {
        if(session!=null) {
            throw new JSchException("existing sessin is in use");
        }
        JSch jsch = createJschInstance();
        Session newSession = createSession(jsch);
        newSession.setTimeout(timeout);
        newSession.connect(connectTimeout);
        setSession(newSession);
        return newSession;
    }



    /**
     * Create a session with the current user policies applied
     *
     * @param jsch the sch instances
     * @return a connected session
     * @throws JSchException if things go wrong
     */
    protected Session createSession(JSch jsch) throws JSchException {
        Session newSession;
        newSession = jsch.getSession(userInfo.getName(), host, port);
        newSession.setUserInfo(userInfo);
        if(!usePublicKey) {
           newSession.setPassword(userInfo.getPassword());
        }
        log.info("Connecting to " + getConnectionDetails());
        return newSession;
    }

    /**
     * Provide a diagnostics string for use in error messages and the like
     * @return the connection info
     */
    public String getConnectionDetails() {
        return host + ':' + port + " as " + userInfo;
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
        endSession();
    }

    /**
     * end any active session and set the session variable to null
     */
    public synchronized void endSession() {
        if (getSession() != null) {
            try {
                getSession().disconnect();
            } finally {
                session=null;
            }
        }
    }

    /**
     * Translate an exception into a SmartFrogLifecycle one.
     * IF the exception is a JSchException, it is left to
     * {@link #translateStartupException(JSchException)} to handle
     * @param thrown incoming exception
     * @return a lifecycle exception
     */
    protected SmartFrogLifecycleException forward(Throwable thrown) {
        if(thrown instanceof JSchException) {
            return translateStartupException((JSchException) thrown);
        } else {
            return (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thrown);
        }
    }


    /**
     * Translate a jsch exception into a SmartFrog one, including better diagnostics.
     * This is brittle as it searches for specific error text in the exception.
     * @param ex incoming exception
     * @return a new lifecycle exception that includes connection details
     */
    protected SmartFrogLifecycleException translateStartupException(JSchException ex) {
        String message;
        String faulttext = ex.getMessage();
        if (faulttext.contains(SESSION_IS_DOWN)) {
            message = TIMEOUT_MESSAGE + getConnectionDetails();
        } else if (faulttext.contains(AUTH_FAIL) || faulttext.contains(AUTH_CANCEL)) {
            message = "Unable to authenticate with the server" + getConnectionDetails()
                    + "\nThis can be caused by: "
                    + "\n -Unknown username "+userName
                    + "\n -wrong password"
                    + (usePublicKey?
                      "\n -key-based authentication failure":
                      "\n -server not supporting password authentication")
                    + (trustAllCerts?
                      "\n -server not trusted":
                      "")
                    + "\n -server not supporting login by that user";
        } else {
            message=getConnectionDetails()+" -"+ faulttext;
        }
        return new SmartFrogLifecycleException(message, ex);
    }
}
