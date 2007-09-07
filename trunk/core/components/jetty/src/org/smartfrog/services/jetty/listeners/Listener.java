/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jetty.listeners;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * An interface for listeners for jetty server 
 * @author Ritu Sabharwal
 */

public interface Listener extends Remote {
    /**
     * attribute name: {@value}
     */
    String LISTENER_PORT = "listenerPort";
    /**
     * attribute name: {@value}
     */
    String SERVER_HOST = "serverHost";

    /**
     * attribute name: {@value}
     */
    String SERVER_NAME = "serverName";

    /**
	 * Add the listener to the http server
     * @param listenerPort port to listen on
     * @param serverHost hostname
     * @throws SmartFrogException
     * @throws RemoteException In case of network/rmi error
     */
    public void addlistener(int listenerPort, String serverHost) throws
            SmartFrogException, RemoteException;
}
