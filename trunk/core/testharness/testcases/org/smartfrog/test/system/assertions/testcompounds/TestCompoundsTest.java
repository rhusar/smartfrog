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

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.services.assertions.TestCompoundImpl;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class TestCompoundsTest extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/assertions/testcompounds/";



    public TestCompoundsTest(String name) {
        super(name);
    }

    protected Prim application;

    protected void tearDown() throws Exception {
        super.tearDown();
        terminateApplication(application);
    }

    public void testEmptyCompound() throws Throwable {
     //   application =deployExpectingSuccess(TestCompoundsTest.FILES +"testEmptyCompound.sf","testEmptyCompound");
    }

    public void NOtestEmptySequence() throws Throwable {
        application =deployExpectingSuccess(TestCompoundsTest.FILES + "testEmptySequence.sf", "testEmptySequence");
    }

    public void NOtestFailure() throws Throwable {
        application =deployExpectingSuccess(TestCompoundsTest.FILES + "testFailure.sf", "testFailure");
    }


    public void NOtestUnexpectedFailure() throws Throwable {
        deployExpectingException(TestCompoundsTest.FILES + "testUnexpectedFailure.sf",
                "testUnexpectedFailure", null, TestCompoundImpl.TEST_FAILED_WRONG_STATUS);
    }

    public void NOtestFailureWrongMessage() throws Throwable {
        deployExpectingException(TestCompoundsTest.FILES + "testFailureWrongMessage.sf",
                "testFailureWrongMessage",null, TestCompoundImpl.TEST_FAILED_WRONG_STATUS);
    }


    public void NOtestFailureWrongMessage2() throws Throwable {
        application = deployExpectingSuccess(TestCompoundsTest.FILES + "testFailureWrongMessage.sf",
                "testFailureWrongMessage");
/*        app.sfPing(null);
        app.sfPing(null);
        app.sfPing(null);
        app.sfPing(null);
        app.sfPing(null);
        app.sfPing(null);*/
        //terminateApplication(app);
        application =null;

    }

    /**
     * spin, pinging the application until it terminates successfully or not.
     * @param ping ping every second?
     * @param secondsToSpin number of seconds to spin
     * @param expectNormal is a normal exit expected?
     * @param errorText any error text to look for
     * @param exceptionName
     * @param exceptionText
     */
    public void spinUntilTerminated(Prim app,boolean ping, int secondsToSpin,
                                    boolean expectNormal,String errorText,String exceptionName,String exceptionText)
            throws RemoteException {
        
        terminateApplication(app);
    }

    public void NOtestFailureWrongMessageNested() throws Throwable {
        application =deployExpectingSuccess(TestCompoundsTest.FILES + "testFailureWrongMessageNested.sf",
                "testFailureWrongMessageNested");
    }

    public void NOtestSmartFrogException() throws Throwable {
        application =deployExpectingSuccess(TestCompoundsTest.FILES + "testSmartFrogException.sf", "testSmartFrogException");
    }

}
