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

import org.smartfrog.services.deployapi.client.Deploy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**

 */
public class DeployCommandTest extends ApiTestBase {

    public DeployCommandTest(String name) {
        super(name);
    }


    public void testMain() throws Exception {

        boolean b = deployResource(RESOURCE_ECHO);
        assertTrue(b);

    }

    private boolean deployResource(String resource) throws
            IOException {
        File file =resourceToTempFile(resource);
        List<String> args=new ArrayList<String>();
        args.add(getBinding().toCommandLineElement());
        args.add(file.getAbsolutePath());
        String args2[]=toStringArray(args);
        boolean b;
        try {
            b= Deploy.innerMain(args2);
        } finally {
            file.delete();
        }
        return b;
    }

    public void testMainFails() throws Exception {

        boolean b = deployResource(RESOURCE_FAILTODEPLOY);
        assertFalse(b);
    }    
}
