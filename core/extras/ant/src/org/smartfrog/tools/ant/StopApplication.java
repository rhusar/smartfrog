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
 * Undeploy any smartfrog application from a running daemon.
 * By default this target raises an error when the application cannot be stopped, and has a timeout
 * set to the standard default value. {@link SmartFrogTask#DEFAULT_TIMEOUT_VALUE}.
 *
 * @ant.task category="SmartFrog" name="sf-undeploy"
 */
public class StopApplication extends SmartFrogTask {

    /**
     * name of an app
     */
    protected String application;
    public static final String ERROR_FAILED_TO_TERMINATE = "failed to terminate ";

    public StopApplication() {
    }

    public void init() throws BuildException {
        super.init();
        bindToLocalhost();
        setFailOnError(true);
    }

    protected String getApplication() {
        return application;
    }


    /**
     * set the app name; optional on some actions
     *
     * @param application application to stop
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * stop the application
     *
     * @throws org.apache.tools.ant.BuildException on trouble
     *
     */
    public void execute() throws BuildException {
        setStandardSmartfrogProperties();
        verifyApplicationName(application);
        String terminateCommand = application + ":"    //NAME
                + "TERMINATE" + ":"   //Action: DEPLOY,TERMINATE,DETACH,DETaTERM
                + "" + ":"            //URL
                + "" + ":"            // sfConfig or empty
                + getHost() + ":"          // host
                + "";              // subprocess
        addApplicationCommand("-a", terminateCommand);
        //addApplicationCommand("-t",application);
        addExitFlag();
        execSmartFrog(ERROR_FAILED_TO_TERMINATE + getApplication());
    }



}
