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
import org.apache.tools.ant.taskdefs.WaitFor;
import org.apache.tools.ant.taskdefs.condition.Condition;

/**
 * A sort of extension/delegate of WaitFor with two features
 * <ol>
 * <li>Extends Task so that parallel/sequential containers can use it straight off</li>
 * <li>Throws a built exception if something failed</li>
 * <li>
 */

public class FaultingWaitForTask extends Task {

    private WaitFor waitFor = new WaitFor();
    private TaskHelper helper = new TaskHelper(this);
    public static final String ERROR_TIMEOUT = "Timeout while waiting for conditions to be met";
    private int maxWait;
    private int checkEvery;
    private String message=ERROR_TIMEOUT;
    public static final int DEFAULT_MAX_WAIT = 30;
    public static final int DEFAULT_CHECK_TIME = 1;

    public FaultingWaitForTask() {
        WaitFor.Unit unit = new WaitFor.Unit();
        unit.setValue("second");
        waitFor.setMaxWaitUnit(unit);
        waitFor.setCheckEveryUnit(unit);
        setMaxWait(DEFAULT_MAX_WAIT);
        setCheckEvery(DEFAULT_CHECK_TIME);
    }

    /**
     * Called by the project to let the task initialize properly. The default
     * implementation is a no-op.
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build
     */
    public void init() throws BuildException {
        super.init();

    }

    /**
     * Set the maximum length of time to wait, in seconds.
     * @param time maxium time to wait for success.
     */
    public void setMaxWait(int time) {
        maxWait=time;
        waitFor.setMaxWait(time);
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
        waitFor.setCheckEvery(time);
    }

    public int getCheckEvery() {
        return checkEvery;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Add an arbitrary condition
     */
    public void add(Condition c) {
        waitFor.add(c);
    }

    /**
     * Called by the project to let the task do its work. This method may be
     * called more than once, if the task is invoked more than once. For
     * example, if target1 and target2 both depend on target3, then running "ant
     * target1 target2" will run all tasks in target3 twice.
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build.
     */
    public void execute() throws BuildException {
        String property = helper.createUniquePropertyName();
        waitFor.setProject(getProject());
        waitFor.setTimeoutProperty(property);
        waitFor.execute();
        if(getProject().getProperty(property)!=null) {
            throw new BuildException(message);
        }
    }

}
