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

import com.jcraft.jsch.JSchException;

import java.rmi.RemoteException;

/**
 * SmartFrog component to executes a command on a remote machine via ssh.
 * It is a wrapper around jsch-0.1.33
 *
 * @author Ritu Sabharwal
 *         see http://www.jcraft.com/jsch/
 */
public class SSHExecAuthPassImpl extends SSHExecImpl {

    /**
     * Constructs SSHExecImpl object.
     */
    public SSHExecAuthPassImpl() throws RemoteException {
        super();
    }

    /**
     * ----------------SmartFrog Life Cycle Methods Begin--------------------
     */


    /**
     * ----------------SmartFrog Life Cycle Methods End ---------------------
     */
/*
    protected void readSFAttributes() throws SmartFrogException, RemoteException {
        super.readSFAttributes();
        PasswordProvider pwdProvider = (PasswordProvider) sfResolve(pwdProviderRef);
        password = pwdProvider.getPassword();
    }*/

    /**
     * Opens a SSH session.
     *
     * @return SSH Session
     * @throws JSchException if unable to open SSH session
     * @see com.jcraft.jsch.Session
     */
/*    protected Session openSession() throws JSchException {
        JSch jsch = createJschInstance();
        Session session = jsch.getSession(userInfo.getName(), host, port);
        session.setUserInfo(userInfo);
        session.setPassword(password);
        log.info("Connecting to " + host + " at Port:" + port);
        session.connect();
        return session;
    }*/

}

