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
 * An unsupported, potentially unstable derivative of waitfor that will 
 * made to fail on timeout.
 * 
 * This task is used for SmartFrog's internal testing purposes, and for 
 * testing components. There are no guarantees of stability over time. 
 * 
 * <p/>

 * An extension/delegate of WaitFor with two features
 * <ol>
 * <li>Extends Task so that parallel/sequential containers can use it straight off</li>
 * <li>Throws a built exception if something failed</li>
 * </ol>
 * The class delegates rather than extends WaitFor for ease of insertion into a sequence, though
 * using an ant.TaskAdapter would fix that. It also limits the number of things you can set
 * directly with it to those in Ant1.6. To use later stuff you need to declare the
 * ant1.7+ conditions using the xmlns:c="antlib:org.apache.tools.ant.tasks.conditions" antlib
 * declaration, then insert conditions in their new namespace.
 *  @ant.task category="SmartFrog" name="sf-faultingwaitfor"
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
     * @param c condition
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
     * @throws BuildException if something goes wrong with the build.
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

    /**
     * Add an &lt;available&gt; condition.
     *
     * @param a an available condition
     *
     * @since 1.1
     */
    public void addAvailable(Available a) {
        waitFor.add(a);
    }

    /**
     * Add an &lt;checksum&gt; condition.
     *
     * @param c a Checksum condition
     *
     * @since 1.4, Ant 1.5
     */
    public void addChecksum(Checksum c) {
        waitFor.add(c);
    }

    /**
     * Add an &lt;uptodate&gt; condition.
     *
     * @param u an UpToDate condition
     *
     * @since 1.1
     */
    public void addUptodate(UpToDate u) {
        waitFor.add(u);
    }

    /**
     * Add an &lt;not&gt; condition "container".
     *
     * @param n a Not condition
     *
     * @since 1.1
     */
    public void addNot(Not n) {
        waitFor.add(n);
    }

    /**
     * Add an &lt;and&gt; condition "container".
     *
     * @param a an And condition
     *
     * @since 1.1
     */
    public void addAnd(And a) {
        waitFor.add(a);
    }

    /**
     * Add an &lt;or&gt; condition "container".
     *
     * @param o an Or condition
     *
     * @since 1.1
     */
    public void addOr(Or o) {
        waitFor.add(o);
    }

    /**
     * Add an &lt;equals&gt; condition.
     *
     * @param e an Equals condition
     *
     * @since 1.1
     */
    public void addEquals(Equals e) {
        waitFor.add(e);
    }

    /**
     * Add an &lt;os&gt; condition.
     *
     * @param o an Os condition
     *
     * @since 1.1
     */
    public void addOs(Os o) {
        waitFor.add(o);
    }

    /**
     * Add an &lt;isset&gt; condition.
     *
     * @param i an IsSet condition
     *
     * @since Ant 1.5
     */
    public void addIsSet(IsSet i) {
        waitFor.add(i);
    }

    /**
     * Add an &lt;http&gt; condition.
     *
     * @param h an Http condition
     *
     * @since Ant 1.5
     */
    public void addHttp(Http h) {
        waitFor.add(h);
    }

    /**
     * Add a &lt;socket&gt; condition.
     *
     * @param s a Socket condition
     *
     * @since Ant 1.5
     */
    public void addSocket(Socket s) {
        waitFor.add(s);
    }

    /**
     * Add a &lt;filesmatch&gt; condition.
     *
     * @param test a FilesMatch condition
     *
     * @since Ant 1.5
     */
    public void addFilesMatch(FilesMatch test) {
        waitFor.add(test);
    }

    /**
     * Add a &lt;contains&gt; condition.
     *
     * @param test a Contains condition
     *
     * @since Ant 1.5
     */
    public void addContains(Contains test) {
        waitFor.add(test);
    }

    /**
     * Add a &lt;istrue&gt; condition.
     *
     * @param test an IsTrue condition
     *
     * @since Ant 1.5
     */
    public void addIsTrue(IsTrue test) {
        waitFor.add(test);
    }

    /**
     * Add a &lt;isfalse&gt; condition.
     *
     * @param test an IsFalse condition
     *
     * @since Ant 1.5
     */
    public void addIsFalse(IsFalse test) {
        waitFor.add(test);
    }

    /**
     * Add an &lt;isreference&gt; condition.
     *
     * @param i an IsReference condition
     *
     * @since Ant 1.6
     */
    public void addIsReference(IsReference i) {
        waitFor.add(i);
    }


}
