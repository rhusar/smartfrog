/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.workflow.conditional;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.test.system.workflow.delay.DelayTest;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * test delays
 */
public class ConditionalTest extends DeployingTestBase {
    protected static final String FILES = "org/smartfrog/test/system/workflow/conditional/";
    private static final String WAITFOR_FAILED = "waitfor failed";

    public ConditionalTest(String s) {
        super(s);
    }

/*
    public void testParseConditionFile() throws Throwable {
        runTestsToCompletion(FILES,"testParseConditionFile");
    }
*/

    public void testPassingIf() throws Throwable {
        application = deployExpectingSuccess(FILES + "testPassingIf.sf", "testPassingIf");
        expectSuccessfulTermination((TestBlock) application);
    }

    public void testPassingWaitFor() throws Throwable {
        application=deployExpectingSuccess(FILES +"testPassingWaitFor.sf","testPassingWaitFor");
        expectSuccessfulTermination((TestBlock) application);
    }

    public void testOneWaitFor() throws Throwable {
        application = deployExpectingSuccess(FILES + "testOneWaitFor.sf", "testOneWaitFor");
        expectSuccessfulTermination((TestBlock) application);
    }

    public void testOneIf() throws Throwable {
        application = deployExpectingSuccess(FILES + "testOneIf.sf", "testOneIf");
        expectSuccessfulTermination((TestBlock) application);
    }

    public void testFailingWaitFor() throws Throwable {
        TestCompletedEvent event =
            expectAbnormalTestRun(FILES, "testFailingWaitFor");
        TerminationRecord tr;
        tr=event.getStatus();
//        application = deployExpectingSuccess(FILES + "testFailingWaitFor.sf", "testFailingWaitFor");
//        tr = expectAbnormalTermination((TestBlock) application);
        assertNotNull("no description in "+tr,tr);
        String description = tr.description;
        assertTrue("No "+WAITFOR_FAILED+" in "+tr,
                description.indexOf(WAITFOR_FAILED)>=0);
    }
}
