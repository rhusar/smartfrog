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
 * Sign a jar file(s)
 * Date: 19-Apr-2004
 * Time: 16:49:50
 */
public class SmartFrogSign extends TaskBase {


    /**
     * the task used for signing
     */
    SignJar signer;

    /**
     * our security holder
     */
    private SecurityHolder securityHolder = new SecurityHolder();



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
    public void setFile(File file) {
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
            throw new BuildException("No security settings provided");
        }
        try {
            sec.applySecuritySettings(signer);
        } catch (IOException e) {
            throw new BuildException("Could not apply security settings with "
                    +sec.toString(),e);
        }
        signer.execute();
    }
}
