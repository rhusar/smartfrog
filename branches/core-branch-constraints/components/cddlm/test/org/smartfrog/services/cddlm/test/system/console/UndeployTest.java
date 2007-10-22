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


package org.smartfrog.services.cddlm.test.system.console;

import org.apache.axis.AxisFault;
import org.apache.axis.types.URI;
import org.cddlm.client.console.ConsoleOperation;
import org.cddlm.client.console.Undeploy;
import org.smartfrog.services.cddlm.api.TerminateProcessor;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;

/**
 * Date: 02-Sep-2004 Time: 20:41:24
 */
public class UndeployTest extends ConsoleTestBase {

    private Undeploy operation;

    /**
     * get the operation of this test base
     *
     * @return the current operation
     */
    protected ConsoleOperation getOperation() {
        return operation;
    }

    /**
     * Sets up the fixture, by creating an operation
     */
    protected void setUp() throws Exception {
        super.setUp();
        operation = new Undeploy(getBinding(), getOut());
    }

    public void testMissingApp() throws Exception {
        try {
            operation.terminate(new URI(INVALID_URI), "termination");
        } catch (AxisFault e) {
            assertFaultMatches(e,
                    DeployApiConstants.FAULT_NO_SUCH_APPLICATION,
                    null);
        }
    }

    public void testNullApp() throws Exception {
        try {
            operation.terminate(null, "termination");
        } catch (AxisFault e) {
            assertFaultMatches(e,
                    DeployApiConstants.FAULT_BAD_ARGUMENT,
                    TerminateProcessor.ERROR_NO_APPLICATION);
        }
    }

    public void testMissingAppAndReason() throws Exception {
        try {
            operation.terminate(new URI(INVALID_URI), null);
        } catch (AxisFault e) {
            assertFaultMatches(e,
                    DeployApiConstants.FAULT_NO_SUCH_APPLICATION,
                    null);
        }
    }

    public void testExecute() throws Exception {
        try {
            String args[] = new String[1];
            args[0] = INVALID_URI;
            operation = new Undeploy(getBinding(), getOut(), args);
            operation.execute();

        } catch (AxisFault e) {
            assertFaultMatches(e,
                    DeployApiConstants.FAULT_NO_SUCH_APPLICATION,
                    null);
        }
    }

    public void testExecute2() throws Exception {
        try {
            operation.setUri(new URI(INVALID_URI));
            operation.execute();

        } catch (AxisFault e) {
            assertFaultMatches(e,
                    DeployApiConstants.FAULT_NO_SUCH_APPLICATION,
                    null);
        }
    }


}
