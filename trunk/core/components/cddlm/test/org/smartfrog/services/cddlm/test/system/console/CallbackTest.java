/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
import org.smartfrog.services.cddlm.api.CallbackProcessor;
import org.smartfrog.services.cddlm.api.Processor;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;

/**
 * created Sep 14, 2004 2:27:34 PM
 */

public class CallbackTest extends DeployingTestBase {
    public static final String DUMMY_ENDPOINT = "http://localhost:8080/endpoint";


    public void testNullApp() throws Exception {
        try {
            operation.setCddlmCallback(null,
                    "http://localhost:8080/endpoint",
                    null);
        } catch (AxisFault e) {
            assertFaultMatches(e,
                    DeployApiConstants.FAULT_BAD_ARGUMENT,
                    CallbackProcessor.ERROR_NO_APPLICATION);
        }
    }

    public void testBadApplication() throws Exception {
        try {
            operation.setCddlmCallback(new URI(INVALID_URI),
                    DUMMY_ENDPOINT,
                    null);
        } catch (AxisFault e) {
            assertFaultMatches(e,
                    DeployApiConstants.FAULT_NO_SUCH_APPLICATION,
                    Processor.ERROR_APP_URI_NOT_FOUND);
        }
    }

    public void testNullURL() throws Exception {
        DeploymentDescriptorType dt = operation.createSmartFrogDescriptor(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        URI uri = deploy(null, dt, null, null);
        try {
            try {
                operation.setCddlmCallback(uri, DUMMY_ENDPOINT, null);
            } catch (AxisFault e) {
                assertFaultMatches(e,
                        DeployApiConstants.FAULT_BAD_ARGUMENT,
                        CallbackProcessor.ERROR_NO_ADDRESS);
            }
        } finally {
            undeploy(uri);
        }
    }


}
