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

import com.jcraft.jsch.UserInfo;
import com.jcraft.jsch.UIKeyboardInteractive;
import org.smartfrog.sfcore.logging.LogSF;

/**
 * Implements UserInfo interface required by Jsch. 
 *
 * @author Ashish Awasthi
 * @see com.jcraft.jsch.UserInfo
 */
public class UserInfoImpl implements UserInfo, UIKeyboardInteractive {

    private String name;
    private String password = null;
    private String passphrase = null;
    private boolean trustAllCertificates = false;
    private LogSF log;


    public UserInfoImpl(LogSF log, boolean trustAllCertificates) {
        this.log = log;
        this.trustAllCertificates = trustAllCertificates;
    }

    /**
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the passphrase.
     *
     * @param passphrase The passphrase to set
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     * Sets the password.
     *
     * @param password The password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the trust.
     *
     * @param trust whether to trust or not.
     */
    public void setTrust(boolean trust) {
        trustAllCertificates = trust;
    }

    /**
     * @return whether to trust or not.
     */
    public boolean getTrust() {
        return trustAllCertificates;
    }

    /**
     * Returns the passphrase.
     *
     * @return String
     */
    @Override
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * @see com.jcraft.jsch.UserInfo#promptPassphrase(String)
     */
    @Override
    public boolean promptPassphrase(String message) {
        return passphrase!=null;
    }

    /**
     * @see com.jcraft.jsch.UserInfo#promptPassword(String)
     */
    @Override
    public boolean promptPassword(String passwordPrompt) {
        return password!=null;
    }

    
    /**
     * Ant's Implementation of UIKeyboardInteractive#promptKeyboardInteractive.
     *
     * @param destination not used.
     * @param username        not used.
     * @param instruction not used.
     * @param prompt      the method checks if this is one in length.
     * @param echo        the method checks if the first element is false.
     * @return the password in an size one array if there is a password and if the prompt and echo checks pass.
     */
    @Override
    public String[] promptKeyboardInteractive(String destination,
                                              String username,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo) {
        if (prompt.length != 1 || echo[0] || password == null) {
            return null;
        }
        String[] response = new String[1];
        response[0] = password;
        return response;
    }


    /**
     * @see com.jcraft.jsch.UserInfo#promptYesNo(String)
     */
    @Override
    public boolean promptYesNo(String message) {
        return trustAllCertificates;
    }

    /**
     * @see com.jcraft.jsch.UserInfo#showMessage(String)
     */
    @Override
    public void showMessage(String message) {
        log.info(message);
    }


    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "user " + name + " [trustsEveryone:" + trustAllCertificates + ", authentication:"
                + (password == null ? "no password" : "password set") + "]";
    }
}
