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
 */
public class RunApplicationTask extends DeployingTaskBase {

    public RunApplicationTask() {
    }

    public void init() throws BuildException {
        super.init();
        setFailOnError(true);
    }

    /**
     * get the title string used to name a task
     *
     * @return the name of the task
     */
    protected String getTaskTitle() {
        return "sf-run";
    }

    /**
     * run a task
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void execute() throws BuildException {
        checkNoHost();
        setStandardSmartfrogProperties();
        enableFailOnError();
        checkApplicationsDeclared();
        deployApplications();
        addExitFlag();
        execSmartfrog("Could not run");
    }


}
