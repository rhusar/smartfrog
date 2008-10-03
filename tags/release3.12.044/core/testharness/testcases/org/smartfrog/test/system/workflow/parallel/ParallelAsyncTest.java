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
package org.smartfrog.test.system.workflow.parallel;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.sfcore.prim.Prim;

/**
 * The asynchronous tests. These seem more prone to race conditons, and are separated for
 * ease of running separately/blocking
 */
public class ParallelAsyncTest extends DeployingTestBase {
    protected static final String FILES = "org/smartfrog/test/system/workflow/parallel/";
    private TestBlock block;

    public ParallelAsyncTest(String s) {
        super(s);
    }

    public void setBlock(Prim prim) {
        setBlock((TestBlock) prim);
    }

    public void setBlock(TestBlock block) {
        this.block = block;
    }


    private void expectValueToggled() throws Throwable {
        assertAttributeEquals(application, "value", true);
    }


    /**
     * test case
     * @throws Throwable on failure
     */
    public void testStartFailingParallelNoTerminateAsync() throws Throwable {
        expectSuccessfulTestRun(FILES, "testStartFailingParallelNoTerminateAsync");
        expectValueToggled();
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testStartFailingParallelAsync() throws Throwable {
        expectAbnormalTestRun(FILES, "testStartFailingParallelAsync", true, null);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testStartFailingParallelAsyncNoChild() throws Throwable {
        expectAbnormalTestRun(FILES, "testStartFailingParallelAsyncNoChild", true, null);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testFailingParallelAsync() throws Throwable {
        expectAbnormalTestRun(FILES, "testFailingParallelAsync", true, null);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testFailingParallelAsyncNoChild() throws Throwable {
        expectSuccessfulTestRun(FILES, "testFailingParallelAsyncNoChild");
        expectValueToggled();
    }
}
