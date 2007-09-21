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
package org.smartfrog.test.system.workflow.parallel;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;

/**
 * @author Ashish Awasthi
 * Date: 02-Jun-2004
 */
public class ParallelTest extends DeployingTestBase {
    protected static final String FILES = "org/smartfrog/test/system/workflow/parallel/";
    private TestBlock block;

    public ParallelTest(String s) {
        super(s);
    }


    public void testEmptyParallel() throws Throwable {
        expectTestTimeout(FILES, "testEmptyParallel");
        assertAttributeEquals(application, TestBlock.ATTR_FORCEDTIMEOUT, true);
    }

    public void testSimpleParallel() throws Throwable {
        expectSuccessfulTestRun(FILES, "testSimpleParallel");
        assertAttributeEquals(application, "value", true);
    }

    public void testEmptyParallelTerminating() throws Throwable {
        expectSuccessfulTestRun(FILES, "testEmptyParallelTerminating");
    }

    public void testStartFailingParallel() throws Throwable {
        expectAbnormalTestRun(FILES, "testStartFailingParallel",true,null);
    }


    public void testStartFailingParallelNoTerminate() throws Throwable {
        expectAbnormalTestRun(FILES, "testStartFailingParallelNoTerminate", true, null);
    }

    public void testFailingParallel() throws Throwable {
        expectAbnormalTestRun(FILES, "testFailingParallel", true, null);
    }

    public void testFailingParallelNoChild() throws Throwable {
        expectSuccessfulTestRun(FILES, "testFailingParallelNoChild");
        assertAttributeEquals(application, "value", true);
    }
}
