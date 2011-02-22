/* (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
import org.smartfrog.services.xunit.listeners.BufferingListener;
import org.smartfrog.services.xunit.serial.Statistics;

/**

 */
public class SyspropsTest extends TestRunnerTestBase {

    public SyspropsTest(String name) {
        super(name);
    }

    public void testSyspropsWorking() throws Throwable {
        String url;
        url = "/files/junit-sysprops.sf";

        int seconds = getTimeout();
        application = deployExpectingSuccess(url, "localhostTest");
        TestRunner runner =  getTestRunner();
        BufferingListener listener = null;
        listener =
                (BufferingListener) application.sfResolve(TestRunner.ATTR_LISTENER,
                        listener,
                        true);
        boolean finished = spinTillFinished(runner, seconds);
        assertTrue("Test run timed out", finished);
        assertEquals("session started", 1,
                listener.getSessionStartCount());
        assertEquals("session ended", 1,
                listener.getSessionEndCount());
        Statistics statistics = runner.getStatistics();
        System.out.println(statistics.toString());
        assertTrue("testsWereSuccessful() is false", listener.testsWereSuccessful());
        assertEquals("statistics.errors!=0 -is " + statistics.getErrors(), 0, statistics.getErrors());
        assertEquals("statistics.failures!=0",
                0,
                statistics.getFailures());

    }

}
