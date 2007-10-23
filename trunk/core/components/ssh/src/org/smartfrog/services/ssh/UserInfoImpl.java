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
import org.smartfrog.sfcore.logging.LogSF;

/**
 *  Implements UserInfo interface required by Jsch.
 *  Some of these methods are not in the current version of UserInfo; they are retained
 *  for historical compatibility, and, as they return false, incur no development/maintenance
 *  costs. At some point they may be deleted.
 *  @see com.jcraft.jsch.UserInfo
 *  @author Ashish Awasthi
 */
public class UserInfoImpl implements UserInfo {

    private String name;
    private String password = null;
    private String passphrase = null;
    private boolean trustAllCertificates = false;
    private LogSF log;


    public UserInfoImpl(LogSF log, boolean trustAllCertificates) {
        this.log=log;
        this.trustAllCertificates = trustAllCertificates;
    }

    /**
     */
    public String getName() {
        return name;
    }

    /**
     * @return pass phrase
     */
    public String getPassphrase(String message) {
        return passphrase;
    }

    /**
     * 
     */
    public String getPassword() {
        return password;
    }

    /**
     * prompt for a string
     * @return false always
     */
    public boolean prompt(String str) {
        return false;
    }

    /**
     * Retry
     * @return false always
     */
    public boolean retry() {
        return false;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the passphrase.
     * @param passphrase The passphrase to set
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     * Sets the password.
     * @param password The password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the trust.
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
     * @return String
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * @see com.jcraft.jsch.UserInfo#promptPassphrase(String)
     */
    public boolean promptPassphrase(String message) {
        return true;
    }

    /**
     * @see com.jcraft.jsch.UserInfo#promptPassword(String)
     */
    public boolean promptPassword(String passwordPrompt) {
        return false;
    }

    /**
     * @see com.jcraft.jsch.UserInfo#promptYesNo(String)
     */
    public boolean promptYesNo(String message) {
        return trustAllCertificates;
    }

    /**
     * @see com.jcraft.jsch.UserInfo#showMessage(String)
     */
    public void showMessage(String message) {
        log.info(message);
    }


    /**
     * Returns a string representation of the object. In general, the <code>toString</code> method returns a string that
     * "textually represents" this object. The result should be a concise but informative representation that is easy
     * for a person to read. It is recommended that all subclasses override this method. <p> The <code>toString</code>
     * method for class <code>Object</code> returns a string consisting of the name of the class of which the object is
     * an instance, the at-sign character `<code>@</code>', and the unsigned hexadecimal representation of the hash code
     * of the object. In other words, this method returns a string equal to the value of: <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "user "+ name +" trustsEveryone:"+trustAllCertificates+" uses "
                +(password==null?"public key":"password");
    }
}
