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
package org.smartfrog.test.system.assertions.testcompounds;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestCompoundImpl;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class TestCompoundsEventsTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/assertions/testcompounds/";

    public TestCompoundsEventsTest(String name) {
        super(name);
    }

    public void testEmptySequence() throws Throwable {
        expectSuccessfulTestRun(FILES , "testEmptySequence");
    }

    public void testFailure() throws Throwable {
        TerminationRecord record = expectSuccessfulTestRun(FILES, "testFailure").getStatus();
        assertTerminationRecordContains(record, "failure message",null,null);
    }

    private TerminationRecord deployToAbnormalTermination(String test) throws Throwable {
        TestCompletedEvent event = expectAbnormalTestRun(FILES, test, true, null);
        return event.getStatus();
    }

    private TerminationRecord deployToNormalTermination(String test) throws Throwable {
        TestCompletedEvent event = expectSuccessfulTestRun(FILES, test);
        return event.getStatus();
    }


    public void testUnexpectedFailure() throws Throwable {
        deployToAbnormalTermination("testUnexpectedFailure");
    }

    public void testFailureWrongMessage() throws Throwable {
        expectAbnormalTestRun(FILES, 
                "testFailureWrongMessage",
                true,
                TestCompoundImpl.TEST_FAILED_WRONG_STATUS);
    }

    public void NotestFailureWrongMessageNested() throws Throwable {
        application =deployExpectingSuccess("testFailureWrongMessageNested",
                "testFailureWrongMessageNested");
    }

    public void testSmartFrogException() throws Throwable {
        TerminationRecord record = deployToNormalTermination("testSmartFrogException");
        assertTerminationRecordContains(record, null, "org.smartfrog.sfcore.common.SmartFrogException", "SFE");
    }
}
