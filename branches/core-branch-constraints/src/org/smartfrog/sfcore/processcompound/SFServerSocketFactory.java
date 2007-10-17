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

package org.smartfrog.sfcore.processcompound;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.rmi.server.RMIServerSocketFactory;


/**
 * A server socket factory that adapts a  server socket factory so that we can bind to a particular ip address and
 * can be used to generate RMI server sockets.
 *
 */
public class SFServerSocketFactory implements RMIServerSocketFactory {


    private final InetAddress bindAddr;

    public int hashCode() {
        return bindAddr.hashCode();
    }
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SFServerSocketFactory other = (SFServerSocketFactory) obj;
        return bindAddr.equals(other.bindAddr);
    }


    /**
     * Constructs SFServerSocketFactory with security environment.
     * <P>
     * If the bind address is <code>null</code>, then the system will pick up
     * an ephemeral port and a valid local address to bind the socket.
     * <P>
     * @param bindAddr bind address for the server socket
     *
     */
    public SFServerSocketFactory(InetAddress bindAddr) {
        this.bindAddr = bindAddr;
    }

    /**
     * Create a server socket on the specified port (port 0 indicates an
     * anonymous port).
     *
     * @param port The port number
     *
     * @return The server socket in the specified port
     *
     * @throws IOException Cannot create server socket.
     */
    public ServerSocket createServerSocket(int port) throws IOException {
        /* We just configure the final socket and not this server socket,
         * for this reason we don't need a SSLServerSocketFactory.
         * However, we have to wrap it to pass the security
         * context. */
        return new ServerSocket(port, 0, bindAddr);
    }
}
