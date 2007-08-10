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
import org.smartfrog.services.assertions.TestCompound;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class TestCompoundsEventsTest extends DeployingTestBase {

    public static final String FILES = "org/smartfrog/test/system/assertions/testcompounds/";

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

    public void testUnexpectedFailure() throws Throwable {
        TestCompletedEvent event = expectAbnormalTestRun(FILES, "testUnexpectedFailure",
                true, 
                "FailAbnormal Error Message");
        event.getStatus();
    }

    public void testFailureWrongMessage() throws Throwable {
        expectAbnormalTestRun(FILES, 
                "testFailureWrongMessage",
                true,
                TestCompoundImpl.TEST_FAILED_WRONG_STATUS);
    }

    public void testFailureNested() throws Throwable {
        TestCompletedEvent event = runTestsToCompletion(FILES, "testFailureNested");
        TerminationRecord status = event.getStatus();
    }

    public void testSkipped() throws Throwable {
        TestCompletedEvent event = runTestsToCompletion(FILES, "testSkipped");
        assertTrue("event is not skipped :"+event,event.isSkipped());
    }

    public void testNotSkipped() throws Throwable {
        TestCompletedEvent event = expectSuccessfulTestRun(FILES, "testNotSkipped");
        assertFalse("event was skipped :" + event, event.isSkipped());
    }

    public void testExportNotRequired() throws Throwable {
        TestCompletedEvent event = expectSuccessfulTestRun(FILES, "testExportNotRequired");
    }
    
    public void testSmartFrogExceptionActionRecord() throws Throwable {
        TestCompletedEvent event = expectSuccessfulTestRun(FILES, "testSmartFrogException");
        TestCompound tc=(TestCompound) getApplication();
        assertSFERecord(tc.getActionTerminationRecord(),event,"TestCompound.getActionTerminationRecord()");
    }


    public void testSmartFrogExceptionStatusAttribute() throws Throwable {
        TestCompletedEvent event = expectSuccessfulTestRun(FILES, "testSmartFrogException");
        TerminationRecord record = event.getStatus();
        assertSFERecord(record,event,"event status");
        TestCompound tc = (TestCompound) getApplication();
        assertSFERecord(tc.getStatus(),event,"application.getStatus()");
    }


    private void assertSFERecord(TerminationRecord actionTR, TestCompletedEvent event,String recordType) {
        if(actionTR==null) {
            fail("No "+recordType+" record after \n"+event);
        }
        assertTerminationRecordContains(actionTR,null, "org.smartfrog.sfcore.common.SmartFrogException", "SFE");
    }
}
