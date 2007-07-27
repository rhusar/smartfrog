/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.initialization;

import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.UnimplementedTestBase;
import org.smartfrog.services.deployapi.test.system.alpine.deployapi.api.StandardTestBase;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.ggf.cddlm.generated.api.CddlmConstants;
import nu.xom.Document;
import nu.xom.Element;

/**
 * created 13-Apr-2006 13:51:02
 * Create a system , then destroy it immediately.
 */

public class Api_27_initialize_bad_url_Test extends StandardTestBase {

    public Api_27_initialize_bad_url_Test(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        createSystem(null);
    }

    public void testHostnameThatDoesntResolve() throws Exception {
        assertDeploymentFails("http://noname.example.org");
    }

    public void testNonXMLFile() throws Exception {
        assertDeploymentFails("http://www.ggf.org/");
    }

    public void testPasswordFile() throws Exception {
        assertDeploymentFails("http://www.ggf.org/");
    }

    private void assertDeploymentFails(String url) {
        try {
            deployCdlURL(url);
            fail("Expected deployment of "+url+" to fail");
        } catch (Exception e) {
            //success
        }
    }
}

