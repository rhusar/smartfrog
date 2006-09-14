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

import org.smartfrog.tools.ant.DeployingTaskBase;
import org.smartfrog.tools.ant.PropertyFile;
import org.smartfrog.tools.ant.test.TaskTestBase;

/**
 * @author steve loughran
 *         created 27-Feb-2004 16:37:55
 */

public class RunTest extends TaskTestBase {

    public RunTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "run.xml";
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


    public void testBadHost() {
        expectBuildExceptionContaining("testBadHost", "host parameter",
                "host cannot be set on this task");
    }

    public void testRunFile(){
        assertDeployFailsWithUnresolvedReference("testRunFile");
    }

    public void testInline() {
        assertDeployFailsWithUnresolvedReference("testInline");
    }
    
    public void testResource() {
        expectDeployed("testResource","app");
        assertInLog("COUNTER: hello - here is a constructed message");
        assertInLog("value is 99");
        assertInLog("goodbye");
        assertInLog("[[elementA, elementB], Message from outerVector, [value is , 99]]");
        assertInLog("1");
    }

    /**
     * test that turning stack tracing on generates a log when we deploy something invalid
     */
    public void testStackTrace() {
        expectBuildExceptionContaining("testStackTrace", "deploy failure",
                "Could not run");
        assertInLog("Warning: stack trace logging enabled");
        assertInLog("SmartFrogTypeResolutionException:: , data: [something_that_isntdefined in: HERE sfConfig cause: Reference not found");
    }


    /**
     * sub directory file references must also work
     */
    public void testSubdir() {
        String target = "testSubdir";
        assertDeployFailsWithUnresolvedReference(target);
    }

    /**
     * assertions get passed down
     */
    public void testAssertions() {
        expectDeployed("testAssertions","app");
        assertInLog("goodbye");
    }

    public void testEmptyPropertyFile() {
        expectBuildExceptionContaining("testEmptyPropertyFile",
                "empty propertyFile",
                PropertyFile.ERROR_NO_FILE_ATTRIBUTE);
    }

    public void testMissingNonOptionalPropertyFile() {
        expectBuildExceptionContaining("testMissingNonOptionalPropertyFile",
                "empty propertyFile",
                PropertyFile.ERROR_FILE_NOT_FOUND);
    }

    public void testMissingOptionalPropertyFile() {
        expectDeployed("testMissingOptionalPropertyFile", "testMissingOptionalPropertyFile");
    }

    public void testValidPropertyFile() {
        expectDeployed("testValidPropertyFile","EqualsTest");
    }


}
