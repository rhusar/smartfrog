/* (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.junit.test.system;

import org.smartfrog.services.xunit.base.TestRunner;
import org.smartfrog.services.xunit.listeners.ConsoleListenerFactory;

/** created Nov 22, 2004 4:45:26 PM */

public class DeployedConsoleListenerTest extends TestRunnerTestBase {

    public static final String PACKAGE = "/files/";
    public static final String FILENAME = "console-all";
    public static final String FILES_CONSOLE_ALL = PACKAGE
            + FILENAME;

    public DeployedConsoleListenerTest(String name) {
        super(name);
    }

    public void testSuccess() throws Throwable {
        String url;
        url = FILES_CONSOLE_ALL + ".sf";

        int seconds = getTimeout();
        application = deployExpectingSuccess(url, "ConsoleTest");
        TestRunner runner = (TestRunner) application;
        assertTrue(runner != null);
        ConsoleListenerFactory listener = null;
        listener =
                (ConsoleListenerFactory) application.sfResolve(
                        TestRunner.ATTR_LISTENER,
                        listener,
                        true);
        assertNotNull(listener);
        boolean finished = spinTillFinished(runner, seconds);
        assertTrue("Test run timed out", finished);

    }

    public void testConsoleListener() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, FILENAME);
    }
}
