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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * <p>
 * Description: ServerSelector is the class used to select a Server to handle a
 * new connection from a client, and open a socket connection to the selected
 * server. The Server selection algorithm is round-robin.
 * </p>
 *
 */
class ServerSelector {
    private static int roundRobinServerIndex = 0; // Index to use for selecting server
    private Hashtable serversMap = new Hashtable(); // Mapping from server hostname to corresponding Server instance
    private Vector servers = new Vector(); // Set of servers to choose from

    ServerSelector() {
    }

    private synchronized Server selectServer() {
        // Increment to next server
        roundRobinServerIndex++;

        if (roundRobinServerIndex >= servers.size()) {
            roundRobinServerIndex = 0;
        }

        Server server = null;

        if (servers.size() > 0) {
            server = (Server) servers.elementAt(roundRobinServerIndex);
        }

        return server;
    }

    /**
     * Create a new connection for the newly created client socket.
     *
     * @param clientChannel
     */
    private void createConnection(SocketChannel clientChannel) {
        // Select a server to handle requests for this client
        Server server = selectServer();

        if (server == null) {
            // Could not find a server, so disconnect the client.
            // We assume that the client will try again at a future time
            try {
                //Logger.err("Could not find a server, closing client connection ");
                clientChannel.close();
            } catch (IOException ioe) {
                //Logger.err("Could not close client connection ");
            }
        } else {
            try {
                // Attempt to open a socket connection to the server
                InetSocketAddress serverAddress = server.getAddress();
                SocketChannel serverChannel = SocketChannel.open();
                serverChannel.connect(serverAddress);

                // Make all subsequent operations non-blocking on the server
                serverChannel.configureBlocking(false);

                // Add this new socket pair to the selected server
                server.addNewConnection(clientChannel, serverChannel);
            } catch (IOException ioe) {
                //Logger.err("Could not connect to the server " + server.getHostname() + ", closing client " + ioe.getMessage());
                // Could not connect to server, so disconnect the client.
                // We assume that the client will try again at a future time
                try {
                    //Logger.err("Could not find a server, closing client connection ");
                    clientChannel.close();
                } catch (IOException ioe2) {
                    //Logger.err("Could not close client connection " + ioe2.getMessage());
                }
            }
        }
    }

    /**
     * Start the server selector.
     */
    void start() {
    }

    /**
     * Close all of the servers and all associated threads.
     */
    synchronized void close() {
        Vector tempServers = (Vector) servers.clone();

        for (Enumeration serverEnum = tempServers.elements(); serverEnum.hasMoreElements();) {
            Server server = (Server) serverEnum.nextElement();
            removeServer(server.getHostname());
        }
    }

    /**
     * Used by Balancer to give us a new client
     *
     * @param client DOCUMENT ME!
     */
    void addClient(SocketChannel client) {
        // Start a new thread to do the work of selecting a server and connecting to it
        ServerConnectThread serverConnect = new ServerConnectThread(client);
        Thread thread = new Thread(serverConnect);
        thread.start();
    }

    /**
     * Add a new server to the balanced set.
     *
     * @param hostname Host name of the new server
     * @param port port number to open socket to new server
     */
    synchronized void addServer(String hostname, int port) {
        Server server = new Server(hostname, port);
        serversMap.put(hostname, server);
        servers.add(server);
    }

    /**
     * Remove a server from the balanced set.
     *
     * @param hostname name of the new server
     */
    synchronized void removeServer(String hostname) {
        Server server = (Server) serversMap.remove(hostname);

        if (server != null) {
            // If this is a server we know about, remove it from the system
            servers.remove(server);

            // ... and close it, along with all open connections
            server.close();
        }
    }

    /**
     * Thread that doe the real work of selecting a server and connecting to it
     * when receive a new client connection.
     */
    private class ServerConnectThread implements Runnable {
        SocketChannel client;

        ServerConnectThread(SocketChannel client) {
            this.client = client;
        }

        /**
         * Do the real work of selecting a server and connecting to it.
         */
        public void run() {
            try {
                //Logger.log("Setting client socket to non-blocking");
                client.configureBlocking(false);

                //Logger.log("Select a server and create connection to it");
                createConnection(client);
            } catch (IOException e) {
                //Logger.err("Closing client socket because Error setting client socket to non-blocking mode: " + e.getMessage());
                try {
                    client.close();
                } catch (IOException ioe) {
                    //Logger.err("Error closing client channel: " + ioe.getMessage());
                }
            }
        }
    }
}
