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
import org.cddlm.client.console.Deploy;
import org.cddlm.client.console.Options;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorTypeBody;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleStateEnum;
import org.smartfrog.services.cddlm.generated.api.types.NotificationInformationType;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Date: 06-Sep-2004 Time: 22:27:16
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
    public static final String UNDEPLOY_REASON = "end test";

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
     *
     * @param dt
     * @param search
     * @throws Exception
     */
    protected void assertInDescriptor(DeploymentDescriptorType dt,
            String search)
            throws Exception {
        DeploymentDescriptorTypeBody body = dt.getBody();
        assertNotNull("data null", body);
        final org.apache.axis.message.MessageElement[] any = body.get_any();
        assertNotNull("data/any null", any);
        assertTrue("any empty", any.length == 1);
        String output = any[0].getAsString();
        assertInText(output, search);
    }

    protected URI deploy(String name,
            DeploymentDescriptorType descriptor,
            Options options, NotificationInformationType callback)
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
            NotificationInformationType callback, final QName fault,
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
     *
     * @param uri
     * @param stateName
     * @throws RemoteException
     */
    public void assertInState(URI uri, String stateName)
            throws RemoteException {
        ApplicationStatusType status = operation.lookupApplicationStatus(uri);
        assertNotNull("app status of " + uri, status);
        LifecycleStateEnum state = status.getState();
        String currentState = state.getValue();
        assertEquals(stateName, currentState);
    }

    /**
     * undeploy something
     *
     * @param uri
     * @return
     * @throws RemoteException
     */
    public boolean undeploy(URI uri) throws RemoteException {
        return operation.terminate(uri, UNDEPLOY_REASON);
    }

    public void assertDeployed(URI uri) throws RemoteException {
        assertInState(uri, DeployApiConstants.STATE_RUNNING);
    }

    protected URI deploy(DeploymentDescriptorType dt) throws RemoteException {
        URI uri;
        uri = deploy(null, dt, null, null);
        return uri;
    }

    protected DeploymentDescriptorType createSimpleDescriptor()
            throws IOException {
        DeploymentDescriptorType dt = operation.createSmartFrogDescriptor(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        return dt;
    }
}
