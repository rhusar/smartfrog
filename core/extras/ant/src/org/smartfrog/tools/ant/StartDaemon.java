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
import org.apache.tools.ant.Project;

/**
 * Start a daemon. There is some fun here, as this can be spawning or non-spawning.
 * if it spawns, it outlives ant (which doesnt block), but output gets lost.
 * This task only works on Ant1.6 or later, as that was when spawning was added to the
 * system
 * @author steve loughran
 * created 16-Feb-2004 16:37:26
 */

public class StartDaemon extends SmartFrogTask {

    public StartDaemon() {
        setFailOnError(true);
    }

    /**
     * get the title string used to name a task
     *
     * @return the name of the task
     */
    protected String getTaskTitle() {
        return "sf-startdaemon";
    }

    /**
     * spawn flag, false by default.
     */
    protected boolean spawn;

    /**
     * enable spawning. This also sets the failonerror flag to false.
     * @param spawn
     */
    public void setStandalone(boolean spawn) {
        this.spawn=spawn;
        setFailOnError(false);
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
        setStandardSmartfrogProperties();
        addSmartfrogProperty("org.smartfrog.sfcore.processcompound.sfProcessName",
                "rootProcess");
        addIniFile();
        addHostname();
        addApplicationName("-t");
        if(spawn) {
            smartfrog.setSpawn(spawn);
        }

        execSmartfrog("failed to start smartfrog daemon");
        if (spawn) {
            //when spawning output gets lost, so we print something here
            log("Smartfrog daemon spawned",Project.MSG_VERBOSE);
        }
    }


}
