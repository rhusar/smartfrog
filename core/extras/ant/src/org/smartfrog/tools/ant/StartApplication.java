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
package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;

/**
 * Start an SF application, an application which will run asynchronously until stopped by another mechanism.
 *
 * A smartfrog daemon must already be running on the target machine/port
 *
 * @ant.task category="SmartFrog" name="sf-deploy" By default this target raises an error when the application cannot be
 * stopped, and has a timeout set to the standard default value. {@link SmartFrogTask#DEFAULT_TIMEOUT_VALUE}. <p/>
 * <i>Important</i>. The codebase set for this task in the codebase elements define the codebase for interpreting the
 * deployment descriptor, but as the action of deployment is left to a daemon, JAR files and referenced in the codebase
 * are loaded by the daemon during deployment. <p/> To ensure that the daemon can actually load the files, you need to
 * <ol> <li>Place them in a shared location (shared file system may work) <li>Place URLs to the shared files in the
 * smartfrog deployment descriptor's <tt>sfCodebase</tt> attribute. The &lt;sf-tourl&gt; task can be used to create a
 * suitable URL from a file reference.
 */
public class StartApplication extends DeployingTaskBase {
    public static final String ERROR_COULD_NOT_DEPLOY = "Could not deploy";

    public void init() throws BuildException {
        super.init();
        setFailOnError(true);
        bindToLocalhost();
    }

    /**
     * starting smartfrog
     *
     * @throws BuildException on failure
     */
    public void execute() throws BuildException {
        setStandardSmartfrogProperties();
        enableFailOnError();
        checkApplicationsDeclared();
        deployApplications();
        addExitFlag();
        execSmartFrog(ERROR_COULD_NOT_DEPLOY);
    }


}
