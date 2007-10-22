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


package org.smartfrog.tools.ant.test.system;

import org.smartfrog.tools.ant.DeployingTaskBase;
import org.smartfrog.tools.ant.StartApplication;
import org.smartfrog.tools.ant.test.TaskTestBase;

/**
 * Test deployment
 * @author steve loughran
 *         Date: 09-Mar-2004
 *         Time: 09:37:41
 */
public class DeployTest extends TaskTestBase {

    public DeployTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "deploy.xml";
    }


    /**
     * shut down anything running
     */
    public void tearDown() throws Exception {
        super.tearDown();
        executeTarget("teardown");
    }

    public void testNoParams() {
        expectBuildExceptionContaining("testNoParams", "no parameters",
                DeployingTaskBase.ERROR_NO_APPLICATIONS_DECLARED);
    }

    /**
     * failonerror is not deemed to affect the no apps configuration,
     * as that is a fundamental configuration error
     */
    public void testNoFailure() {
        expectBuildExceptionContaining("testNoFailure", "no parameters",
                DeployingTaskBase.ERROR_NO_APPLICATIONS_DECLARED);
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
/*
[sf-deploy] Unable to locate IP address of the host: no-such-hostname
[sf-deploy] java.net.UnknownHostException: no-such-hostname
*/
    public void testBadHost() {
        expectBuildExceptionContaining("testBadHost", "unknown host",
                StartApplication.ERROR_COULD_NOT_DEPLOY);
        assertInLog("Unable to locate IP address of the host: no-such-hostname");
        //assertInLog("java.net.UnknownHostException: no-such-hostname");
    }

    /**
     * Will fail with no local server.
     * [sf-deploy] SmartFrog 3.01.003_alpha
     [sf-deploy] (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP
     [sf-deploy] Warning: SmartFrog security is NOT active
     [sf-deploy] Unable to connect to sfDaemon on: localhost.
     [sf-deploy] Reason:sfDaemon may not be running on localhost
     [sf-deploy] java.rmi.ConnectException: Connection refused to host: 127.0.0.1; nested exception is:
     [sf-deploy] 	java.net.ConnectException: Connection refused: connect
     */
    public void testRunFile() {
        expectBuildExceptionContaining("testRunFile", "deploy failure", StartApplication.ERROR_COULD_NOT_DEPLOY);
        assertNoConnectionToLocalhost();
    }

    /**
     * this search string has a habit of changing.
     * It needs to work on an IPv6 box as well as an IPv4 system, so you should
     * not hard code 
     */
    private void assertNoConnectionToLocalhost() {
        //assertInLog("Unable to connect to sfDaemon on: localhost");
        //assertInLog("Reason:sfDaemon may not be running on localhost");
        assertInLog("java.rmi.ConnectException: Connection refused to host");
    }

    public void testResource() {
        expectBuildExceptionContaining("testResource", "deploy failure",
                StartApplication.ERROR_COULD_NOT_DEPLOY);
        assertNoConnectionToLocalhost();
    }

    /**
     * Successfully deployed components: [app]
     [sf-deploy] Error during deployment of URL:C:\Projects\SmartFrog\Forge\core\extras\ant\test\files\valid.sf, for component: app
     [sf-deploy] SmartFrogDeploymentException: HOST "141.20.195.89":rootProcess failed to deploy 'app' component
     [sf-deploy]    cause: SmartFrogResolutionException:: Reference not found, Unresolved Reference: sfClass
     */
    public void notestDeployFile() {
        expectBuildExceptionContaining("testDeployFile", "expected timeout", "Timeout");
        //assertInLog("rootProcess failed to deploy 'app' component");
        assertInLog("FAILED when trying DEPLOY of 'app'");
        assertInLog("Reference not found");
        assertInLog("Unresolved Reference");
    }


    public void testDeployResource() {
        expectBuildExceptionContaining("testDeployResource", "expected timeout", "Timeout");
        assertInLog("Successfully deployed");
        assertInLog("COUNTER: hello - here is a constructed message");
        assertInLog("value is 99");
        assertInLog("goodbye");
        assertInLog("[[elementA, elementB], Message from outerVector, [value is , 99]]");
        assertInLog("1");
    }

    public void testInline() {
        expectBuildExceptionContaining("testInline", "deploy failure",
                StartApplication.ERROR_COULD_NOT_DEPLOY);
        assertNoConnectionToLocalhost();
    }

    /**
     * turned off; it doesnt add more than the inline one
     */
    public void notestDeployInline() {
        expectBuildExceptionContaining("testDeployInline", "expected timeout", "Timeout");
        assertInLog("goodbye");    }
}
