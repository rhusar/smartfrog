/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SignJar;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;

import java.io.File;

/**
 * This is a datatype for the smartfrog tasks, one that
 * takes security settings. It also contains the code to define those settings
 * on the commandline.
 * Date: 19-Apr-2004
 * Time: 16:46:05
 */
public class Security extends DataType {

    private File keystore;
    private File passFile;
    private File policyFile;

    public File getKeystore() {
        return keystore;
    }

    /**
     * set the name of a keyword store
     *
     * @param keystore
     */
    public void setKeystore(File keystore) {
        this.keystore = keystore;
    }

    public File getPassFile() {
        return passFile;
    }

    /**
     * name a properties file containing the passwords in the syntax
     * org.smartfrog.sfcore.security.keyStorePassword=MkgzZVm9tyPdn77aWR54
     * org.smartfrog.sfcore.security.activate=true
     *
     * @param passFile
     */
    public void setPassFile(File passFile) {
        this.passFile = passFile;
    }


    public File getPolicyFile() {
        return policyFile;
    }

    /**
     * set a policy file containing security policy information.
     * Optional.
     * @param policyFile
     */
    public void setPolicyFile(File policyFile) {
        this.policyFile = policyFile;
    }

    /**
     * take a reference in a project and resolve it.
     *
     * @param project
     * @param reference
     * @return the security object we were referrring to.
     * @throws BuildException if the reference is to an unsupported type.
     */
    public static Security resolveReference(Project project,
                                            Reference reference) {
        assert project != null;
        assert reference != null;
        Object o = reference.getReferencedObject(project);
        if (!(o instanceof Security)) {
            throw new BuildException("reference is of wrong type");
        }
        return (Security) o;
    }

    /**
     * assert that a file must exist and be readable
     *
     * @param file name of file
     * @param role role for error message
     * @throws BuildException if it doesn't
     */
    protected void assertValidFile(File file, String role) {
        if (file == null) {
            throw new BuildException(role + " file is not defined");
        }
        String pretext = role + " file " + file;
        if (!file.exists()) {
            throw new BuildException(pretext + " does not exist");
        }
        if (!file.isFile()) {
            throw new BuildException(pretext + " is not a file");
        }
        if (!file.canRead()) {
            throw new BuildException(pretext + " is not readable");
        }
    }

    /**
     * validate the settings.
     */
    public void validate() {
        assertValidFile(keystore, "Keystore");
        assertValidFile(passFile, "PassFile");
        if(policyFile!=null) {
            assertValidFile(policyFile, "PolicyFile");
        }
    }

    /**
     * apply whatever security settings are needed for a daemon.
     */
    public void applySecuritySettings(SmartFrogTask task) {
        validate();
        task.addJVMProperty("org.smartfrog.sfcore.security.keyStoreName",
                keystore.getAbsolutePath());
        task.addJVMProperty("org.smartfrog.sfcore.security.propFile",
                passFile.getAbsolutePath());
        task.defineJVMArg("-Djava.security.manager");
        //the extra equals in the assignment forces it to overide all others
        if (policyFile != null) {
            task.defineJVMArg("-Djava.security.policy=="
                    + policyFile.getAbsolutePath());
        }
    }


    /**
     * apply whatever settings are needed for signing a jar file
     * @param signJar task to configure
     */
    public void applySecuritySettings(SignJar signJar) {
        validate();
        signJar.setKeystore(keystore.getAbsolutePath());
        //todo: get the pass in. 
        signJar.setKeypass("");
    }
}
