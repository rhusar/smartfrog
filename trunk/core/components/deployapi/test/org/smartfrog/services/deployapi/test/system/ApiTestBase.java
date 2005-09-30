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
import org.apache.xmlbeans.XmlException;
import org.smartfrog.services.deployapi.client.ConsoleOperation;
import org.smartfrog.services.deployapi.client.Deploy;
import org.smartfrog.services.deployapi.client.SystemEndpointer;
import org.smartfrog.services.deployapi.client.Endpointer;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.ggf.xbeans.cddlm.api.DescriptorType;
import org.ggf.xbeans.cddlm.api.OptionMapType;
import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyResponseDocument;
import org.ggf.cddlm.utils.QualifiedName;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;
import java.io.IOException;

import nu.xom.Element;

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

    protected ApiTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        operation = new Deploy(getBinding(), getOut());
    }


    protected SystemEndpointer deploy(String name,
                                      DescriptorType descriptor,
                                      OptionMapType options)
            throws RemoteException {
        SystemEndpointer systemEndpointer = operation.deploy(null,descriptor, options);
        return systemEndpointer;
    }

    /**
     * deploy, expecting some kind of fault
     *
     * @param dd
     * @param options
     * @param fault
     * @param text
     * @throws java.rmi.RemoteException
     */
    protected void deployExpectingFault(DescriptorType dd,
                                        final OptionMapType options,
                                        final QName fault,
                                        final String text)
            throws RemoteException {
        try {
            deploy(null, dd, options);
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
    public void assertInState(SystemEndpointer system, String stateName)
            throws RemoteException {
        /*
        ApplicationStatusType status = operation.lookupApplicationStatus(system);
        assertNotNull("app status of " + uri, status);
        Constants.LifecycleStateEnum state = status.getState();
        String currentState = state.getValue();
        assertEquals(stateName, currentState);
        */
    }


    public void assertDeployed(SystemEndpointer system) throws RemoteException {
        assertInState(system, Constants.STATE_RUNNING);
    }

    protected SystemEndpointer createSystem() throws RemoteException {
        SystemEndpointer systemEndpointer;
        systemEndpointer = createSystem(null);
        return systemEndpointer;
    }

    protected SystemEndpointer createSystem(String hostname) throws RemoteException {
        SystemEndpointer systemEndpointer = getOperation().create(hostname);
        log.info("Created system "+systemEndpointer);
        return systemEndpointer;
    }

    /**
     * Terminate a system if it is not null
     * @param system
     * @throws RemoteException
     */
    public void terminateSystem(SystemEndpointer system) throws IOException {
        if (system != null) {
            log.info("terminating " + system.toString());
            system.terminate("end of test");
        }
    }

    /**
     * Destroy a system if it is not null
     *
     * @param system
     * @throws RemoteException
     */
    public void destroySystem(SystemEndpointer system) throws IOException {
        if (system != null) {
            log.info("destroying" + system.toString());
            system.destroy();
        }
    }

    protected GetResourcePropertyResponseDocument getPortalResourceProperty(QualifiedName portalProperty) throws RemoteException {
        return getOperation().getPortalProperty(portalProperty);
    }

    protected GetResourcePropertyResponseDocument getResourceProperty(Endpointer system,
                                                                      QualifiedName property) throws RemoteException {
        QName qname= Utils.convert(property);
        return system.getPropertyResponse(qname);
    }


}
