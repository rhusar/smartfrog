/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.deployapi.test.system;

import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.deployapi.client.SystemEndpointer;
import org.smartfrog.services.deployapi.system.Constants;
import org.apache.axis2.AxisFault;
import nu.xom.Element;

import java.io.IOException;

/**
 * This test suite is only to run against a local system
 */
public class LocalSmartFrogSystemTest extends ApiTestBase {

    public static final String INCLUDE_ECHO="#include \"org/smartfrog/services/deployapi/components/fun/components.sf\"\n";

    private final static String ECHO_APP =
        INCLUDE_ECHO+
        "sfConfig extends EchoLifecycle;\n";

    private DescriptorHelper helper = new DescriptorHelper(null);

    public LocalSmartFrogSystemTest(String name) {
        super(name);
    }

    private Element createSFrequest(String body) {
        Element descriptor = helper.createSmartFrogInlineDescriptor(body);
        Element request = helper.createInitRequest(descriptor);
        helper.validateRequest(request);
        return request;
    }
    /**
     * create an app and initialise it then terminate
     * @throws Exception
     */
    public void testInitializeInline() throws Exception {
        SystemEndpointer system=null;
        try {
            system = deployEchoSystem();

        } finally{
            destroySystem(system);
        }
    }

    private SystemEndpointer deployEchoSystem() throws IOException {
        SystemEndpointer system;
        system = createSystem();
        Element request = createSFrequest(ECHO_APP);
        system.invokeBlocking(Constants.API_SYSTEM_OPERATION_INITIALIZE,
                request);
        return system;
    }

    public void testDoubleTerminate() throws Exception {
        SystemEndpointer system = deployEchoSystem();
        try {
            terminateSystem(system);
            //this must succeed, even though the system is already terminated
            terminateSystem(system);
        } finally {
            destroySystem(system);
        }
    }

    public void testTerminateDestroy() throws Exception {
        SystemEndpointer system = deployEchoSystem();
        terminateSystem(system);
        //this must succeed, even though the system is already terminated
        destroySystem(system);
    }

    public void testDestroy() throws Exception {
        SystemEndpointer system = deployEchoSystem();
        destroySystem(system);
        try {
            destroySystem(system);
        } catch (AxisFault e) {
            assertFaultMatches(e,Constants.F_NO_SUCH_APPLICATION);
        }
    }

}
