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
package org.smartfrog.test.system.assertions.testcompounds;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestCompoundImpl;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class TestCompoundsTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/assertions/testcompounds/";

    public TestCompoundsTest(String name) {
        super(name);
    }

    public void testEmptySequence() throws Throwable {
        application = deployExpectingSuccess(TestCompoundsTest.FILES + "testEmptySequence.sf", "testEmptySequence");
        expectSuccessfulTermination((TestBlock) application);
    }

    public void testFailure() throws Throwable {
        TerminationRecord record = deployToNormalTermination("testFailure");
        assertTerminationRecordContains(record, "failure message",null,null);
    }

    private TerminationRecord deployToAbnormalTermination(String test) throws Throwable {
        application = deployExpectingSuccess(TestCompoundsTest.FILES + test +".sf", test);
        TerminationRecord record = expectAbnormalTermination((TestBlock) application);
        return record;
    }

    private TerminationRecord deployToNormalTermination(String test) throws Throwable {
        application = deployExpectingSuccess(TestCompoundsTest.FILES + test + ".sf", test);
        TerminationRecord record = expectSuccessfulTermination((TestBlock) application);
        return record;
    }

    public void testFailureWrongMessageNested() throws Throwable {
        application =deployExpectingSuccess(TestCompoundsTest.FILES + "testFailureWrongMessageNested.sf",
                "testFailureWrongMessageNested");
    }

    public void testSmartFrogException() throws Throwable {
        TerminationRecord record = deployToNormalTermination("testSmartFrogException");
        assertTerminationRecordContains(record, null, "org.smartfrog.sfcore.common.SmartFrogException", "SFE");
    }
}
