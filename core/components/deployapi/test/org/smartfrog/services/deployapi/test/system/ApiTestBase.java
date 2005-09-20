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


package org.smartfrog.services.deployapi.test.system;

import org.apache.axis2.AxisFault;
import org.smartfrog.services.deployapi.client.ConsoleOperation;
import org.smartfrog.services.deployapi.system.Constants;
import org.ggf.xbeans.cddlm.api.DescriptorType;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.rmi.RemoteException;
import java.net.URI;

/**
 * Date: 06-Sep-2004 Time: 22:27:16
 */
public abstract class ApiTestBase extends ConsoleTestBase {
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
    protected void assertInDescriptor(DescriptorType dt,
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
                                  DescriptorType descriptor,
                                  OptionsType options, NotificationInformationType callback)
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
                                        DescriptorType dd,
                                        final OptionsType options,
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
     * @throws java.rmi.RemoteException
     */
    public void assertInState(URI uri, String stateName)
            throws RemoteException {
        ApplicationStatusType status = operation.lookupApplicationStatus(uri);
        assertNotNull("app status of " + uri, status);
        Constants.LifecycleStateEnum state = status.getState();
        String currentState = state.getValue();
        assertEquals(stateName, currentState);
    }

    /**
     * undeploy something
     *
     * @param uri
     * @return
     * @throws java.rmi.RemoteException
     */
    public boolean undeploy(URI uri) throws RemoteException {
        return operation.terminate(uri, UNDEPLOY_REASON);
    }

    public void assertDeployed(URI uri) throws RemoteException {
        assertInState(uri, Constants.STATE_RUNNING);
    }

    protected URI deploy(DescriptorType dt) throws RemoteException {
        URI uri;
        uri = deploy(null, dt, null, null);
        return uri;
    }

    protected DescriptorType createSimpleDescriptor()
            throws IOException {
        DescriptorType dt = operation.createSmartFrogDescriptor(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        return dt;
    }
}
