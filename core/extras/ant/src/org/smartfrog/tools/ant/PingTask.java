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
 * Probe the health of a deployed application.
 * This task will trigger a liveness check against the application, which
 * causes the application to assess its own health and return successfully
 * or raise an exception.
 * The build file will stop on failure (default), or the task can be set
 * to ignore the failure, and instead set a property on success, leaving it
 * blank on failure. 
 * @ant.task category="SmartFrog" name="sf-ping"
 */
public class PingTask extends SmartFrogTask {
    /**
     * name of an app
     */
    protected String application= SmartFrogJVMProperties.ROOT_PROCESS ;

    protected String successProperty;

    public static final String ERROR_FAILED_TO_PING = "failed to ping";

    public void init() throws BuildException {
        super.init();
        bindToLocalhost();
        setFailOnError(true);
    }

    protected String getApplication() {
        return application;
    }


    /**
     * set the name of the application to ping. Required
     *
     * @param application application to ping
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * the name of a property to set on a successful ping.
     * Only useful if failonerror is set to false
     * @param successProperty true on success
     */
    public void setSuccessProperty(String successProperty) {
        this.successProperty = successProperty;
    }

    /**
     * execution logic
     *
     * @throws org.apache.tools.ant.BuildException
     *
     */
    public void execute() throws BuildException {
        setStandardSmartfrogProperties();
        verifyApplicationName(application);
        String command = application + ":"    //NAME
                + "PING" + ":"
                + "" + ":"            //URL
                + "" + ":"            // sfConfig or empty
                + getHost() + ":"          // host
                + "";              // subprocess
        addApplicationCommand("-a", command);
        addExitFlag();
        if(execSmartFrog(ERROR_FAILED_TO_PING + getApplication())){
            if(successProperty!=null) {
                getProject().setNewProperty(successProperty,"true");
            }
        }

    }
}
