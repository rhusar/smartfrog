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
 * Run an application by deploying it locally; only return from ant after it has finished.
 * The default timeout of this task is zero; it only exits when finished. It does
 * have a failonerror value of true; any failure to run is an error.
 *
 * @ant.task category="SmartFrog" name="sf-run"
 */
public class RunApplicationTask extends DeployingTaskBase {
    public static final String ERROR_COULD_NOT_RUN = "Could not run";

    public RunApplicationTask() {
    }

    /**
     * Initialise, set the failonerror flag and turn the timeout off
     * @throws BuildException
     */
    public void init() throws BuildException {
        super.init();
        setFailOnError(true);
        setTimeout(0);
    }


    /**
     * run a task
     *
     * @throws BuildException if something goes wrong with the build
     */
    public void execute() throws BuildException {
        verifyHostUndefined();
        setHost("");
        setStandardSmartfrogProperties();
        enableFailOnError();
        checkApplicationsDeclared();
        deployApplications();
        addExitFlag();
        execSmartFrog(ERROR_COULD_NOT_RUN);
    }


}
