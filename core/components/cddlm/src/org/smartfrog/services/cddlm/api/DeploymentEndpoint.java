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
package org.smartfrog.services.cddlm.api;

import org.smartfrog.services.cddlm.generated.api.types._deployResponse;
import org.smartfrog.services.cddlm.generated.api.types._deployRequest;
import org.smartfrog.services.cddlm.generated.api.types._undeployRequest;
import org.smartfrog.services.cddlm.generated.api.types.ServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types._serverStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types._applicationStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types._lookupApplicationRequest;
import org.smartfrog.services.cddlm.generated.api.types.StaticServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.DynamicServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.ServerInformationType;
import org.smartfrog.services.cddlm.generated.api.types.LanguageListType;
import org.apache.axis.types.URI;
import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;

/**
 * created Aug 4, 2004 9:49:56 AM
 */

public class DeploymentEndpoint implements org.smartfrog.services.cddlm.generated.api.endpoint.DeploymentEndpoint {

    /**
     * log for everything other than operations
     */
    private Log log=LogFactory.getLog(this.getClass());

    /**
     * log just for operational data
     */
    private Log operations = LogFactory.getLog(this.getClass().getName()+".OPERATIONS");

    private ServerStatusHandler serverStatusHandler=new ServerStatusHandler();

    public _deployResponse deploy(_deployRequest deploy)
            throws RemoteException {
        throwNotImplemented();
        return null;
    }

    public boolean undeploy(_undeployRequest undeploy) throws RemoteException {
        throwNotImplemented();
        return false;
    }

    public ServerStatusType serverStatus(_serverStatusRequest serverStatus)
            throws RemoteException {
        operations.info("entering serverStatus");
        return  serverStatusHandler.serverStatus(serverStatus);
    }

    public ApplicationStatusType applicationStatus(
            _applicationStatusRequest applicationStatus)
            throws RemoteException {
        throwNotImplemented();
        return null;
    }

    public URI lookupApplication(_lookupApplicationRequest lookupApplication)
            throws RemoteException {
        throwNotImplemented();
        return null;
    }

    /**
     * indicate that something is not implemented by throwing a fault
     * @throws AxisFault with an error message
     */
    private void throwNotImplemented() throws AxisFault {
        throw new AxisFault("This feature is not yet implemented");
    }
}
