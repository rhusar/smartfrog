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
 * By default this target raises an error when the application cannot be stopped, and has a timeout
 * set to the standard default value. {@link SmartFrogTask#DEFAULT_TIMEOUT_VALUE}.
 *
 * @author steve loughran
 * @ant.task category="SmartFrog" name="sf-stopdaemon"
 */
public class StopDaemon extends SmartFrogTask {
    public static final String ERROR_FAILED_TO_TERMINATE = "failed to terminate ";

    public StopDaemon() {
    }

    public void init() throws BuildException {
        super.init();
        bindToLocalhost();
        setFailOnError(true);
    }

    /**
     * stop the daemon
     * @throws org.apache.tools.ant.BuildException
     *
     */
    public void execute() throws BuildException {
        setStandardSmartfrogProperties();
        verifyHostDefined();
        String terminateCommand = SmartFrogJVMProperties.ROOT_PROCESS + ":TERMINATE:::" + getHost() + ":";
        addApplicationCommand("-a", terminateCommand);
        addExitFlag();
        execSmartFrog(ERROR_FAILED_TO_TERMINATE + SmartFrogJVMProperties.ROOT_PROCESS);
    }


}
