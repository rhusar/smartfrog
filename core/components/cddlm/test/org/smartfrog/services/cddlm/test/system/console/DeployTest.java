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

import org.apache.axis.types.URI;
import org.cddlm.client.console.Deploy;
import org.cddlm.client.generated.api.types.DeploymentDescriptorType;
import org.cddlm.client.generated.api.types._deploymentDescriptorType_data;
import org.cddlm.client.generated.api.types.OptionMapType;
import org.smartfrog.services.cddlm.cdl.ResourceLoader;

import java.io.InputStream;
import java.io.IOException;
import java.rmi.RemoteException;

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
        DeploymentDescriptorType dt = operation.createSmartFrogDescriptor(SIMPLE_DESCRIPTOR);
        assertInDescriptor(dt, SFCONFIG_EXTENDS_COMPOUND);
    }

    public void testDescriptorInstream() throws Exception {
        ResourceLoader loader = new ResourceLoader();
        InputStream in = loader.loadResource("org/smartfrog/services/cddlm/components.sf");
        DeploymentDescriptorType dt = operation.createSmartFrogDescriptor(in);
        assertInDescriptor(dt, "axis/services/cddlm?wsdl");
    }

    private void assertInDescriptor(DeploymentDescriptorType dt, String search) throws Exception {
        _deploymentDescriptorType_data data = dt.getData();
        assertNotNull("data null", data);
        final org.apache.axis.message.MessageElement[] any = data.get_any();
        assertNotNull("data/any null", any);
        assertTrue("any empty", any.length == 1);
        String output = any[0].getAsString();
        assertInText(output, search);
    }

    public void testSimpleDeploy() throws Exception {
        DeploymentDescriptorType dt = operation.createSmartFrogDescriptor(SIMPLE_DESCRIPTOR);
        URI uri = deploy("simple", dt, null);
    }

    public void testEmptyDeploy() throws Exception {
        URI uri = deploy("null", null, null);

    }

    public void testBrokenDeploy() throws IOException {
        DeploymentDescriptorType dt = operation.createSmartFrogDescriptor(BROKEN_DESCRIPTOR);
        URI uri = deploy("broken", dt, null);
    }

    private URI deploy(String name,
                       DeploymentDescriptorType descriptor,
                       OptionMapType options) throws RemoteException {
        URI uri = operation.deploy(name, descriptor, options);
        assertNotNull("uri", uri);
        return uri;
    }

    //TODO
    public void testUnsupportedLanguage() {

    }

    //TODO
    public void testUnsupportedSmartFrogVersion() {

    }

    public void testUnsupportedCallback() {

    }

    public void testNotUnderstoodHeader() {
    }

    public void testIgnoredHeader() {

    }


    public void testDeployInvalidURL() throws Exception {
        DeploymentDescriptorType descriptor = new DeploymentDescriptorType();
        descriptor.setSource(new URI("http://localhost/invalid.sf"));
        operation.deploy("invalid", descriptor, null);
    }
}
