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

import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Vector;


/**
 * <p>
 * Title: Server
 * </p>
 *
 * <p>
 * Description: Server is used to maintain state about a server to be included
 * in the balance set. Server maintains the set of current open Connections to
 * the clients assigned by the balancer to a specific server. It is also
 * responsible for stating and stoping the associated ConnectionRelay
 * instance.
 * </p>
 *
 */
class Server {
    private String hostname; // Name of the server
    private int port; // Port on server
    private InetSocketAddress addr; // Address of server
    private Vector connections = new Vector(); // List of current Connections to clients
    private ConnectionRelay connectionRelay; // Reference to associated ConnectionRelay instance
    private volatile boolean closing = false; // true if Server is in process of closing or is closed

    private static String name="Server";

    /**
     * Constructor for Server
     *
     * @param hostname The host name of the remote server
     * @param port Port number used to connect to the server
     */
    Server(String name, String hostname, int port) {
        this.name = name+"_"+this.name;
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        this.addr = address;
        this.hostname = hostname;
        this.port = port;

        connectionRelay = new ConnectionRelay(this, false);
        connectionRelay.start();
    }

    /**
     * Get the host name of the server
     *
     * @return Host name
     */
    String getHostname() {
        return hostname;
    }

    /**
     * Get the port number used to connect to the server
     *
     * @return Port number
     */
    int getPort() {
        return port;
    }

    InetSocketAddress getAddress() {
        return addr;
    }

    public String toString() {
        return "Server: " + hostname + " port: " + port;
    }

    /**
     * Create a new Connection instance for the new connection and start to
     * handle data movement between the client and the server.
     *
     * @param clientChannel Socket channel to the connected client
     * @param serverChannel Socket channel to the corresponding server
     */
    void addNewConnection(SocketChannel clientChannel,
        SocketChannel serverChannel) {
        if (!closing) {
            // Create data structure to associate the objects, and add to the dataMover
            Connection conn = new Connection(clientChannel, serverChannel, this);
            connections.add(conn);
            connectionRelay.addConnection(conn);
        } else {
            // The server has been removed while waiting to connect, so just close sockets
            try {
                clientChannel.close();
            } catch (IOException ioe) {
                if (sfLog().isErrorEnabled()) sfLog().error("Could not close client socket " + ioe.getMessage(),ioe);
            }

            try {
                serverChannel.close();
            } catch (IOException ioe) {
                if (sfLog().isErrorEnabled()) sfLog().error("Could not close server socket " + ioe.getMessage());
            }
        }
    }

    /**
     * Close all open connections to the server, stop realaying data to the
     * server, and stop all associated threads. After this method has been
     * called, the Server will not accept new connections.
     */
    void close() {
        closing = true;

        Vector tempConnections = (Vector) connections.clone();

        // Close all open connections
        for (Enumeration connectionEnum = tempConnections.elements(); connectionEnum.hasMoreElements();) {
            Connection connection = (Connection) connectionEnum.nextElement();
            connection.terminate();
        }

        // Stop sending data
        if (connectionRelay != null) {
            connectionRelay.stop();
            connectionRelay = null;
        }
    }

    /**
     * Callback used by the Connection.terminate when it has closed a
     * connection.
     *
     * @param conn
     */
    void connectionClosed(Connection conn) {
        connections.remove(conn);
        connectionRelay.connectionClosed(conn);

        if (sfLog().isInfoEnabled()) sfLog().debug("Server Connection closed to " + getHostname() + ": " + getPort() + ". Remaining: " + connections.size());
    }

    /**
     * Callback used by the ConnectionRelay when it has cleaned its state after
     * a connection has closed.
     *
     * @param conn
     */
    void dataTransferConnectionClosed(Connection conn) {
        connections.remove(conn);

        if (sfLog().isDebugEnabled()) sfLog().debug("dataTransferConnectionClosed Connection closed to " + getHostname() + ": " + getPort() + ". Remaining: " + connections.size());
    }

    private LogSF sflog = null;
    /**
     *
     * @return LogSF
     */
    public LogSF sfLog(){
         if (sflog==null) {
             sflog= LogFactory.getLog(name);
         }
         return sflog;
    }
}
