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
 * system.
 * <p>
 * This task starts a daemon which is only terminated by external request, and which
 * blocks the calling thread until it terminates
 * This means the build file calling this routine must either
 * <ol>
 * <li>execute it in a separate thread (using parallel/sequential containers),
 * and call &lt;sf-stopdaemon&gt; in a separate thread to end it
 * <li>set the standalone property to true to run it in a new process.
 * <li>set the timeout to enforce a death time on the process.
 * </ol>
 * Timeout killing of a process is somewhat brutal; we do not (yet) cleanly shut
 * down the localhost, though that is a distinctly possible option in future.
 * @author steve loughran
 * created 16-Feb-2004 16:37:26
 * @ant.task category="SmartFrog" name="sf-startdaemon"
 *
 */

public class StartDaemon extends DeployingTaskBase {

    public StartDaemon() {
        setFailOnError(true);
    }

    public void init() throws BuildException {
        super.init();
        setHost("localhost");
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
     * run the process standalone, losing all output.
     * This also sets the failonerror flag to false.
     * @param spawn
     */
    public void setSpawn(boolean spawn) {
        this.spawn=spawn;
        setFailOnError(false);
    }

    /**
     *  Start the daemon in this thread or a new process.
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void execute() throws BuildException {
        setStandardSmartfrogProperties();
        //this is needed to start the registry. Without it you cannot shut
        //smartfrog down.
        addSmartfrogProperty("org.smartfrog.sfcore.processcompound.sfProcessName",
                ROOT_PROCESS);
        addIniFile();
        deployApplications();
        if(spawn) {
            smartfrog.setSpawn(spawn);
        } else {
            log("embedded smartfrog daemon started; " +
                    "this thread will block until it exits", Project.MSG_VERBOSE);
        }

        execSmartfrog("failed to start the smartfrog daemon");
        if (spawn) {
            //when spawning output gets lost, so we print something here
            log("Standalone SmartFrog daemon started");
        }
    }


}
