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
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.cddlm.client.console.Deploy;
import org.cddlm.client.console.Options;
import org.cddlm.client.generated.api.types.CallbackEnum;
import org.cddlm.client.generated.api.types.CallbackInformationType;
import org.cddlm.client.generated.api.types.DeploymentDescriptorType;
import org.cddlm.client.generated.api.types.OptionMapType;
import org.cddlm.client.generated.api.types.OptionType;
import org.cddlm.client.generated.api.types._deploymentDescriptorType_data;
import org.smartfrog.services.cddlm.api.Constants;
import org.smartfrog.services.cddlm.cdl.ResourceLoader;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * created Sep 1, 2004 6:00:41 PM
 */

public class DeployTest extends ConsoleTestBase {

    private Deploy operation;


    public static final String SFCONFIG_EXTENDS_COMPOUND = "sfConfig extends Compound {}";
    public static final String SIMPLE_DESCRIPTOR =
            "#include \"/org/smartfrog/components.sf\"\n"
            + SFCONFIG_EXTENDS_COMPOUND + "\n";

    public static final String BROKEN_DESCRIPTOR =
            "#include \"/org/smartfrog/components.sf\""
            + " sfConfig extends Unknown {} ";

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        operation = new Deploy(getBinding(), getOut());
    }

    public void testDescriptorCreation() throws Exception {
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(
                SIMPLE_DESCRIPTOR);
        assertInDescriptor(dd, SFCONFIG_EXTENDS_COMPOUND);
    }

    public void testDescriptorInstream() throws Exception {
        ResourceLoader loader = new ResourceLoader();
        InputStream in = loader.loadResource(
                "org/smartfrog/services/cddlm/components.sf");
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(in);
        assertInDescriptor(dd, "axis/services/cddlm?wsdl");
    }

    private void assertInDescriptor(DeploymentDescriptorType dt, String search)
            throws Exception {
        _deploymentDescriptorType_data data = dt.getData();
        assertNotNull("data null", data);
        final org.apache.axis.message.MessageElement[] any = data.get_any();
        assertNotNull("data/any null", any);
        assertTrue("any empty", any.length == 1);
        String output = any[0].getAsString();
        assertInText(output, search);
    }

    public void testSimpleDeploy() throws Exception {
        DeploymentDescriptorType dt = operation.createSmartFrogDescriptor(
                SIMPLE_DESCRIPTOR);
        URI uri = deploy("simple", dt, null, null);
    }

    public void testEmptyDeploy() throws Exception {
        URI uri = deploy("null", null, null, null);

    }

    public void testBrokenDeploy() throws IOException {
        DeploymentDescriptorType dt = operation.createSmartFrogDescriptor(
                BROKEN_DESCRIPTOR);
        URI uri = deploy("broken", dt, null, null);
    }

    private URI deploy(String name,
                       DeploymentDescriptorType descriptor,
                       Options options, CallbackInformationType callback)
            throws RemoteException {
        URI uri = operation.deploy(name, descriptor, options, null);
        assertNotNull("uri", uri);
        return uri;
    }

    public void testUnsupportedLanguage() throws RemoteException {
        MessageElement me = operation.createSmartfrogMessageElement(
                SIMPLE_DESCRIPTOR);
        me.setNamespaceURI("http://invalid.example.org");
        DeploymentDescriptorType dd = operation.createDescriptorWithXML(me);
        deployExpectingFault("unsupported",
                dd,
                null,
                null,
                Constants.FAULT_UNSUPPORTED_LANGUAGE,
                null);
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
     * @throws RemoteException
     */
    private void deployExpectingFault(final String name,
                                      DeploymentDescriptorType dd,
                                      final Options options,
                                      CallbackInformationType callback, final QName fault,
                                      final String text)
            throws RemoteException {
        try {
            URI uri = deploy(name, dd, options, null);
        } catch (AxisFault af) {
            assertEquals(fault, af.getFaultCode());
            if (text != null) {
                String message = af.getFaultReason();
                assertNotNull("fault reason is null", message);
                assertTrue("expected [" + text + "] in " + message,
                        message.indexOf(text) >= 0);
            }
        }
    }

    /**
     * change the version to sfrog and see what happens
     *
     * @throws RemoteException
     */
    public void testUnsupportedSmartFrogVersion() throws RemoteException {
        MessageElement me = operation.createSmartfrogMessageElement(
                SIMPLE_DESCRIPTOR);
        me.removeAttribute("version");
        me.setAttribute(me.getNamespaceURI(), "version", "1.7");
        DeploymentDescriptorType dd = operation.createDescriptorWithXML(me);
        deployExpectingFault("unsupportedVersion",
                dd,
                null,
                null, Constants.FAULT_UNSUPPORTED_LANGUAGE,
                null);

    }

    /**
     * change the version to sfrog and see what happens
     *
     * @throws RemoteException
     */
    public void testUndefinedSmartFrogVersion() throws RemoteException {
        MessageElement me = operation.createSmartfrogMessageElement(
                SIMPLE_DESCRIPTOR);
        me.removeAttribute("version");
        DeploymentDescriptorType dd = operation.createDescriptorWithXML(me);
        deployExpectingFault("UndefinedVersion",
                dd,
                null,
                null,
                Constants.FAULT_UNSUPPORTED_LANGUAGE,
                null);
    }

    /**
     * test this callback option is not recognised
     *
     * @throws IOException
     */
    public void testUnsupportedCallback() throws IOException {
        CallbackInformationType callback = new CallbackInformationType();
        callback.setType(CallbackEnum.fromValue("ws-eventing"));
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(
                SIMPLE_DESCRIPTOR);
        deployExpectingFault("UndefinedVersion",
                dd,
                null,
                callback,
                Constants.FAULT_UNSUPPORTED_CALLBACK,
                null);
    }

    public void testOptionCreation() throws IOException {
        Options options = new Options();
        OptionType option = options.createNamedOption(
                new URI("http://localhost/1"), true);
        assertTrue(option.isMustUnderstand());
        option =
        options.createNamedOption(new URI("http://localhost/2"), false);
        assertFalse(option.isMustUnderstand());
        OptionMapType map = options.toOptionMap();
        assertEquals("map size is two", 2, map.getOption().length);
        final URI name = new URI("http://localhost/3");
        options.addOption(name, true, true);
        OptionType o = options.lookupOption(name);
        assertNotNull("option lookup", o);
        assertTrue(o.is_boolean());
        options.removeOption(name);
        assertNull("option removal", options.lookupOption(name));
        Iterator it = options.iterator();
        assertNotNull("option iterator", it);
    }

    /**
     * test that a header is not understood
     *
     * @throws RemoteException
     */
    public void testNotUnderstoodOption() throws IOException {
        Options options = new Options();
        options.addOption(new URI("http://localhost/invalid.sf"),
                "test",
                true);
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(
                SIMPLE_DESCRIPTOR);
        deployExpectingFault("testNotUnderstoodOption",
                dd,
                options,
                null,
                Constants.FAULT_NOTUNDERSTOOD,
                null);
    }

    /**
     * test that ignored headers dont raise trouble
     *
     * @throws RemoteException
     */
    public void testIgnoredHeader() throws IOException {
        Options options = new Options();
        options.addOption(new URI("http://localhost/ignored"), "test", false);
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(
                SIMPLE_DESCRIPTOR);
        URI uri = deploy("testIgnoredHeader", dd, options, null);
    }


    public void testDeployInvalidURL() throws Exception {
        DeploymentDescriptorType descriptor = new DeploymentDescriptorType();
        descriptor.setSource(new URI("http://localhost/invalid.sf"));
        deploy("invalid", descriptor, null, null);
    }
}
