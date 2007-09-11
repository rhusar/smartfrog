/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test.system.assertions;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.SmartFrogAssertionException;

/**
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class AssertionsTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/assertions/";

    public AssertionsTest(String name) {
        super(name);
    }

    public void testBasicAssertions() throws Throwable {
        application=deployExpectingSuccess(FILES+"testBasicAssertions.sf","testBasicAssertions");
    }

    public void testTrueIsFalse() throws Throwable  {
        deployExpectingAssertionFailure(FILES + "testTrueIsFalse.sf", "testTrueIsFalse");
    }

    public void testFalseIsTrue() throws Throwable {
        Throwable t=deployExpectingAssertionFailure(FILES + "testFalseIsTrue.sf", "testFalseIsTrue");
        SmartFrogAssertionException sfe= extractAssertionException(t);
        assertContains(sfe.getMessage(),"truth and falsehood");
    }



    /**
     //Todo: turn on once we have a way of expecting liveness faults.
     * @throws Throwable
     */
    public void NotestFalseIsLazyTrue() throws Throwable {
        deployExpectingAssertionFailure(FILES + "testFalseIsLazyTrue.sf", "testFalseIsLazyTrue");
    }

    /**
     * probably not doing what we think
     * @throws Throwable
     */
    public void testEvaluatesTrue() throws Throwable {
        application =deployExpectingSuccess(FILES + "testEvaluatesTrue.sf", "testEvaluatesTrue");
    }


    /**
     * probably not doing what we think
     *
     * @throws Throwable
     */
    public void testEvaluatesTrueToFalse() throws Throwable {
        application = deployExpectingSuccess(FILES + "testEvaluatesTrueToFalse.sf", "testEvaluatesTrueToFalse");
    }


    /**
     * test that no method results in a meaningful failure
     * @throws Throwable
     */
    public void testEvaluatesNoSuchMethod() throws Throwable {
        deployExpectingAssertionFailure(FILES + "testEvaluatesNoSuchMethod.sf", "testEvaluatesNoSuchMethod");
    }

    /**
     * test that values are resolved.
     */
    public void testEvaluatesThrowsSFException() throws Throwable {
        application = deployExpectingSuccess(FILES + "testEvaluatesThrowsSFException.sf", "testEvaluatesThrowsSFException");
    }

    public void testEvaluatesThrowsRuntimeException() throws Throwable {
        application = deployExpectingSuccess(FILES + "testEvaluatesThrowsRuntimeException.sf", "testEvaluatesThrowsRuntimeException");
    }

    public void testAttributeFound() throws Throwable {
        application = deployExpectingSuccess(FILES + "testAttributeFound.sf", "testAttributeFound");
    }

    public void testAttributeNotFound() throws Throwable {
        deployExpectingAssertionFailure(FILES + "testAttributeNotFound.sf", "testAttributeNotFound");
    }

    public void testAssertVectorEval() throws Throwable {
        application = deployExpectingSuccess(FILES + "testAssertVectorEval.sf",
                "testAssertVectorEval");
    }
    public void testAssertVectorSize() throws Throwable {
        deployExpectingAssertionFailure(FILES + "testAssertVectorSize.sf",
                "testAssertVectorSize");
    }

    public void testAssertVectorEvalFail() throws Throwable {
        deployExpectingAssertionFailure(FILES + "testAssertVectorEvalFail.sf",
                "testAssertVectorEvalFail");
    }


}
