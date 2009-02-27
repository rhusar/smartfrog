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

import org.apache.tools.ant.BuildFileTest;
import org.smartfrog.tools.ant.DeployingTaskBase;
import org.smartfrog.tools.ant.SmartFrogTask;
import org.smartfrog.tools.ant.StartApplication;
import org.smartfrog.tools.ant.StartDaemon;

/**
 * this is a base class for the smartfrog ant tasks. Each test case instantiates an ant file and runs targets. All the
 * core logic to do that is in the Ant-testutils.jar contained parent classes. This base class just extracts a helper
 * directory
 */
public abstract class TaskTestBase extends BuildFileTest {
    public static final String ERROR_NO_APPLICATION_NAME
            = DeployingTaskBase.Application.ERROR_NO_APPLICATION_NAME;
    public static final String ERROR_FILE_NOT_FOUND = DeployingTaskBase.Application.ERROR_FILE_NOT_FOUND;
    public static final String ERROR_NO_APPLICATION_DESCRIPTOR
            = DeployingTaskBase.Application.ERROR_NO_APPLICATION_DESCRIPTOR;
    public static final String ERROR_HOST_NOT_SETTABLE = SmartFrogTask.ERROR_HOST_NOT_SETTABLE;
    public static final String ERROR_NO_APPLICATIONS_DECLARED = DeployingTaskBase.ERROR_NO_APPLICATIONS_DECLARED;
    public static final String ERROR_COULD_NOT_DEPLOY = StartApplication.ERROR_COULD_NOT_DEPLOY;
    public static final String ERROR_FAILED_TO_START_DAEMON = StartDaemon.ERROR_FAILED_TO_START_DAEMON;
    protected static final String CONNECTION_REFUSED = "Connection refused to host";

    public TaskTestBase(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected abstract String getBuildFile();

    /**
     * get a mandatory property for the test,
     *
     * @param property
     * @return
     * @throws RuntimeException if the property was not found
     */
    public final String getRequiredTestProperty(String property) {
        String result = System.getProperty(property, null);
        if (result == null) {
            throw new RuntimeException("Property " + property + " was not set");
        }
        return result;
    }

    public void setUp() throws Exception {
        super.setUp();
        String basedir = getRequiredTestProperty("test.files.dir");
        String filename = getBuildFile();
        configureProject(basedir + "/" + filename);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * assert that a property is null.
     *
     * @param property property name
     */
    @Override
    public void assertPropertyUnset(String property) {
        String result = project.getProperty(property);
        if (result != null) {
            fail("Expected property " + property
                    + " to be unset, but it is set to the value: " + result);
        }
    }


    /**
     * assert that some text is not in the log
     *
     * @param text
     */
    public void assertNotInLog(String text) {
        String log = getLog();
        boolean found = log.indexOf(text) >= 0;
        if (found) {
            System.out.print(log);
            System.out.flush();
            fail("Did not want to find: " + text);
        }
    }

    /**
     * Assert that the given substring is in the log messages
     */

    protected void assertInLog(String text) {
        String log = getLog();
        boolean found = log.indexOf(text) >= 0;
        if (!found) {
            System.out.println("not found:");
            System.out.println(text);
            System.out.println(" log was:-");
            System.out.print(log);
            System.out.flush();
            String errorText = "not found: \"" + text + "\" log was \""
                    + log + "\"";
            fail(errorText);
        }
    }

    /**
     * for overrides
     *
     * @param text
     */
    public void assertLogContaining(String text) {
        assertInLog(text);
    }

    /**
     * expect an exception with the text in the log
     *
     * @param target
     * @param log
     * @param cause
     */
    public void expectExceptionWithLogContaining(String target, String log,
                                                 String cause) {
        expectBuildException(target, cause);
        assertInLog(log);
    }

    public void expectExceptionWithLogContaining(String target, String log) {
        expectExceptionWithLogContaining(target, log, target);
    }

    public void assertRootProcessInLog() {
        assertInLog("SmartFrog ready");
        //assertProcessInLog("rootProcess");
    }

    protected void expectDeployed(String target, String appname) {
        executeTarget(target);
        assertProcessInLog(appname);
    }

    private void assertProcessInLog(String appname) {
        assertInLog(":sfRunProcess:" + appname);
    }

    /**
     * this test is used by both run and deploy tests, where the failure strings are slightly different, "Could not run"
     * vs "Could not deploy". So when testing, we only check a bit of the string
     *
     * @param target
     */
    protected void assertDeployFailsWithUnresolvedReference(String target) {
        expectBuildExceptionContaining(target, "deploy failure", "Could not");
        assertInLog("Unresolved Reference");
    }

    /**
     * assert that a property contains a string
     *
     * @param property name of property to look for
     * @param contains what to search for in the string
     */
    protected void assertPropertyContains(String property, String contains) {
        String result = project.getProperty(property);
        assertTrue("expected " + contains + " in " + result,
                result != null && result.contains(contains));
    }

    /**
     * get a property from the project
     *
     * @param property ant property
     * @return the property value (may be null)
     */
    protected String getProperty(String property) {
        return project.getProperty(property);
    }

    /**
     * assert that a property ends with a value
     *
     * @param property property name
     * @param ending   ending
     */
    protected void assertPropertyEndsWith(String property, String ending) {
        String result = getProperty(property);
        String substring = result.substring(result.length() - ending.length());
        assertEquals(ending, substring);
    }
}