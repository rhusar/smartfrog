/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.junitmr;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 17-Mar-2009 16:49:06
 */

public class TestSuiteRun implements TestListener {

    private List<SingleTestRun> tests = new ArrayList<SingleTestRun>();

    private Test activeTest;
    private SingleTestRun activeTestRun;

    public TestSuiteRun() {
    }

    private void checkActive(Test t) {
        if (t != activeTest) {
            throw new JUnitMRException("the test " + t + " is not the active test "
                    + activeTest);
        }
    }

    private void bindActive(Test t) {
        activeTest = t;
        activeTestRun = new SingleTestRun();
        tests.add(activeTestRun);
    }

    /**
     * A test started.
     */
    public void startTest(Test test) {
        bindActive(test);
        activeTestRun.startTest(test);
    }

    /**
     * A test ended.
     */
    public void endTest(Test test) {
        checkActive(test);
        activeTestRun.endTest(test);
        //now clean up
        activeTestRun = null;
        activeTest = null;
    }

    /**
     * An error occurred.
     */
    public void addError(Test test, Throwable t) {
        checkActive(test);
        activeTestRun.addError(test, t);
    }

    /**
     * A failure occurred.
     */
    public void addFailure(Test test, AssertionFailedError t) {
        //it's just an error to us
        addError(test, t);
    }

    /**
     * Get the list of tests
     *
     * @return test list
     */
    public List<SingleTestRun> getTests() {
        return tests;
    }
}
