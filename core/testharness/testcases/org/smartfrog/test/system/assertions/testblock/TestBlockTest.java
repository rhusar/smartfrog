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
package org.smartfrog.test.system.assertions.testblock;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.TestBlockImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Test that the test block works. 
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class TestBlockTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/assertions/testblock/";



    public TestBlockTest(String name) {
        super(name);
    }


    public void testEmptySequence() throws Throwable {
        application =deployExpectingSuccess(FILES + "testSequence.sf", "testSequence");
        TestBlock testBlock = (TestBlock) application;
        expectSuccessfulTermination(testBlock);
        assertSuccessful(testBlock);
    }

    private void assertSuccessful(TestBlock testBlock) throws RemoteException, SmartFrogException {
        assertTrue(testBlock.isFinished());
        assertFalse(testBlock.isFailed());
        assertTrue(testBlock.isSucceeded());
        TerminationRecord status = testBlock.getStatus();
        assertTrue(status.isNormal());
    }


    public void testRun() throws Throwable {
        application = deployExpectingSuccess(FILES + "testRun.sf", "testRun");
        TestBlock testBlock = (TestBlock) application;
        expectSuccessfulTermination(testBlock);
        assertSuccessful(testBlock);
    }

    public void testFailure() throws Throwable {
        application =deployExpectingSuccess(FILES + "testFailure.sf", "testFailure");
        String error = "failure message";
        expectFailure(error);
    }

    public void testSmartFrogException() throws Throwable {
        application =deployExpectingSuccess(FILES + "testSmartFrogException.sf", "testSmartFrogException");
        String error = TestBlockImpl.ERROR_STARTUP_FAILURE;
        expectFailure(error);
    }

    private void expectFailure(String error) throws Throwable {
        TestBlock testBlock = (TestBlock) application;
        expectAbnormalTermination(testBlock);
        assertTrue(testBlock.isFinished());
        assertTrue(testBlock.isFailed());
        assertFalse(testBlock.isSucceeded());
        TerminationRecord status = testBlock.getStatus();
        assertFalse(status.isNormal());
        assertEquals(error, status.description);
    }

}
