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
 * <p/>
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
 *
 * @author steve loughran
 *         created 16-Feb-2004 16:37:26
 * @ant.task category="SmartFrog" name="sf-startdaemon"
 */

public class StartDaemon extends DeployingTaskBase {
    public static final String ERROR_FAILED_TO_START_DAEMON = "failed to start the smartfrog daemon";

    public StartDaemon() {
        setFailOnError(true);
    }

    /**
     * Set the entry point of the daemon. This defaults to that of the command line
     * @param method method to run
     */ 
    public void setEntrypoint(String method) {
        getBaseJavaTask().setClassname(method);
    }

    /**
     * Start the daemon in this thread or a new process.
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void execute() throws BuildException {
        verifyHostUndefined();
        bindToLocalhost();
        setStandardSmartfrogProperties();
        //this is needed to start the registry. Without it you cannot shut
        //smartfrog down.
        addJVMProperty(SmartFrogJVMProperties.PROCESS_NAME,
                SmartFrogJVMProperties.ROOT_PROCESS);
        addIniFile();
        deployApplications();


        execSmartFrog(ERROR_FAILED_TO_START_DAEMON);
    }


}
