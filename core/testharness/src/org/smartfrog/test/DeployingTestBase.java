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
package org.smartfrog.test;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.assertions.TestBlock;

/**
 * Add an application that is always destroyed on teardown
 * created 13-Oct-2006 16:28:33
 */

public abstract class DeployingTestBase extends SmartFrogTestBase {
    private static final int SPIN_INTERVAL = 10;
    private static final int TIMEOUT = 10000;


    protected DeployingTestBase(String name) {
        super(name);
    }


    /**
     * This is an application that will be undeployed at teardown time
     */
    protected Prim application;

    protected void tearDown() throws Exception {
        super.tearDown();
        terminateApplication(application);
    }


    protected TerminationRecord expectSuccessfulTermination(TestBlock testBlock) throws Throwable {
        return expectTermination(testBlock, true);
    }

    protected TerminationRecord expectAbnormalTermination(TestBlock testBlock) throws Throwable {
        return expectTermination(testBlock, false);
    }

    protected TerminationRecord expectTermination(TestBlock testBlock,boolean normal) throws Throwable {
        TerminationRecord status = spinUntilFinished(testBlock);
        assertTrue("unexpected exit status" + status, normal==status.isNormal());
        return status;
    }

    /**
     * Delay until a test has finished, sleeping (and yielding the CPU) until
     * that point is reached. There is no timeout.
     * @param testBlock
     * @return the termination record of the component
     * @throws Throwable
     */
    protected TerminationRecord spinUntilFinished(TestBlock testBlock,long timeout) throws Throwable {
        long endtime = System.currentTimeMillis() +timeout;
        while (!testBlock.isFinished() && System.currentTimeMillis()<endtime) {
            Thread.sleep(SPIN_INTERVAL);
        }
        assertTrue("timeout waiting for application to finish",testBlock.isFinished());
        TerminationRecord status = testBlock.getStatus();
        return status;

    }

    /**
     * Delay until a test has finished, sleeping (and yielding the CPU) until
     * that point is reached. There is no timeout.
     * @param testBlock
     * @return the termination record of the component
     * @throws Throwable
     */
    protected TerminationRecord spinUntilFinished(TestBlock testBlock) throws Throwable {
        return spinUntilFinished(testBlock,TIMEOUT);
    }
}
