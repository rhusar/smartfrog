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
package org.smartfrog.services.assertions;

import org.smartfrog.sfcore.workflow.conditional.Conditional;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;


/**
 * created 22-Sep-2006 16:13:38
 */


public interface TestCompound extends TestBlock, Conditional {


    /**
     * A condition to wait for, before running the tests.
     * {@value}
     */
    String ATTR_WAITFOR ="waitFor";

    /**
     * time in milliseconds to undeploy the action; use -1 for no limit.
     * {@value}
     */
    String ATTR_UNDEPLOY_AFTER = "undeployAfter";

    /**
     * time in milliseconds to trigger an exception if the component is not started;
     * use -1 for no limit
     * {@value}
     */
    String ATTR_STARTUP_TIMEOUT = "startupTimeout";

    /**
     * is the action component expected to terminate itself?
     * {@value}
     */
    String ATTR_EXPECT_TERMINATE = "expectTerminate";

    /**
     * A component that is deployed after the action component is successfully deployed,
     and which contains assertions to deploy.
     The assertions are only deployed if the action deployment was successful.
     <ol>
     <li>If this component terminates successfully, then the test is deemed to be successful, and
     the TestCompound terminates the tests and runs teardown.</li>
     <li>. If this component does not terminate, then it is kept running (and pinged during pings)
     until the action terminates or the test is undeployed.
     </li></ol>
     * {@value}
     */
    String ATTR_TESTS = "tests";

    /**
     * Time in milliseconds for the tests to successfully finish, use -1 for no limit.
     * If this is set, then the tests are deemed to have failed if they have not finished
     * by the time the tests are forcibly terminated
     */
    String ATTR_TEST_TIMEOUT = "testTimeout";


    /**
     * should we force a set of pings?
     * {@value}
     */
    String ATTR_FORCEPING = "forcePing";

    /**
     * time in milliseconds between forced pings
     * {@value}
     */
    String ATTR_PINGINTERVAL = "pingInterval";

    /**
     * string which must be found in the termination errortype attribute using a case sensitive match.
     * {@value}
     */
    String ATTR_EXIT_TYPE = "exitType";

    /**
     * string which must be found in the TerminationRecord.description attribute using a case sensitive match.
     * {@value}
     */
    String ATTR_EXIT_TEXT = "exitText";


    /**
     * Get the termination record for this child; may be null
     * @return a termination record or null
     * @throws RemoteException for network problems
     */
    TerminationRecord getActionTerminationRecord() throws RemoteException;

    /**
     * Get the termination record for this child; may be null
     * @return a termination record or null
     * @throws RemoteException for network problems
     */
    TerminationRecord getTestsTerminationRecord() throws RemoteException;

}
