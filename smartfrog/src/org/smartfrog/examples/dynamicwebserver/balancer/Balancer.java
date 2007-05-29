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

package org.smartfrog.examples.dynamicwebserver.balancer;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * <p>
 * Description: Balancer defines the remote interface to the balancer
 * component, allowing a client to add to or remove servers from the set of
 * servers that the balancer uses to balance the load from clients.
 * </p>
 *
 */
public interface Balancer extends Remote {
    //
    // Definition of SmartFrog parameter names for the component
    public final String NAME = "name";
    public final String PORT = "port";
    public final String HOSTSPORT = "hostsPort";
    public final String HOSTS = "hosts";
    public final String LOGTO = "logTo";

    /**
     * Add a server to the balancer server set.
     *
     * @param hostname The host name of the new server
     * @param port The port number on the new server used open the connection
     *        from the balancer
     * @throws RemoteException in case of Remote/network error
     */
    public void addServer(String hostname, int port) throws RemoteException;

    /**
     * Add a server to the balancer server set.
     *
     * @param hostname The host name of the new server, using the hostsPort
     *        Port
     * @throws RemoteException in case of Remote/network error
     */
    public void addServer(String hostname) throws RemoteException;

    /**
     * Remove a server from the balancer server set.
     *
     * @param hostname The host name of the server to remove from the set
     * @throws RemoteException in case of Remote/network error
     */
    public void removeServer(String hostname) throws RemoteException;
}
