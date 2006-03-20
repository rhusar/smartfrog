/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.WaitFor;
import org.apache.tools.ant.taskdefs.Available;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.UpToDate;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.Not;
import org.apache.tools.ant.taskdefs.condition.And;
import org.apache.tools.ant.taskdefs.condition.Or;
import org.apache.tools.ant.taskdefs.condition.Equals;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.condition.IsSet;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.apache.tools.ant.taskdefs.condition.Socket;
import org.apache.tools.ant.taskdefs.condition.FilesMatch;
import org.apache.tools.ant.taskdefs.condition.Contains;
import org.apache.tools.ant.taskdefs.condition.IsTrue;
import org.apache.tools.ant.taskdefs.condition.IsFalse;
import org.apache.tools.ant.taskdefs.condition.IsReference;

/**
 * A sort of extension/delegate of WaitFor with two features
 * <ol>
 * <li>Extends Task so that parallel/sequential containers can use it straight off</li>
 * <li>Throws a built exception if something failed</li>
 * </ol>
 */

public class FaultingWaitForTask extends WaitFor {

    public static final String ERROR_TIMEOUT = "Timeout while waiting for conditions to be met";
    private int maxWait;
    private int checkEvery;
    private String timeoutProperty;
    private String message=ERROR_TIMEOUT;
    public static final int DEFAULT_MAX_WAIT = 30;
    public static final int DEFAULT_CHECK_TIME = 1;

    public FaultingWaitForTask() {
        super();
        WaitFor.Unit unit = new WaitFor.Unit();
        unit.setValue("second");
        setMaxWaitUnit(unit);
        setCheckEveryUnit(unit);
        setMaxWait(DEFAULT_MAX_WAIT);
        setCheckEvery(DEFAULT_CHECK_TIME);
    }

    /**
     * Sets the project object of this component. This method is used by
     * Project when a component is added to it so that the component has
     * access to the functions of the project. It should not be used
     * for any other purpose.
     *
     * @param project Project in whose scope this component belongs.
     *                Must not be <code>null</code>.
     */
    public void setProject(Project project) {
        super.setProject(project);
    }

    /**
     * Set the maximum length of time to wait, in seconds.
     * @param time maxium time to wait for success.
     */
    public void setMaxWait(int time) {
        maxWait=time;
        super.setMaxWait(time);
    }

    public int getMaxWait() {
        return maxWait;
    }

    /**
     * Set the time between each check, in seconds.
     *
     * @param time between checks in seconds
     */
    public void setCheckEvery(int time) {
        checkEvery=time;
        super.setCheckEvery(time);
    }

    public int getCheckEvery() {
        return checkEvery;
    }

    /**
     * Name the property to set after a timeout.
     *
     * @param p the property name
     */
    public void setTimeoutProperty(String p) {
        timeoutProperty=p;
        super.setTimeoutProperty(p);
    }

    public void setMessage(String message) {
        this.message = message;
    }


    /**
     * set up our parent for running by creating a unique property.
     * and after running super.execute, check for the property being set
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build.
     */
    public void execute() throws BuildException {
        ProjectHelper helper = new ProjectHelper(getProject());

        if(timeoutProperty==null) {
            String property;
            property = helper.createUniquePropertyName();
            setTimeoutProperty(property);
        }
        log("About to wait for "+timeoutProperty+"; setting property "+timeoutProperty,Project.MSG_DEBUG);
        super.execute();
        String result = getProject().getProperty(timeoutProperty);
        if(result !=null) {
            throw new BuildException(message);
        }
    }



}
