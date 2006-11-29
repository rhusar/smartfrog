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

package org.smartfrog.test.system.workflow.delay;

import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.test.DeployingTestBase;


/**
 * test delays
 */
public class DelayTest extends DeployingTestBase {
    protected static final String FILES = "org/smartfrog/test/system/workflow/delay/";

    public DelayTest(String s) {
        super(s);
    }
    public void testDelayNormal() throws Throwable {
        application=deployExpectingSuccess(FILES +"testDelayNormal.sf","testDelayNormal");
        expectSuccessfulTermination((TestBlock) application);
    }

    /**
     * We have a problem here with testing delayed operations, because
     * we need to lock and wait for completion. 
     * this may be time to rethink how we run tests.
     * @throws Throwable
     */
    public void testDelayAbnormalFailure() throws Throwable {
        application = deployExpectingSuccess(FILES+"testDelayAbnormalFailure.sf","testDelayAbnormalFailure");
        expectAbnormalTermination((TestBlock) application);
    }

    public void testDelayNoAction() throws Throwable {
        application = deployExpectingSuccess(FILES + "testDelayNoAction.sf", "testDelayNoAction");
        expectSuccessfulTermination((TestBlock) application);
    }
}
