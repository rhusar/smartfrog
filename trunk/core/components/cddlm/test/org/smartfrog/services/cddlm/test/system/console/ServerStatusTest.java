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


package org.smartfrog.services.cddlm.test.system.console;

import org.apache.axis.types.URI;
import org.cddlm.client.console.ShowServerStatus;
import org.cddlm.client.generated.api.types.DynamicServerStatusType;
import org.cddlm.client.generated.api.types.ServerInformationType;
import org.cddlm.client.generated.api.types.ServerStatusType;
import org.cddlm.client.generated.api.types.StaticServerStatusType;

import java.rmi.RemoteException;

/**
 * Date: 01-Sep-2004 Time: 11:16:43
 */
public class ServerStatusTest extends ConsoleTestBase {

    private ShowServerStatus operation;

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        operation = new ShowServerStatus(getBinding(), getOut());
    }

    public void testStatus() throws RemoteException {
        operation.execute();
        System.out.println(getOutputBuffer());
    }

    public void testLanguages() throws RemoteException {
        StaticServerStatusType staticStatus = getStaticStatus();
        assertTrue("Languages>0",
                staticStatus.getLanguages().getLanguage().length > 0);
    }

    public void testCallbacks() throws RemoteException {
        StaticServerStatusType staticStatus = getStaticStatus();
        assertTrue("Callbacks>0",
                staticStatus.getCallbacks().getCallback().length > 0);
    }


    public void testServerInfo() throws RemoteException {
        StaticServerStatusType staticStatus = getStaticStatus();
        ServerInformationType serverInfo = staticStatus.getServer();
        assertNotEmpty("name empty", serverInfo.getName());
        assertNotEmpty("location empty", serverInfo.getLocation());
        assertNotEmpty("build empty", serverInfo.getBuild());
        URI home = serverInfo.getHome();
        assertNotNull("home empty", home);
        assertNotNull("UTC offset", serverInfo.getTimezoneUTCOffset());
    }

    private StaticServerStatusType getStaticStatus() throws RemoteException {
        ServerStatusType status = operation.getStatus();
        StaticServerStatusType staticStatus = status.get_static();
        return staticStatus;
    }

    private DynamicServerStatusType getDynamicStatus() throws RemoteException {
        ServerStatusType status = operation.getStatus();
        return status.getDynamic();
    }

}
