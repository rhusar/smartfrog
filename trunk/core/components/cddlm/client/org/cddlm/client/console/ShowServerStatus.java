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
package org.cddlm.client.console;

import org.cddlm.client.common.ServerBinding;
import org.cddlm.client.generated.api.endpoint.CddlmSoapBindingStub;
import org.cddlm.client.generated.api.endpoint.DeploymentEndpoint;
import org.cddlm.client.generated.api.types.DynamicServerStatusType;
import org.cddlm.client.generated.api.types.ServerInformationType;
import org.cddlm.client.generated.api.types.ServerStatusType;
import org.cddlm.client.generated.api.types.StaticServerStatusType;
import org.cddlm.client.generated.api.types._serverStatusRequest;

import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * created Aug 31, 2004 4:42:42 PM
 */

public class ShowServerStatus extends ConsoleOperation {

    public ShowServerStatus(ServerBinding binding, PrintWriter out) {
        super(binding, out);
    }

    /**
     * execute this operation, or throw a remote exception
     *
     * @throws java.rmi.RemoteException
     */
    public void execute() throws RemoteException {
        ServerStatusType status = getStatus();
        StaticServerStatusType statInfo = status.get_static();
        DynamicServerStatusType dynInfo = status.getDynamic();
        ServerInformationType serverInfo = statInfo.getServer();
        out.println("server :" +
                serverInfo.getName() +
                " at " +
                serverInfo.getLocation());
        out.println("UTC offset " + serverInfo.getTimezoneUTCOffset());
        out.println("Build " + serverInfo.getBuild());
        String callbacks[] = statInfo.getCallbacks().getCallback();
        out.println("Callbacks: " + callbacks.length + " :-");
        out.println();

    }

    /**
     * get the status
     *
     * @return
     * @throws RemoteException
     */
    public ServerStatusType getStatus() throws RemoteException {
        CddlmSoapBindingStub stub;
        stub = getStub();
        DeploymentEndpoint deployer = stub;
        _serverStatusRequest request = new _serverStatusRequest();
        ServerStatusType status = stub.serverStatus(request);
        return status;
    }


}
