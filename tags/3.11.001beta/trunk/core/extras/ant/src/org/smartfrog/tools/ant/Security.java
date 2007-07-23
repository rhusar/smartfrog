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
import org.apache.tools.ant.util.FileUtils;
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

    /** java security keystore */
    private File keystore;
    /** java security policy file */
    private File policyFile;

    /** sf security resource */
    private File securityProperties;

    /** the identity to use */
    private String alias;

    /**
     * enabled flag. Security refs are enabled by default.
     */
    private boolean enabled=true;

    /**
     * error string used in JUnit test cases
     */
    public static final String ERROR_REFID_EXCLUSIVE = "Cannot have other attributes when refid is set";

    /**
     * error string used in JUnit test cases
     */
    public static final String ERROR_NO_ALIAS = "alias attribute must be set";

    /**
     * get the keystore
     * @return keystore or null
     */
    public File getKeystore() {
        return keystore;
    }

    /**
     * set the name of a keyword store
     *
     * @param keystore keystore to use
     */
    public void setKeystore(File keystore) {
        this.keystore = keystore;
    }

    /**
     * Get the security properties
     * @return security properties file or null
     */
    public File getSecurityProperties() {
        return securityProperties;
    }

    /**
     * name a properties file containing the passwords in the syntax
     * org.smartfrog.sfcore.security.keyStorePassword=MkgzZVm9tyPdn77aWR54
     * org.smartfrog.sfcore.security.activate=true
     *
     * @param securityProperties
     */
/*
    public void setSecurityResource(String securityProperties) {
        this.securityProperties = securityProperties;
    }
*/

    /**
     * Path to a file containing the security properties
     * @param securityProperties security properties
     */
    public void setSecurityFile(File securityProperties) {
        this.securityProperties = securityProperties;
    }


    public File getPolicyFile() {
        return policyFile;
    }

    /**
     * set a policy file containing security policy information.
     * Optional.
     * @param policyFile  policy file containing security policy information.
     */
    public void setPolicyFile(File policyFile) {
        this.policyFile = policyFile;
    }

    public String getAlias() {
        return alias;
    }

    /**
     * set the alias to use when signing
     * @param alias signing alias
     */
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
     * Test for this security element being enabled
     * @return true if this security element is to be used, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the enabled flag. Disabled security elements are effectively unused.
     * @param enabled true for security to be on
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * resolve any references, return the base security reference
     * @return the security instance that may be us, may be somebody else.
     */
    public Security resolve() {
        if(getRefid()==null) {
            //check the syntax is good
            validateReferencesAndAttributes();
            return this;
        }
        return (Security) getCheckedRef(Security.class, "Security");
    }

    /**
     * take a reference in a project and resolve it.
     *
     * @param project project to resolve against
     * @param reference reference to resolve
     * @return the security object we were referrring to.
     * @throws BuildException if the reference is to an unsupported type.
     */
    public static Security resolveReference(Project project,
                                            Reference reference) {
        //create a temp security
        Security security=new Security();
        //bound to this project
        security.setProject(project);
        //with the refID attr
        security.setRefid(reference);
        //which we ask to resolve
        return security.resolve();
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
     * verifies that attributes are free if the references are set
     */
    private void validateReferencesAndAttributes() {
        if(getRefid()!=null && !isEmpty()) {
            throw new BuildException(ERROR_REFID_EXCLUSIVE);
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
        assertValidFile(securityProperties, "Security Properties");
        if(alias==null) {
            throw new BuildException(ERROR_NO_ALIAS);
        }
    }

    /**
     * apply whatever security settings are needed for a daemon.
     * @param task to apply properties to,
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
     * @throws IOException if something happens when reading/writing files
     */
    public void applySecuritySettings(SignJar signJar) throws IOException {
        validateForSigning();
        signJar.setKeystore(keystore.getAbsolutePath());
        //get the pass in.
        Properties securityProps = loadPassFile();
        signJar.setStorepass(securityProps.getProperty(SmartFrogJVMProperties.KEYSTORE_PASSWORD));
        signJar.setAlias(getAlias());
    }

    /**
     * load the passfile into a properties structure
     * @return the loaded file
     * @throws IOException if something happens when reading/writing files
     */
    private Properties loadPassFile() throws IOException {
        Properties securityProps=new Properties();
        InputStream instream=null;
        try {
            instream=new FileInputStream(securityProperties);
            securityProps.load(instream);
            return securityProps;
        } finally {
            FileUtils.close(instream);
        }
    }

    /**
     * @return a string representation of the object.
     */
    public String toString() {
        return "Security: keystore="+keystore+" passfile="+securityProperties;
    }
}
