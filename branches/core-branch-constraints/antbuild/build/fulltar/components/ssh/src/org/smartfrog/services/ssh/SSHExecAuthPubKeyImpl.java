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

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
//import org.smartfrog.services.ssh.FilePasswordProvider;

/**
 * SmartFrog component to executes a command on a remote machine via ssh.
 * It is a wrapper around jsch-0.1.33
 *
 * @author Ritu Sabharwal
 *         see http://www.jcraft.com/jsch/
 */
public class SSHExecAuthPubKeyImpl extends SSHExecImpl {
    private String passphrase;
    private String keyFile;
    private Reference pwdProviderRef = new Reference("passwordProvider");

    /**
     * Constructs SSHExecImpl object.
     */
    public SSHExecAuthPubKeyImpl() throws RemoteException {
        super();
    }

    /**----------------SmartFrog Life Cycle Methods Begin--------------------*/

    /**
     * Reads SmartFrog attributes and deploys SSHExecImpl component.
     *
     * @throws SmartFrogException in case of error in deploying or reading the
     *                            attributes
     * @throws RemoteException    in case of network/emi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
                                               RemoteException {
        super.sfDeploy();
        userInfo.setPassphrase(passphrase);
    }

    /**----------------SmartFrog Life Cycle Methods End ---------------------*/

    /**
     * Reads SmartFrog attributes.
     *
     * @throws SmartFrogResolutionException if failed to read any
     *                                      attribute or a mandatory attribute is not defined.
     * @throws RemoteException              in case of network/rmi error
     */
    protected void readSFAttributes() throws SmartFrogException, RemoteException {
        super.readSFAttributes();
        // Mandatory attributes
        keyFile = sfResolve(KEYFILE, keyFile, true);
        PasswordProvider pwdProvider = (PasswordProvider) sfResolve(pwdProviderRef);
        passphrase = pwdProvider.getPassword();
    }

    /**
     * Opens a SSH session.
     *
     * @return SSH Session
     * @throws JSchException if unable to open SSH session
     * @see com.jcraft.jsch.Session
     */
    protected Session openSession() throws JSchException {
        JSch jsch = new JSch();
        jsch.addIdentity(keyFile);
        Session session = jsch.getSession(userInfo.getName(), host, port);
        session.setUserInfo(userInfo);
        log.info("Connecting to " + host + " at Port:" + port);
        session.connect();
        return session;
    }
}

