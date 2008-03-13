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

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.cddlm.client.console.Options;
import org.smartfrog.services.cddlm.api.Processor;
import org.smartfrog.services.cddlm.cdl.ResourceLoader;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types.NotificationInformationType;
import org.smartfrog.services.cddlm.generated.api.types.OptionMapType;
import org.smartfrog.services.cddlm.generated.api.types.OptionType;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * created Sep 1, 2004 6:00:41 PM
 */

public class DeployTest extends DeployingTestBase {


    public void testDescriptorCreation() throws Exception {
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        assertInDescriptor(dd, DeploySmartFrogTest.SFCONFIG_EXTENDS_COMPOUND);
    }

    public void testDescriptorInstream() throws Exception {
        ResourceLoader loader = new ResourceLoader();
        InputStream in = loader.loadResource(
                "org/smartfrog/services/cddlm/components.sf");
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(in);
        assertInDescriptor(dd, "axis/services/cddlm?wsdl");
    }


    public void testEmptyDeploy() throws Exception {
        deployExpectingFault(null,
                null,
                null,
                null,
                DeployApiConstants.FAULT_BAD_ARGUMENT,
                null);
    }

    public void testBrokenDeploy() throws IOException {
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(
                DeploySmartFrogTest.BROKEN_DESCRIPTOR);
        deployExpectingFault(null,
                dd,
                null,
                null,
                DeployApiConstants.FAULT_DEPLOYMENT_FAILURE,
                null);
    }

    public void testUnsupportedLanguage() throws RemoteException,
            URI.MalformedURIException {
        MessageElement me = operation.createSmartfrogMessageElement(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        String nsURI = "http://invalid.example.org";
        me.setNamespaceURI(nsURI);
        DeploymentDescriptorType dd = operation.createDescriptorWithXML(me,
                new URI(me.getNamespaceURI()), null);
        deployExpectingFault(null,
                dd,
                null,
                null,
                DeployApiConstants.FAULT_UNSUPPORTED_LANGUAGE,
                null);
    }

    public void testNoLanguage() throws RemoteException,
            URI.MalformedURIException {
        MessageElement me = operation.createSmartfrogMessageElement(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        DeploymentDescriptorType dd = operation.createDescriptorWithXML(me,
                null, null);
        deployExpectingFault(null,
                dd,
                null,
                null,
                DeployApiConstants.FAULT_BAD_ARGUMENT,
                Processor.ERROR_NO_LANGUAGE_DECLARED);
    }

    /**
     * change the version to sfrog and see what happens
     *
     * @throws RemoteException
     */
    public void testUnsupportedSmartFrogVersion() throws Exception {
        MessageElement me = operation.createSmartfrogMessageElement(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        me.removeAttribute("version");
        me.setAttribute(me.getNamespaceURI(), "version", "1.7");
        DeploymentDescriptorType dd = operation.createDescriptorWithXML(me,
                new URI(DeployApiConstants.SMARTFROG_NAMESPACE),
                "1.7");
        deployExpectingFault(null,
                dd,
                null,
                null, DeployApiConstants.FAULT_UNSUPPORTED_LANGUAGE,
                null);
    }

    /**
     * change the version to sfrog and see what happens
     *
     * @throws RemoteException
     */
    public void testUndefinedSmartFrogVersion() throws Exception {
        MessageElement me = operation.createSmartfrogMessageElement(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        me.removeAttribute("version");
        DeploymentDescriptorType dd = operation.createDescriptorWithXML(me,
                new URI(DeployApiConstants.SMARTFROG_NAMESPACE),
                null);
        deployExpectingFault(null,
                dd,
                null,
                null,
                DeployApiConstants.FAULT_UNSUPPORTED_LANGUAGE,
                null);
    }

    /**
     * test this callback option is not recognised
     *
     * @throws IOException
     */
    public void testUnsupportedCallback() throws IOException {
        NotificationInformationType callback = new NotificationInformationType();
        callback.setType(DeployApiConstants.URI_CALLBACK_WS_EVENTING);
        DeploymentDescriptorType dd = operation.createSmartFrogDescriptor(
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        deployExpectingFault(null,
                dd,
                null,
                callback,
                DeployApiConstants.FAULT_UNSUPPORTED_CALLBACK,
                null);
    }

    public void testOptionCreation() throws IOException {
        Options options = new Options();
        OptionType option = options.createNamedOption(
                new URI("http://localhost/1"), true);
        assertTrue(option.getMustUnderstand().booleanValue());
        option =
                options.createNamedOption(new URI("http://localhost/2"),
                        false);
        assertFalse(option.getMustUnderstand().booleanValue());
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
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        deployExpectingFault(null,
                dd,
                options,
                null,
                DeployApiConstants.FAULT_NOT_UNDERSTOOD,
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
                DeploySmartFrogTest.SIMPLE_DESCRIPTOR);
        URI uri = deploy(null, dd, options, null);
        undeploy(uri);
    }


    public void NotestDeployInvalidURL() throws Exception {
        DeploymentDescriptorType descriptor = new DeploymentDescriptorType();
        descriptor.setReference(new URI("http://localhost/invalid.sf"));
        URI uri = deploy(null, descriptor, null, null);
        undeploy(uri);
    }
}
