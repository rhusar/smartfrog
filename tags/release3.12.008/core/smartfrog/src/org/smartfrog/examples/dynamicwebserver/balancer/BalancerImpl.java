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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.DataSource;
import org.smartfrog.examples.dynamicwebserver.logging.LogWrapper;
import org.smartfrog.examples.dynamicwebserver.logging.Logger;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * <p>
 * Title: BalancerImpl.
 * </p>
 *
 * <p>
 * Description: BalancerImpl is the main class for a software load balancer for
 * socket connections between a set of clients and a set of servers. The
 * clients connect to a specific port on the balancer host and the balancer
 * selects a server to handle the client session. The balancer opens a socket
 * connection to the selected server and relays data between the client and
 * the server. The balancer listens for client connections on a well-known
 * port, specified by the "port" SmartFrog parameter.
 * </p>
 *
 * <p>
 * The Balancer maintains a set of servers that are included in the selection
 * algorithm for balancing. The curent selection algorithm is round-robin. An
 * initial set of servers can be specified via the "hosts" SmartFrog
 * parameter; if "hosts" is specified, the parameter "hostsPort" is used to
 * specify the port number to connect to on the servers. Subsequent changes to
 * the set of servers can be made at run-time via the Balancer remote
 * interface; in this case the port number for each host can be specified
 * individually.
 * </p>
 *
 */
public class BalancerImpl extends PrimImpl implements Prim, Balancer, DataSource {
    //
    // Other instance variables
    private ServerSelector serverSelector; // Object that does the load balancing to select a server from a list
    private volatile BalancerThread balancerThread = null; // The main Balancer thread
    private volatile boolean stopping = false; // Used to signal the Balancer server thread to terminate
    private LogWrapper logger; // used to log through the logging component if one exists
    private String name;
    private int port; // port number to register balancer server socket
    private int hostsPort; // port number for connections to remote hosts
    private Vector hosts; // initial set of hosts to include in the balancer set
    private int connectionCount = 0;  // number of connections made since last time this was accessed through getdata()

    /**
     * Constructor for the Balancer component
     *
     * @throws RemoteException in case of Remote/network error
     */
    public BalancerImpl() throws RemoteException {
    }

    /**
     * Implementation of the Balancer interface
     *
     * @param hostname The host name of the new server
     * @param serverport The port number on the new server used open the connection
     *        from the balancer
     */
    public void addServer(String hostname, int serverport) {
        if (!stopping) {
            serverSelector.addServer(hostname, serverport);
        }
    }

    /**
     * Implementation of the Balancer interface
     *
     * @param hostname The host name of the new server
     */
    public void addServer(String hostname) {
        if (!stopping) {
            serverSelector.addServer(hostname, hostsPort);
        }
    }

    /**
     * Implementation of the Balancer interface
     *
     * @param hostname The host name of the server to remove from the set
     */
    public void removeServer(String hostname) {
        serverSelector.removeServer(hostname);
    }


    /** implementation of DataSource interface
     *  resets the data after every request - so only suited to single requestor
     *
     * @return the number of connections since last request
     * @throws RemoteException in case of Remote/network error
     */
    public int getData() throws RemoteException {
        int tmp = connectionCount;
        connectionCount = 0;
        return tmp;
    }


    /**
     * Start the load balancer.
     */
    private synchronized void start() {
        stopping = false;

        // Start the Server Selector
        serverSelector = new ServerSelector();
        serverSelector.start();

        //
        //Create a thread to accept new connections
        if (balancerThread == null) {
            balancerThread = new BalancerThread();
            balancerThread.start();
        }

        //
        // If any remote hosts were defined in hosts, add them to the list
        if (hosts != null) {
            for (Enumeration hostlist = hosts.elements(); hostlist.hasMoreElements();) {
                String hostname = (String) hostlist.nextElement();
                addServer(hostname, hostsPort);
            }
        }
    }

    /**
     * Stop the load balancer.
     */
    public synchronized void stop() {
        stopping = true;

        //
        // Stop accepting new connections.
        if (balancerThread != null) {
            balancerThread.stop();
            balancerThread = null;
        }

        //
        // Close all open connections and terminate all threads.
        serverSelector.close();
    }

    /**
     * Used to start the component from the command line.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            BalancerImpl balancer = new BalancerImpl();

            // Start balancing
            balancer.start();

            for (int count = 0; count < args.length; count += 2) {
                // For testing, add a set of servers
                balancer.addServer(args[count],
                    Integer.parseInt(args[count + 1]));
            }
        } catch (RemoteException re) {
        }
    }

    //
    // SmartFrog Component interface
    //

    /**
     * Returns textual representation.
     *
     * @return textual representation
     */
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("name: " + name);
        str.append(", port: " + port);
        str.append(", hostsPort: " + hostsPort);
        str.append(", hosts: " + hosts);

        return (str.toString());
    }

    /**
     * Reads SF description.
     *
     * @throws SmartFrogException error while reading
     * @throws RemoteException In case of network/rmi error
     */
    private void readSFAttributes() throws SmartFrogException, RemoteException {
        //
        // Optional attributes.
        //
        logger = new LogWrapper((Logger) sfResolve(LOGTO, false));
        name = sfCompleteName().toString();

        hosts = sfResolve(HOSTS, hosts, false);
        port = sfResolve(PORT, port, false);
        hostsPort = sfResolve(HOSTSPORT, hostsPort, false);

        logger.log(name, this.toString());
    }

    /**
     * Standard sfDeploy().
     *
     * @exception SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        //  any component-specific init code
        readSFAttributes();

        // Simply invoke the instance start method
	// This needs to be done here, so that others can invoke the addServer methods, etc
	// during their sfStarts.

        start();

        // Any error - propagate and hance fail to deploy
    }

    /**
     * Standard sfStart().
     *
     * @exception SmartFrogException error while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    /**
     * Standard sfTerminateWith()
     *
     * @param tr TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        // Simply invoke the instance stop method
        stop();
        super.sfTerminateWith(tr);
    }

    /**
     * The main thread of the Balancer.
     */
    private class BalancerThread implements Runnable {
        private Thread thread = null;
        private volatile boolean stopRequested = false;

        /**
         * Constructor
         */
        BalancerThread() {
        }

        /**
         * Start the thread
         */
        void start() {
            if (thread == null) {
                // Create writer thread.
                thread = new Thread(this, "BalancerThread");
                thread.start();
            }
        }

        /**
         * Stop the thread
         */
        void stop() {
            if (thread != null) {
                stopRequested = true;
                thread.interrupt();

                try {
                    thread.join();
                } catch (InterruptedException ie) {
                }

                thread = null;
            }
        }

        /**
         * Accept new connections from clients and perform balancing to connect
         * them to one of the servers.
         */
        public void run() {
        	ServerSocketChannel server = null; // Server socket to accept connections from clients
        	try {

        		try {
        			server = ServerSocketChannel.open();

        			InetSocketAddress address = new InetSocketAddress(port);
        			server.socket().bind(address);
        		} catch (IOException e) {
        			logger.err(name, "Error creating server socket: " + e.getMessage());

        			// Exit thread because encountered a severe problem
        			return;
        		}

        		//
        		// Now loop to wait for connections from clients.

        		while (!stopRequested) {
        			try {
        				SocketChannel client = server.accept();
        				connectionCount += 1;
        				logger.log(name, "Accepted connection from " + client);

        				// Select a server to handle the new client socket, and hand off processing to it.
        				serverSelector.addClient(client);
        			} catch (IOException ioe) {
        				logger.err(name,
        						"Error accepting connection from client" +
        						ioe.getMessage());
        			}
        		}

        		//

        	} finally {
        		// Clean up the socket when we stop.
        		try {
        			if( server != null) {
        				server.close();
        			}
        		} catch (IOException ioe) {
        			logger.err(name,
        					"Error closing server socket " + ioe.getMessage());
        		} 	
        		//and remove our self-reference
        		balancerThread = null;
        	}
        }
    }
}
