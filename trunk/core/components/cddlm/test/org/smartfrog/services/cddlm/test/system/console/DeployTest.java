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

/**
 * created Sep 1, 2004 6:00:41 PM
 */

public class DeployTest extends ConsoleTestBase {

    private Deploy operation;

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        operation = new Deploy(getBinding(), getOut());
    }

    public void testDeployInvalidURL() throws Exception {
        DeploymentDescriptorType descriptor = new DeploymentDescriptorType();
        descriptor.setSource(new URI("http://localhost/invalid.sf"));
        operation.deploy("invalid", descriptor, null);
    }
}
