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
                "No applications declared");
    }
    /**
     * failonerror is not deemed to affect the no apps configuration,
     * as that is a fundamental configuration error
     */
    public void testNoFailure() {
        expectBuildExceptionContaining("testNoFailure", "no parameters",
                "No applications declared");
    }

    public void testEmptyApplication() {
        expectBuildExceptionContaining("testEmptyApplication", "anon app",
                "no application name");
    }

    public void testAnonApplication() {
        expectBuildExceptionContaining("testAnonApplication", "anon app",
                "no application name");
    }

    public void testDatalessApplication() {
        expectBuildExceptionContaining("testDatalessApplication", "no descriptor",
                "no descriptor provided");
    }

    public void testBadFile() {
        expectBuildExceptionContaining("testBadFile", "missing file",
                "missing-file.sf does not exist");
    }


    public void testBadHost() {
        expectBuildExceptionContaining("testBadHost", "host parameter",
                "host cannot be set on this task");
    }

    public void testRunFile(){
        expectBuildExceptionContaining("testRunFile","deploy failure","Could not run");
        assertLogContaining("Reference not found, Unresolved Reference: sfClass");
    }

    public void testInline() {
        expectBuildExceptionContaining("testInline", "deploy failure", "Could not run");
        assertLogContaining("Reference not found, Unresolved Reference: sfClass");
    }
    
    public void testResource() {
        executeTarget("testResource");
        assertDeployed("app");
        assertLogContaining("app");
        assertLogContaining("COUNTER: hello - here is a constructed message");
        assertLogContaining("value is 99");
        assertLogContaining("goodbye");
        assertLogContaining("[[elementA, elementB], Message from outerVector, [value is , 99]]");
        assertLogContaining("1");
    }

    /**
     * test that turning stack tracing on generates a log when we deploy something invalid
     */
    public void testStackTrace() {
        expectBuildExceptionContaining("testStackTrace", "deploy failure",
                "Could not run");
        assertLogContaining("Warning: stack trace logging enabled");
        assertLogContaining("at org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException.typeResolution");
    }


    /**
     * sub directory file references must also work
     */
    public void testSubdir() {
        expectBuildExceptionContaining("testSubdir", "deploy failure",
                "Could not run");
        assertLogContaining("Reference not found, Unresolved Reference: sfClass");
    }

    /**
     * assertions get passed down
     */
    public void testAssertions() {
        executeTarget("testAssertions");
        assertLogContaining("Successfully deployed components: [app]");
        assertLogContaining("goodbye");
    }
}
