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
package org.smartfrog.services.cloudfarmer.server.common;

import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created 05-Oct-2009 16:39:45
 */


public interface ClusterRole extends Remote {


    /**
     * {@value}
     */
    String ATTR_NAME = "name";
    /**
     * {@value}
     */
    String ATTR_DESCRIPTION = "description";
    /**
     * {@value}
     */
    String ATTR_LONG_DESCRIPTION = "longDescription";
    /**
     * {@value}
     */
    String ATTR_MIN = "min";
    /**
     * {@value}
     */
    String ATTR_MAX = "max";
    /**
     * {@value}
     */
    String ATTR_RECOMMENDED_MIN = "recommendedMin";
    /**
     * {@value}
     */
    String ATTR_RECOMMENDED_MAX = "recommendedMax";

    /**
     * Build a new ClusterRoleInfo instance
     * @return the new instance
     * @throws RemoteException for network problems
     */
    ClusterRoleInfo buildClusterRoleInfo() throws RemoteException;

    ClusterRoleInfo resolveRoleInfo(String name) throws RemoteException, SmartFrogResolutionException;
}
