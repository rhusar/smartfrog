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
import org.apache.tools.ant.taskdefs.SignJar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;

import java.io.File;
import java.io.IOException;

/**
 *
 * @ant.task category="SmartFrog" name="sf-sign"
 * Sign JAR files using the SmartFrog security configuration.
 * This task is essentially a thin wrapper around Ant's <tt>signjar</tt> task,
 * with integration with the SmartFrog security properties file, that being
 * where the passphrase to unlock the keystore is extracted.
 *
 */
public class SmartFrogSign extends TaskBase {


    /**
     * the task used for signing
     */
    private SignJar signer;

    /**
     * our security holder
     */
    private SecurityHolder securityHolder = new SecurityHolder();
    public static final String ERROR_NO_SECURITY_SETTINGS = "No security settings provided";
    public static final String ERROR_COULD_NOT_APPLY_SETTINGS = "Could not apply security settings with ";
    public static final String MESSAGE_EMPTY_SECURITY = "Empty security: signing skipped";


    /**
     * Called by the project to let the task initialize properly.
     * The default implementation is a no-op.
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void init() throws BuildException {

        //create a signer task
        signer = (SignJar) getProject().createTask("signjar");
        signer.setTaskName(this.getTaskName());
    }

    /**
     * set a reference to the security types
     *
     * @param securityRef
     */
    public void setSecurityRef(Reference securityRef) {
        securityHolder.setSecurityRef(securityRef);
    }

    /**
     * set a security definition
     *
     * @param security
     */
    public void addSecurity(Security security) {
        securityHolder.addSecurity(security);
    }

    /**
     * name a JAR file to sign
     *
     * @param file
     */
    public void setJar(File file) {
        signer.setJar(file);
    }

    /**
     * name the jar to sign
     * @param file
     */
    public void setSignedJar(File file) {
        signer.setSignedjar(file);
    }

    /**
     * a fileset of jar files to sign
     * @param fileset
     */
    public void addFileSet(FileSet fileset) {
        signer.addFileset(fileset);
    }

    /**
     * enable verbose output
     * @param verbose
     */
    public void setVerbose(boolean verbose) {
        signer.setVerbose(verbose);
    }

    /**
     * flag to control whether the presence of a signature file means a JAR is signed
     * and so does not need resigning
     * @param lazy
     */
    public void setLazy(boolean lazy) {
        signer.setLazy(true);
    }

    /**
     *  Set the maximum memory to be used by the jarsigner process
     *
     * @param maxMemory a string indicating the maximum memory according to the
     *        JVM conventions (e.g. 128m is 128 Megabytes)
     */ 
    public void setMaxMemory(String maxMemory) {
        signer.setMaxmemory(maxMemory);
    }


    /**
     * Called by the project to let the task do its work. This method may be
     * called more than once, if the task is invoked more than once.
     * For example,
     * if target1 and target2 both depend on target3, then running
     * "ant target1 target2" will run all tasks in target3 twice.
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void execute() throws BuildException {
        Security sec = securityHolder.getSecurity(this);
        if (sec == null) {
            throw new BuildException(ERROR_NO_SECURITY_SETTINGS);
        }
        if(sec.isEmpty()) {
            log(MESSAGE_EMPTY_SECURITY);
        }
        try {
            sec.applySecuritySettings(signer);
        } catch (IOException e) {
            throw new BuildException(ERROR_COULD_NOT_APPLY_SETTINGS
                    +sec.toString(),e);
        }
        signer.execute();
    }
}
