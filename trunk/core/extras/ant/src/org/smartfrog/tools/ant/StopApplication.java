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
 * unload any smartfrog application
 */
public class StopApplication extends SmartFrogTask {

    /**
     * name of an app
     */
    protected String application;

    public StopApplication() {
        setHost("localhost");
        setFailOnError(true);
    }

    protected String getApplication() {
        return application;
    }


    /**
     * set the app name; optional on some actions
     *
     * @param application
     */
    public void setApplication(String application) {
        this.application = application;
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
        String terminateCommand = application+":"    //NAME
              +"TERMINATE"+":"   //Action: DEPLOY,TERMINATE,DETACH,DETaTERM
              +""+":"            //URL
              +""+":"            // sfConfig or empty
              +getHost()+":"          // host
              +"";              // subprocess
        addApplicationCommand("-a", terminateCommand);
        //addApplicationCommand("-t",application);
        addExitFlag();
        execSmartfrog("failed to terminate "+getApplication());
    }

    /**
     * get the title string used to name a task
     *
     * @return the name of the task
     */
    protected String getTaskTitle() {
        return "sf-undeploy";
    }

}
