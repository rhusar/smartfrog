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

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.services.assertions.SmartFrogAssertionException;

/**
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class AssertionsTest extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/assertions/";

    public AssertionsTest(String name) {
        super(name);
    }

    public void testBasicAssertions() throws Throwable {
        deployExpectingSuccess(FILES+"testBasicAssertions.sf","testBasicAssertions");
    }

    public void testTrueIsFalse() throws Throwable  {
        deployExpectingAssertionFailure("testTrueIsFalse.sf");
    }

    public void testFalseIsTrue() throws Throwable {
        Throwable t=deployExpectingAssertionFailure("testFalseIsTrue.sf");
        SmartFrogAssertionException sfe= extractAssertionException(t);
        assertContains(sfe.getMessage(),"truth and falsehood");

    }

    /**
     * recursive search for the root cause
     * @param throwable
     * @return the assertion or null
     */
    public SmartFrogAssertionException extractAssertionException(Throwable throwable) {
        if(throwable==null) {
            return null;
        }
        if(throwable instanceof SmartFrogAssertionException) {
            return (SmartFrogAssertionException) throwable;
        }
        return extractAssertionException(throwable.getCause());
    }

    /**
     * @todo: turn on once we have a way of expecting liveness faults.
     * @throws Throwable
     */
    public void NotestFalseIsLazyTrue() throws Throwable {
        deployExpectingAssertionFailure("testFalseIsLazyTrue.sf");
    }

    /**
     * probably not doing what we think
     * @throws Throwable
     */
    public void testEvaluatesTrue() throws Throwable {
        deployExpectingSuccess(FILES + "testEvaluatesTrue.sf", "testEvaluatesTrue");
    }


    /**
     * probably not doing what we think
     *
     * @throws Throwable
     */
    public void testEvaluatesTrueToFalse() throws Throwable {
        deployExpectingSuccess(FILES + "testEvaluatesTrueToFalse.sf", "testEvaluatesTrueToFalse");
    }

    /**
     * deploy something from this directory; expect an exception
     * @param filename
     * @throws Throwable
     */
    public Throwable deployExpectingAssertionFailure(String filename) throws Throwable {
        return deployExpectingException(FILES + filename,
                filename,
                "SmartFrogLifecycleException", null,
                "SmartFrogAssertionException", null);
    }

    /**
     * test that no method results in a meaningful failure
     * @throws Throwable
     */
    public void testEvaluatesNoSuchMethod() throws Throwable {
        deployExpectingAssertionFailure("testEvaluatesNoSuchMethod.sf");
    }

    /**
     * test that values are resolved.
     */
    public void testEvaluatesThrowsSFException() throws Throwable {
        deployExpectingSuccess(FILES + "testEvaluatesThrowsSFException.sf", "testEvaluatesThrowsSFException");
    }

    public void testEvaluatesThrowsRuntimeException() throws Throwable {
        deployExpectingSuccess(FILES + "testEvaluatesThrowsRuntimeException.sf", "testEvaluatesThrowsRuntimeException");
    }

}
