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
package org.smartfrog.tools.ant.test;

import org.apache.tools.ant.BuildException;
import org.smartfrog.tools.ant.PropertyFile;
import org.smartfrog.tools.ant.DeployingTaskBase;
import org.smartfrog.tools.ant.SmartFrogTask;

/**
 * This test tests the daemon starting.
 * This is quite a functional test, as it runs new processes and things, which
 * is why there is complexity in cleanup. Even so, there is non-zero
 * risk that bad things are happening, so we test for that, too.
 * @author steve loughran
 *         created 01-Mar-2004 16:19:21
 */

public class StartTest extends TaskTestBase {

    /**
     * constructor
     * @param s
     */
    public StartTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "start.xml" ;
    }

    /**
     * shut down anything running
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        executeTarget("teardown");
    }

    /**
     * test for bad settings
     */
    public void testIncompatibleSettings() {
        executeTarget("testIncompatibleSettings");
        assertInLog(SmartFrogTask.MESSAGE_SPAWNED_DAEMON);
        assertInLog("SmartFrog daemon terminated");
    }
    /**
     * test for a improper default.ini properties values
     */


	public void testIncompatiblePort() 
	{
		 expectExceptionWithLogContaining("testIncompatiblePort", "Wrong object for sfRootLocatorPort");
	}
	
	public void testIncompatibleLivenessDelay() 
	{
		expectExceptionWithLogContaining("testIncompatibleLivenessDelay", "SmartFrog [rootProcess] dead");
	}
	public void testIncompatibleLivenessFactor() 
	{
		expectExceptionWithLogContaining("testIncompatibleLivenessFactor", "SmartFrog [rootProcess] dead");
	}
	public void testIncompatibleLogStackTraces() 
	{
		expectExceptionWithLogContaining("testIncompatibleLogStackTraces", "SmartFrog [rootProcess] dead");
	}
	public void testIncompatibleProcessAllow() 
	{
		expectExceptionWithLogContaining("testIncompatibleProcessAllow", "SmartFrog [rootProcess] dead");
	}
	public void testIncompatibleProcessTimeout() 
	{
		expectExceptionWithLogContaining("testIncompatibleProcessTimeOut", "SmartFrog [rootProcess] dead");
	}
	public void testIncompatibleLoggerClass() 
	{
		expectExceptionWithLogContaining("testIncompatibleLoggerClass", "SmartFrog [rootProcess] dead");
	}
	public void testIncompatibleLogLevel() 
	{
		expectExceptionWithLogContaining("testIncompatibleLogLevel", "SmartFrog [rootProcess] dead");
	}
	
	


    /**
     * test for a bad hostname raising an error
     */
    public void testBadHost() {
        expectBuildExceptionContaining("testBadHost", "host parameter",
            SmartFrogTask.ERROR_HOST_NOT_SETTABLE);
    }

    public void testDefaults() {
        expectBuildExceptionContaining("testDefaults", "timeout",
                "Timeout:");
        assertRootProcessInLog();
        assertInLog("COUNTER: step 1");
    }

    /**
     * Look for this trace
     * [sf-startdaemon] Standalone SmartFrog daemon started
     [sf-stopdaemon] SmartFrog 3.01.003_alpha
     [sf-stopdaemon] (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP
     [sf-stopdaemon] Warning: SmartFrog security is NOT active
     [sf-stopdaemon] SmartFrog daemon terminated


     */
    public void testSpawn() {
        executeTarget("testSpawn");
        assertInLog(SmartFrogTask.MESSAGE_SPAWNED_DAEMON);
        assertInLog("SmartFrog daemon terminated");
    }



    public void testEmptyApplication() {
        expectBuildExceptionContaining("testEmptyApplication", "anon app",
                DeployingTaskBase.Application.ERROR_NO_APPLICATION_NAME);

    }

    public void testAnonApplication() {
        expectBuildExceptionContaining("testAnonApplication", "anon app",
                DeployingTaskBase.Application.ERROR_NO_APPLICATION_NAME);
    }

    public void testDatalessApplication() {
        expectBuildExceptionContaining("testDatalessApplication", "no descriptor",
                DeployingTaskBase.Application.ERROR_NO_APPLICATION_DESCRIPTOR);
    }

    public void testBadFile() {
        expectBuildExceptionContaining("testBadFile", "missing file",
                DeployingTaskBase.Application.ERROR_FILE_NOT_FOUND);
    }

    public void testRunFile() {
        executeTarget("testRunFile");
        assertInLog("FAILED when trying DEPLOY of 'app'");
        assertInLog("Reference not found");
        assertInLog("Unresolved Reference: HERE sfClass");
        assertInLog("SmartFrog [rootProcess] dead");
    }

    public void testResource() {
        executeTarget("testResource");
        assertInLog("All these moments will be lost in time, like tears in rain.");
        assertTerminationInLog();
    }

    private void assertTerminationInLog() {
        assertInLog("SmartFrog [rootProcess] dead");
    }


}
