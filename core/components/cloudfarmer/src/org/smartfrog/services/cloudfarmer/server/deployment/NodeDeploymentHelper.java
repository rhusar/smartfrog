/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.server.deployment;

import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.sfcore.security.SecureRemoteObject;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created 05-Jan-2010 15:23:31
 */

public class NodeDeploymentHelper {

    /**
     * Export an instance
     * @param service service instance to export 
     * @return the exported reference
     * @throws RemoteException RMI problems
     * @throws SmartFrogException any wrapped security problems
     */
    public static NodeDeploymentService export(NodeDeploymentService service) throws RemoteException,
            SmartFrogException {
        try {
            return (NodeDeploymentService) SecureRemoteObject.exportObject(service, 0);
        } catch (SFGeneralSecurityException e) {
            throw new SmartFrogException("Failed to deploy " + service + ": " + e, e);
        }
    }
}
