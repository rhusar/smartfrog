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
package org.smartfrog.tools.ant.test.system;

import org.smartfrog.tools.ant.test.TaskTestBase;

/**
 * @author steve loughran created 27-Feb-2004 16:37:55
 */

public class StartTest extends TaskTestBase {
    private static final String BAD_VALUE = "Incompatible Value";

    public StartTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "start.xml";
    }

    public void testIncompatiblePort() {
        expectBuildExceptionContaining("testIncompatiblePort", BAD_VALUE,
                ERROR_FAILED_TO_START_DAEMON);
    }

    public void testIncompatibleLivenessDelay() {
        expectBuildExceptionContaining("testIncompatibleLivenessDelay", BAD_VALUE,
                ERROR_FAILED_TO_START_DAEMON);
    }

    public void testIncompatibleLivenessFactor() {
        expectBuildExceptionContaining("testIncompatibleLivenessFactor", BAD_VALUE,
                ERROR_FAILED_TO_START_DAEMON);
    }

    public void testIncompatibleProcessAllow() {
        expectLogContaining("testIncompatibleProcessAllow", "Not allowed to create process");
    }

    public void NotestIncompatibleProcessTimeOut() {
        executeTarget("testIncompatibleProcessTimeOut");
        expectLogContaining("Not allowed to create process", "Not allowed to create process");
    }

    public void NotestIncompatibleLogLevel() {
        expectBuildExceptionContaining("testIncompatibleLogLevel", BAD_VALUE,
                ERROR_FAILED_TO_START_DAEMON);
    }

    public void testNoFailure() throws Throwable {
        executeTarget("testNoFailure");
    }

    public void testEmptyApplication() throws Throwable {
        expectBuildExceptionContaining("testEmptyApplication", "Empty application",
                ERROR_NO_APPLICATION_NAME);
    }

    public void testAnonApplication() throws Throwable {
        expectBuildExceptionContaining("testAnonApplication", "Anonymous application",
                ERROR_NO_APPLICATION_NAME);
    }

    public void testDatalessApplication() throws Throwable {
        expectBuildExceptionContaining("testDatalessApplication", "Application with no data",
                ERROR_NO_APPLICATION_DESCRIPTOR);
    }

    public void testBadFile() throws Throwable {
        expectBuildExceptionContaining("testBadFile", "deploy a nonexistent file",
                ERROR_FILE_NOT_FOUND);
    }

    public void testBadHost() throws Throwable {
        expectBuildExceptionContaining("testBadHost",
                "Set a host on a task that doesn't allow it",
                ERROR_HOST_NOT_SETTABLE);
    }

    public void testRunFile() throws Throwable {
        executeTarget("testRunFile");
    }

    public void testResource() throws Throwable {
        executeTarget("testResource");
    }


    public void testDifferentPort() {
        executeTarget("testDifferentPort");
    }

    public void testDifferentPortDeploy() {
        executeTarget("testDifferentPortDeploy");
    }

    public void testDifferentPortDeployUndeploy() {
        executeTarget("testDifferentPortDeployUndeploy");
    }

}
