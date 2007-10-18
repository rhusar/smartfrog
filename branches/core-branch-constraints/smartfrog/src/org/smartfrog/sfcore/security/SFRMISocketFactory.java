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

package org.smartfrog.sfcore.security;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;


/**
 * A class that wraps a client and server RMI socket factories.
 */
public class SFRMISocketFactory extends RMISocketFactory {
    /** A rmi factory to create client sockets */
    private RMIClientSocketFactory csf;

    /** A rmi factory to create server sockets */
    private RMIServerSocketFactory ssf;

    /**
     * Constructs a SFRMISocketFactory.
     *
     * @param csf A RMI factory to create client's sockets.
     * @param ssf A RMI factory to create server's sockets.
     */
    public SFRMISocketFactory(RMIClientSocketFactory csf,
        RMIServerSocketFactory ssf) {
        super();
        this.csf = csf;
        this.ssf = ssf;
    }

    /**
     * Creates a client socket connected to the specified host and port.
     *
     * @param host the host name
     * @param port the port number
     *
     * @return a socket connected to the specified host and port.
     *
     * @throws IOException if an I/O error occurs during socket creation
     *
     * @since JDK1.1
     */
    public Socket createSocket(String host, int port) throws IOException {
        return csf.createSocket(host, port);
    }

    /**
     * Create a server socket on the specified port (port 0 indicates an
     * anonymous port).
     *
     * @param port the port number
     *
     * @return the server socket on the specified port
     *
     * @throws IOException if an I/O error occurs during server socket
     *            creation
     *
     * @since JDK1.1
     */
    public ServerSocket createServerSocket(int port) throws IOException {
        return ssf.createServerSocket(port);
    }
}
