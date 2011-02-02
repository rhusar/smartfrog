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

package org.smartfrog.examples.orchdws.balancer;


import java.io.IOException;
import java.nio.channels.SocketChannel;

/**

 * <p>
 * Description: Connection is used to maintain an association between the
 * paired sockets connected to the client and server for a single client
 * session.
 * </p>
 *
 */
class Connection {
    private SocketChannel client; // The socket connection to the client
    private SocketChannel server; // The socket connection to the server
    private Server serverHost; // The parent Server object
    boolean terminated; // Has this connection been terminated

    /**
     * Constructor
     * @param client  The socket connection to the client
     * @param server  The socket connection to the server
     * @param serverHost The parent Server object
     */
    Connection(SocketChannel client, SocketChannel server, Server serverHost) {
        this.client = client;
        this.server = server;
        this.serverHost = serverHost;

        terminated = false;
    }

    /**
     * Get socket connection to the client
     * @return  The socket connection to the client
     */
    SocketChannel getClientSocket() {
        return client;
    }

    /**
     * Get socket connection to the server
     * @return  The socket connection to the server
     */
    SocketChannel getServerSocket() {
        return server;
    }

    /**
     * Get parent Server Object
     * @return The parent Server object
     */
    Server getServer() {
        return serverHost;
    }

    /**
     * If not already terminated, close the client and server socket
     * connections.
     */
    void terminate() {
        if (!terminated) {
            //Logger.log("Terminate connection " + toString());
            // Explicitly close the connections
            try {
                if (client.isOpen()) {
                    client.close();
                }
            } catch (IOException ioe) {
                // Ignore
                //Logger.err("Error closing channels: " + ioe.getMessage());
            }

            try {
                if (server.isOpen()) {
                    server.close();
                }
            } catch (IOException ioe) {
                // Ignore
                //Logger.err("Error closing channel: " + ioe.getMessage());
            }

            terminated = true;

            // Tell the Server to clean up its state
            serverHost.connectionClosed(this);
        }
    }

    /**
     * Is connection closed?
     * @return boolean
     */
    boolean isTerminated() {
        return terminated;
    }

    /**
     * Returns textual representation.
     *
     * @return textual representation
     */
    public String toString() {
        return "Connection from " + client + " to " + server;
    }
}
