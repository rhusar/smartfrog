/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.assertions.testblock;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlockImpl;
import org.smartfrog.sfcore.workflow.events.LifecycleEvent;

/**
 * Test that the test block works using event notifications, rather than spinning.
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class TestBlockEventsTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/assertions/testblock/";



    public TestBlockEventsTest(String name) {
        super(name);
    }


    public void testEmptySequence() throws Throwable {
        runTestsToCompletion(FILES, "testSequence");
    }

    public void testRun() throws Throwable {
        runTestsToCompletion(FILES, "testRun");
    }

    public void testFailure() throws Throwable {
        LifecycleEvent event=runTestDeployment(FILES, "testFailure");
        assertTestRunFailed(event, true,"failure message");
    }

    public void testSmartFrogException() throws Throwable {
        LifecycleEvent event = runTestDeployment(FILES, "testSmartFrogException");
        assertTestRunFailed(event,true,TestBlockImpl.ERROR_STARTUP_FAILURE);
    }

}
