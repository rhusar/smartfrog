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
package org.smartfrog.services.hadoop.components.cluster;

import org.apache.hadoop.util.Service;
import org.smartfrog.services.hadoop.conf.ClusterBound;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for probing the state of a service
 */


public interface HadoopService extends Remote, ClusterBound {

    /**
     * Test for the service being live
     * @return true if the service is live
     * @throws RemoteException for RMI problems
     */
    boolean isServiceLive() throws RemoteException;

    /**
     * Get the service state
     * @return the current service state, or UNDEFINED if there is no service
     * @throws RemoteException for RMI problems
     */
    Service.ServiceState getServiceState() throws RemoteException;

    /**
     * Ping the service and get its service state
     * @return the current service status, or null if there is no service
     * @throws RemoteException for RMI problems
     * @throws IOException for pinging problems
     */
    Service.ServiceStatus pingService() throws IOException;

    /**
     * Gets the description of a service
     * @return a description string of the service
     * @throws RemoteException network problems
     */
    String getDescription() throws RemoteException ;

}
