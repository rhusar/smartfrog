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

/**
 * Ant task to stop a smartfrog daemon on a host
 * As the daemon is just another process, this is implemented as a call to shut down the application
 * named 'rootProcess'.
 * By default this target does not raise an error when the daemon cannot be stopped,
 * as the usual cause of this is 'there is no daemon', which is the state
 * we are seeking.
 * @author steve loughran
 */
public class StopDaemon extends SmartFrogTask {

    public StopDaemon() {
        setApplicationName("rootProcess");
        setHostname("localhost");
    }

    /**
     * execution logic
     *
     * @throws org.apache.tools.ant.BuildException
     *
     */
    public void execute() throws BuildException {
        setStandardSmartfrogProperties();
        addHostname();
        addApplicationName("-t");
        addExitFlag();
        setFailOnError(false);
        execSmartfrog("failed to terminate " + getApplicationName());
    }

    /**
     * get the title string used to name a task
     *
     * @return the name of the task
     */
    protected String getTaskTitle() {
        return "sf-stopdaemon";
    }
}
