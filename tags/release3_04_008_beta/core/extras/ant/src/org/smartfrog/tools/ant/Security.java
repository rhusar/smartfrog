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
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This is a datatype for the smartfrog tasks, one that
 * takes security settings. It also contains the code to define those settings
 * on the commandline.
 * Date: 19-Apr-2004
 * Time: 16:46:05
 */
public class Security extends DataType {

    //java security keystore
    private File keystore;
    //java security policy file
    private File policyFile;
    //sf security resource
    private String securityProperties;

    //the identity to use
    private String alias;


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

    public String getSecurityProperties() {
        return securityProperties;
    }

    /**
     * name a properties file containing the passwords in the syntax
     * org.smartfrog.sfcore.security.keyStorePassword=MkgzZVm9tyPdn77aWR54
     * org.smartfrog.sfcore.security.activate=true
     *
     * @param securityProperties
     */
    public void setSecurityResource(String securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * Path to a file containing the security properties
     * @param securityProperties
     */
    public void setSecurityFile(File securityProperties) {
        this.securityProperties = securityProperties.getAbsolutePath();
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * test for this security declaration being empty
     * @return true if it is completely undefined.
     */
    public boolean isEmpty() {
        return keystore==null && policyFile==null && securityProperties==null
                && alias==null;
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
        validateForSigning();
        assertValidFile(policyFile, "PolicyFile");
    }


    /**
     * validate the settings.
     */
    public void validateForSigning() {
        assertValidFile(keystore, "Keystore");
    }

    /**
     * apply whatever security settings are needed for a daemon.
     */
    public void applySecuritySettings(SmartFrogTask task) {
        validate();
        task.addJVMProperty(SmartFrogJVMProperties.KEYSTORE_NAME,
                keystore.getAbsolutePath());
        task.addSmartfrogPropertyIfDefined(SmartFrogJVMProperties.KEYSTORE_PROPFILE,
                securityProperties);
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
    public void applySecuritySettings(SignJar signJar) throws IOException {
        validateForSigning();
        signJar.setKeystore(keystore.getAbsolutePath());
        //get the pass in.
        Properties securityProps = loadPassFile();
        signJar.setKeypass(securityProps.getProperty(SmartFrogJVMProperties.KEYSTORE_PASSWORD));
        signJar.setAlias(getAlias());
    }

    /**
     * load the passfile into a properties structure
     * @return
     * @throws IOException
     */
    private Properties loadPassFile() throws IOException {
        Properties securityProps=new Properties();
        InputStream instream=null;
        try {
            instream=new FileInputStream(securityProperties);
            securityProps.load(instream);
            return securityProps;
        } finally {
            if(instream!=null) {
                try {
                    instream.close();
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * @return a string representation of the object.
     */
    public String toString() {
        return "Security: keystore="+keystore+" passfile="+securityProperties;
    }
}
