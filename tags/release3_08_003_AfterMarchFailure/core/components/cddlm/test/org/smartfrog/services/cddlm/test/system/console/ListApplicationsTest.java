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
import org.cddlm.client.console.ConsoleOperation;
import org.cddlm.client.console.ListApplications;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * created Sep 1, 2004 4:54:12 PM
 */

public class ListApplicationsTest extends ConsoleTestBase {
    private ListApplications operation;

    /**
     * get the operation of this test base
     *
     * @return the current operation
     */
    protected ConsoleOperation getOperation() {
        return operation;
    }

    /**
     * Sets up the fixture, by creating an operation
     */
    protected void setUp() throws Exception {
        super.setUp();
        operation = new ListApplications(getBinding(), getOut());
    }


    public void testExecute() throws RemoteException {
        operation.execute();
        final String text = getOutputBuffer();
        System.out.println(text);
        assertInText(text, ListApplications.DEPLOYED_TEXT);
    }

    public void testValidCount() throws RemoteException {
        URI[] apps = operation.listApplications();
        assertNotNull("null list", apps);
        assertTrue("length is at least zero", apps.length >= 0);
    }

    public void testInnerMain() throws IOException {
        boolean flag = ListApplications.innerMain(createBoundArguments());
        assertTrue("failed", flag);
    }
}
