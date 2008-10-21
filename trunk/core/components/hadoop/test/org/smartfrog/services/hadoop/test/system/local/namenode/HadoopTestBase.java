/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.test.system.local.namenode;

import org.smartfrog.services.hadoop.common.HadoopUtils;
import org.smartfrog.test.DeployingTestBase;

/**
 * Created 21-Oct-2008 12:40:30
 */

public class HadoopTestBase extends DeployingTestBase {

    protected boolean checkPort = false;
    protected boolean failOnCheckFailure = false;

    protected int portNumber = 8020;
    protected int connectTimeout = 5000;

    public HadoopTestBase(String name) {
        super(name);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (checkPort && isPortOpen(portNumber)) {
            String message = "Expected port localhost:" + portNumber + " to be closed, but it is open";
            getLog().warn(message);
            if(failOnCheckFailure) {
                fail(message);
            }
        }
    }


    boolean isPortOpen(int port) {
        return HadoopUtils.isLocalPortOpen(port, connectTimeout);
    }

    protected void enablePortCheck() {
        checkPort = true;
    }

    protected void enableFailOnPortCheck() {
        failOnCheckFailure = true;
    }
}
