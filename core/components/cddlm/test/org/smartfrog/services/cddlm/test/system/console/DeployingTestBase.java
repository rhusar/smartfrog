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

import org.cddlm.client.console.Deploy;
import org.cddlm.client.console.Options;
import org.cddlm.client.console.ConsoleOperation;
import org.cddlm.client.generated.api.types.DeploymentDescriptorType;
import org.cddlm.client.generated.api.types._deploymentDescriptorType_data;
import org.cddlm.client.generated.api.types.CallbackInformationType;
import org.cddlm.client.generated.api.types.ApplicationStatusType;
import org.cddlm.client.generated.api.types.LifecycleStateEnum;
import org.apache.axis.types.URI;
import org.apache.axis.AxisFault;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;

/**
 * Date: 06-Sep-2004
 * Time: 22:27:16
 */
public abstract class DeployingTestBase extends ConsoleTestBase {
    protected Deploy operation;
    public static final String SFCONFIG_EXTENDS_COMPOUND = "sfConfig extends Compound {}";
    public static final String SIMPLE_DESCRIPTOR =
            "#include \"/org/smartfrog/components.sf\"\n"
            + SFCONFIG_EXTENDS_COMPOUND + "\n";
    public static final String BROKEN_DESCRIPTOR =
            "#include \"/org/smartfrog/components.sf\""
            + " sfConfig extends Unknown {} ";

    protected ConsoleOperation getOperation() {
        return operation;
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        operation = new Deploy(getBinding(), getOut());
    }

    /**
     * assert a descriptor contains the text
     * @param dt
     * @param search
     * @throws Exception
     */
    protected void assertInDescriptor(DeploymentDescriptorType dt, String search)
            throws Exception {
        _deploymentDescriptorType_data data = dt.getData();
        assertNotNull("data null", data);
        final org.apache.axis.message.MessageElement[] any = data.get_any();
        assertNotNull("data/any null", any);
        assertTrue("any empty", any.length == 1);
        String output = any[0].getAsString();
        assertInText(output, search);
    }

    protected URI deploy(String name,
                       DeploymentDescriptorType descriptor,
                       Options options, CallbackInformationType callback)
            throws RemoteException {
        URI uri = operation.deploy(name, descriptor, options, callback);
        assertNotNull("uri", uri);
        return uri;
    }

    /**
     * deploy, expecting some kind of fault
     *
     * @param name
     * @param dd
     * @param options
     * @param callback
     * @param fault
     * @param text
     * @throws java.rmi.RemoteException
     */
    protected void deployExpectingFault(final String name,
                                      DeploymentDescriptorType dd,
                                      final Options options,
                                      CallbackInformationType callback, final QName fault,
                                      final String text)
            throws RemoteException {
        try {
            URI uri = deploy(name, dd, options, callback);
        } catch (AxisFault af) {
            assertFaultMatches(af, fault, text);
        }
    }

    /**
     * assert that an app exists and is in the named state
     * @param uri
     * @param stateName
     * @throws RemoteException
     */
    public void assertInState(URI uri, String stateName) throws RemoteException {
        ApplicationStatusType status = operation.lookupApplicationStatus(uri);
        assertNotNull("app status of "+uri,status);
        LifecycleStateEnum state = status.getState();
        String currentState = state.getValue();
        assertEquals(stateName, currentState);
        }
}
