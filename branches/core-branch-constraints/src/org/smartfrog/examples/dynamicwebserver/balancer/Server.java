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
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * ConnectionRelay moves data between all of the paired client and server
 * socket connections for a specifc Server. It uses asynchronous IO functions
 * to minimize the number of threads in the system. It works by using a
 * selector to detect when either a client or server socket is available for
 * reading. It then reads the data from the socket and attempts to write the
 * data to the paired socket. If the data cannot be written, because the write
 * buffer of the paired socket is full, it deselects the socket just read from
 * from the selector to prevent additional data from being read, and shedules
 * a write operation with the DelayedWriter instance. DelayedWriter waits for
 * the paired socket to become writable again and writes the data to it. When
 * all of the data has been written, the original socket is reenabled for
 * reading to read more data. There is one instance of
 * ConnectionRelay/DelayedWriter for each Server. ConnectionRelay and
 * DelayedWriter each have their own thread to handle data movement between
 * the paired sockets for clients connected to the coresponding server. There
 * 2 threads are started per server in the system.
 */
class ConnectionRelay implements Runnable {
    private Server server; // The parent Server
    private boolean halfClose; // Close reading and writing independently
    private Selector selector; // Selector used to detect data to be read on all sockets for all current Connections
    private Map clients; // Map from client socket to corresponding Connection instance
    private Map servers; // Map from server socket to corresponding Connection instance
    private List newConnections; // List of new connections to be added to the set of active keys in the selector
    private List channelsToReactivate; // Channels to turn on asynchronous OP_READ detection after DelayedWriter completed
    private DelayedWriter delayedWriter; // Object to handle delayed writes caused by fill of write buffer
    private Thread thread;
    private volatile boolean running = true;
    private static final int BUFFER_SIZE = 128 * 1024;

    ConnectionRelay(Server server, boolean halfClose) {
        this.server = server;
        this.halfClose = halfClose;

        try {
            selector = Selector.open();
        } catch (IOException e) {
            //Logger.err("Error creating selector: " + e.getMessage());
        }

        clients = new HashMap();
        servers = new HashMap();
        newConnections = new LinkedList();
        channelsToReactivate = new LinkedList();

        delayedWriter = new DelayedWriter();
    }

    // Start the ConnectionRelay threads
    void start() {
        // Start the delayed writer thread
        delayedWriter.start();

        // Create a thread for ourselves and start it
        thread = new Thread(this, toString());
        thread.start();
    }

    // Stop the ConnectionRelay threads
    void stop() {
        // Stop the delayed writer thread
        delayedWriter.stop();

        running = false;
        selector.wakeup();
    }

    /**
     * Server registers established connections to enable transfer of data
     * across the connection.
     *
     * @param conn DOCUMENT ME!
     */
    void addConnection(Connection conn) {
        // Add connection to a list that will be processed later by calling processNewConnections()
        synchronized (newConnections) {
            newConnections.add(conn);
        }

        // Wakeup the selector so that the new connections get processed
        selector.wakeup();
    }

    /**
     * Process new connections queued up by calls to addConnection() Returns
     * true if it did something.
     *
     * @return DOCUMENT ME!
     */
    private boolean processNewConnections() {
        Iterator iter;
        Connection conn;
        SocketChannel client;
        SocketChannel serverSocket;
        boolean didSomething = false;

        synchronized (newConnections) {
            iter = newConnections.iterator();

            while (iter.hasNext()) {
                conn = (Connection) iter.next();
                iter.remove();

                client = conn.getClientSocket();
                serverSocket = conn.getServerSocket();

                try {
                    //Logger.logOptional("Setting channels to non-blocking mode");
                    client.configureBlocking(false);
                    serverSocket.configureBlocking(false);

                    //Logger.logOptional("Registering channels with selector");
                    client.register(selector, SelectionKey.OP_READ);
                    serverSocket.register(selector, SelectionKey.OP_READ);

                    clients.put(client, conn);
                    servers.put(serverSocket, conn);
                } catch (IOException e) {
                    //Logger.err("Error configuring channels: " + e.getMessage());
                    // There was a problem, so forcibly terminate the connection
                    closeConnection(conn);
                }

                didSomething = true;
            }
        }

        return didSomething;
    }

    /*
     * In the copyData() method, if there is a destination channel which can't immediately
     * write data, the corresponding source channel is deactivated from the selector until
     * DelayedMover is able to transmit all of that delayed data.
     * This method is used by DelayedMover to signify that all of the data from a
     * channel has been sent to its destination, and the channel can be re-activated for reading with
     * the selector.
     */
    private void addToReactivateList(SocketChannel channel) {
        // Add channel to a list that will be processed later by calling processReactivateList()
        synchronized (channelsToReactivate) {
            channelsToReactivate.add(channel);
        }

        // Wakeup the select so that the list gets processed
        selector.wakeup();
    }

    /**
     * Process channels queued up by calls to addToReactivateList() Returns
     * true if it did something.
     *
     * @return DOCUMENT ME!
     */
    private boolean processReactivateList() {
        Iterator iter;
        SocketChannel channel;
        SelectionKey key;
        boolean didSomething = false;

        synchronized (channelsToReactivate) {
            iter = channelsToReactivate.iterator();

            while (iter.hasNext()) {
                channel = (SocketChannel) iter.next();
                iter.remove();

                key = channel.keyFor(selector);

                try {
                    // Add OP_READ back to the interest bits
                    key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                } catch (CancelledKeyException e) {
                    // The channel has been closed. Figure out corresponding Connection if possible and
                    // make sure it is closed.
                    Connection conn = null;

                    if (clients.containsKey(channel)) {
                        conn = (Connection) clients.get(channel);
                        closeConnection(conn);
                    } else if (servers.containsKey(channel)) {
                        conn = (Connection) servers.get(channel);
                        closeConnection(conn);
                    }
                }

                didSomething = true;
            }
        }

        return didSomething;
    }

    /**
     * The main thread of the ConnectionRelay
     */
    public void run() {
        ByteBuffer buffer;
        boolean pncReturn;
        boolean prlReturn;
        int selectFailureOrZeroCount = 0;
        int selectReturn;
        Iterator keyIter;
        SelectionKey key;
        SocketChannel src;
        SocketChannel dst;
        Connection conn = null;
        boolean clientToServer;
        boolean readMore;
        int numberOfBytes;

        buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

        while (running) {
            // Register new connections with the selector
            pncReturn = processNewConnections();

            // Re-activate channels with the selector
            prlReturn = processReactivateList();

            // Reset the failure counter if processNewConnections() or
            // processReactivateList() did something, as that would
            // explain why select would return with zero ready channels.
            if (pncReturn || prlReturn) {
                selectFailureOrZeroCount = 0;
            }

            // If we exceed the threshold of failed selects, pause
            // for a bit so we don't go into a tight loop
            if (selectFailureOrZeroCount >= 10) {
                //Logger.log("select appears to be failing repeatedly, pausing");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                selectFailureOrZeroCount = 0;
            }

            // Now select for any channels that have data to be moved
            selectReturn = 0;

            try {
                selectReturn = selector.select();

                if (selectReturn > 0) {
                    selectFailureOrZeroCount = 0;
                } else {
                    selectFailureOrZeroCount++;
                }
            } catch (IOException e) {
                //Logger.err("Error when selecting for ready channel: " + e.getMessage());
                selectFailureOrZeroCount++;

                continue;
            }

            //Logger.logOptional("select reports " + selectReturn + " channels ready to read");
            // Work through the list of channels that have data to read
            keyIter = selector.selectedKeys().iterator();

            while (keyIter.hasNext()) {
                key = (SelectionKey) keyIter.next();
                keyIter.remove();

                // Work out if this is a client or server socket, and get the corresponding Connection.
                src = (SocketChannel) key.channel();

                if (clients.containsKey(src)) {
                    clientToServer = true;
                    conn = (Connection) clients.get(src);
                    dst = conn.getServerSocket();
                } else if (servers.containsKey(src)) {
                    clientToServer = false;
                    conn = (Connection) servers.get(src);
                    dst = conn.getClientSocket();
                } else {
                    // We've been dropped from the maps, which means the
                    // connection has already been closed.  Nothing to
                    // do except cancel our key (just to be safe) and
                    // move on to the next ready key.
                    key.cancel();

                    continue;
                }

                try {
                    do { // Loop as long as the source has data to read and can write it to the destination.
                        readMore = false;

                        // Try to read data
                        buffer.clear();
                        numberOfBytes = src.read(buffer);

                        //Logger.logOptional("Read " + numberOfBytes + " bytes from " + src);
                        if (numberOfBytes > 0) { // Data was read

                            if (copyData(buffer, src, dst, clientToServer, key,
                                        conn)) {
                                readMore = true;
                            }
                        } else if (numberOfBytes == -1) { // EOF
                            handleEOFonRead(key, src, dst, clientToServer, conn);
                        }
                    } while (readMore);
                } catch (IOException e) {
                    //Logger.err("Error moving data between channels: " + e.getMessage());
                    closeConnection(conn);
                }
            }
        }

        // Finished with the selector now so close it
        try {
            selector.close();
        } catch (IOException ioe) {
            //Logger.err("Error closing selector " + ioe.getMessage());
        }
    }

    /**
     * Copy data drom the source socket to the paired destination socket.
     * Returns true is all of the data in buffer is successfully transmitted
     * to dst, false if some or all of it is delayed.
     *
     * @param buffer DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param dst DOCUMENT ME!
     * @param clientToServer DOCUMENT ME!
     * @param sourceKey DOCUMENT ME!
     * @param conn DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    private boolean copyData(ByteBuffer buffer, SocketChannel src,
        SocketChannel dst, boolean clientToServer, SelectionKey sourceKey,
        Connection conn) throws IOException {

        buffer.flip();

        // Make an effort to send the data on to its destination
        dst.write(buffer);

        // If there is still data in the buffer, hand it off to DelayedMover
        if (buffer.hasRemaining()) {
            //Logger.log("Delaying " + buffer.remaining() + " bytes from " + src + " to " + dst);
            // Copy the delayed data into a temporary buffer
            ByteBuffer delayedBuffer = ByteBuffer.allocate(buffer.remaining());
            delayedBuffer.put(buffer);
            delayedBuffer.flip();

            // De-activate the source channel from the selector by
            // removing OP_READ from the interest bits -
            // don't want to read any more data from the source until we
            // get this delayed data written to the destination.
            try {
                sourceKey.interestOps(sourceKey.interestOps() ^
                    SelectionKey.OP_READ);
                delayedWriter.addToQueue(new DelayedDataInfo(dst,
                        delayedBuffer, src, clientToServer, conn));
            } catch (CancelledKeyException e) {
                // The channel has been closed, so make sure Connection is closed.
                closeConnection(conn);
            }

            return false;
        } else {
            return true;
        }
    }

    /**
     * Handle the case that EOF is detected on a channel
     *
     * @param key DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param dst DOCUMENT ME!
     * @param clientToServer DOCUMENT ME!
     * @param conn DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    private void handleEOFonRead(SelectionKey key, SocketChannel src,
        SocketChannel dst, boolean clientToServer, Connection conn)
        throws IOException {
        if (halfClose) {
            Socket srcSocket;
            Socket dstSocket;

            // Cancel this key, otherwise this channel will repeatedly
            // trigger select to tell us that it is at EOF.
            key.cancel();

            srcSocket = src.socket();
            dstSocket = dst.socket();

            // If the other half of the socket is already shutdown then
            // go ahead and close the socket
            if (srcSocket.isOutputShutdown()) {
                //Logger.log("Closing source socket");
                srcSocket.close();
            }
            // Otherwise just close down the input stream.  This allows
            // any return traffic to continue to flow.
            else {
                //Logger.log("Shutting down source input");
                srcSocket.shutdownInput();
            }

            // Do the same thing for the destination, but using the
            // reverse streams.
            if (dstSocket.isInputShutdown()) {
                //Logger.log("Closing destination socket");
                dstSocket.close();
            } else {
                //Logger.log("Shutting down dest output");
                dstSocket.shutdownOutput();
            }

            // Clean up if both halves of the connection are now closed
            if (srcSocket.isClosed() && dstSocket.isClosed()) {
                cleanup(conn);
            }
        } else {
            // Just close the connection.
            closeConnection(conn);
        }
    }

    /**
     * Callback from Connection to tell us that it is closed
     *
     * @param conn
     */
    void connectionClosed(Connection conn) {
        // Now remove any associated trace of the connection from our internal state
        cleanup(conn);
    }

    private void closeConnection(Connection conn) {
        // Explicitly close the connection
        conn.terminate();
    }

    /**
     * Remove any associated trace of the specified connection from the
     * internal state.
     *
     * @param conn DOCUMENT ME!
     */
    private void cleanup(Connection conn) {
        SocketChannel clientChannel = conn.getClientSocket();
        SocketChannel serverChannel = conn.getServerSocket();

        clients.remove(clientChannel);
        servers.remove(serverChannel);

        // Inform the delayedWriter about the closure
        delayedWriter.connectionClosed(conn);

        // Inform ths server that the connection has closed
        server.dataTransferConnectionClosed(conn);
    }

    public String toString() {
        return getClass().getName() + " for " + server.getHostname() + ":" +
        server.getPort();
    }

    /**
     * This class is uded to schedule writes that could not be completed
     * because the write buffer is full. It has its own thread and Selector to
     * handle these delayed write operations.
     */
    private class DelayedWriter implements Runnable {
        private Selector delayedSelector;
        private List writeQueue;
        private Map delayedInfo; // Map from write socket to corresponding DelayedDataInfo instance
        private Thread writerThread;
        private volatile boolean isRunning = true; // Used to cleanly stop the thread

        private DelayedWriter() {
            try {
                delayedSelector = Selector.open();
            } catch (IOException e) {
                //Logger.err("Error creating selector: " + e.getMessage());
            }

            writeQueue = new LinkedList();
            delayedInfo = new HashMap();
        }

        /**
         * Start the DelayedWriter thread.
         */
        void start() {
            // Create a thread for ourselves and start it
            writerThread = new Thread(this, toString());
            writerThread.start();
        }

        /**
         * Stop the DelayedWriter thread.
         */
        void stop() {
            isRunning = false;
            delayedSelector.wakeup();
        }

        /*
         * Used by requestMoreResources to register a destination.
         */
        void addToQueue(DelayedDataInfo info) {
            // Add channel to a list that will be processed later by calling processQueue()
            synchronized (writeQueue) {
                writeQueue.add(info);
            }

            // Wakeup the select so that the new connection list gets processed
            delayedSelector.wakeup();
        }

        /*
         * Process the list created by addToQueue()
         */
        private boolean processQueue() {
            Iterator iter;
            DelayedDataInfo info;
            SocketChannel dst;
            SelectionKey key;
            boolean didSomething = false;

            synchronized (writeQueue) {
                iter = writeQueue.iterator();

                while (iter.hasNext()) {
                    info = (DelayedDataInfo) iter.next();
                    iter.remove();

                    dst = info.getDest();

                    Connection conn = info.getConnection();

                    if (conn.isTerminated()) {
                        // The connection closed while we were waiting
                        continue;
                    }

                    // Store the DelayedDataInfo for later use
                    synchronized (delayedInfo) {
                        delayedInfo.put(dst, info);
                    }

                    // Check to see if we already have a key registered for this channel.
                    key = dst.keyFor(delayedSelector);

                    if (key == null) {
                        // No key already registered so register a new one.
                        //Logger.logOptional("Registering channel with delayed writer selector");
                        try {
                            dst.register(delayedSelector, SelectionKey.OP_WRITE);
                        } catch (ClosedChannelException e) {
                            // If the channel is already closed, make sure we have cleaned up.
                            //Logger.err("ClosedChannelException when register channel " + e.getMessage());
                            closeConnection(conn);
                        }
                    } else {
                        // Already have a key registered, make sure it has the right interest bits.
                        try {
                            key.interestOps(key.interestOps() |
                                SelectionKey.OP_WRITE);
                        } catch (CancelledKeyException e) {
                            // If the channel is already closed, make sure we have cleaned up.
                            //Logger.err("CancelledKeyException when change interestOps channel " + e.getMessage());
                            closeConnection(conn);
                        }
                    }

                    didSomething = true;
                }
            }

            return didSomething;
        }

        public void run() {
            int selectReturn;
            int selectFailureOrZeroCount = 0;
            boolean pqReturn;
            Iterator keyIter;
            SelectionKey key;
            SocketChannel dst;
            DelayedDataInfo info;
            Connection conn;
            ByteBuffer delayedBuffer;
            int numberOfBytes;
            SocketChannel src;

            while (isRunning) {
                // Register any new connections with the selector
                pqReturn = processQueue();

                // Reset the failure counter if processQueue() did
                // something, as that would explain why select would
                // return with zero ready channels.
                if (pqReturn) {
                    selectFailureOrZeroCount = 0;
                }

                // If we exceed the threshold of failed selects, pause
                // for a bit so we don't go into a tight loop
                if (selectFailureOrZeroCount >= 10) {
                    //Logger.log("select appears to be failing repeatedly, pausing");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }

                    selectFailureOrZeroCount = 0;
                }

                // Now select for any channels that are ready to write
                try {
                    selectReturn = delayedSelector.select();

                    if (selectReturn > 0) {
                        selectFailureOrZeroCount = 0;
                    } else {
                        selectFailureOrZeroCount++;
                    }
                } catch (IOException e) {
                    //Logger.err("Error when selecting for ready channel: " + e.getMessage());
                    selectFailureOrZeroCount++;

                    continue;
                }

                //Logger.logOptional("select reports " + selectReturn + " channels ready to write");
                // Work through the list of channels that are ready to write
                keyIter = delayedSelector.selectedKeys().iterator();

                while (keyIter.hasNext()) {
                    key = (SelectionKey) keyIter.next();
                    keyIter.remove();

                    dst = (SocketChannel) key.channel();

                    synchronized (delayedInfo) {
                        info = (DelayedDataInfo) delayedInfo.get(dst);
                    }

                    delayedBuffer = info.getBuffer();
                    conn = info.getConnection();

                    try {
                        numberOfBytes = dst.write(delayedBuffer);

                        //Logger.logOptional("Wrote " + numberOfBytes + " delayed bytes to " + dst + ", " + delayedBuffer.remaining() + " bytes remain delayed");
                        // If the buffer is now empty, we're done with this channel.
                        if (!delayedBuffer.hasRemaining()) {
                            // The delayed write Key seems to prevent the socket from being closed cleanly,
                            // so delete it here, and create a new one later if required
                            key.cancel(); // yyy

                            /*
                               try {
                                   key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
                               } catch (CancelledKeyException e) {
                                   // If the channel is already closed, make sure we have cleaned up.
                                   //Logger.err("Cancelled key exception " + e.getMessage());
                                   closeConnection(conn);
                               }
                             */
                            src = info.getSource();
                            cleanSocketState(info.getDest());
                            addToReactivateList(src);
                        }
                    } catch (IOException e) {
                        //Logger.err("Error writing delayed data: " + e.getMessage());
                        closeConnection(conn);
                    }
                }
            }

            // Finished with the selector now so close it
            try {
                delayedSelector.close();
            } catch (IOException ioe) {
                //Logger.err("Error closing delayedSelector " + ioe.getMessage());
            }
        }

        void connectionClosed(Connection conn) {
            //Logger.log("DelayedWriter connectionClosed for " + conn.getServer().toString());
            cleanSocketState(conn.getClientSocket());
            cleanSocketState(conn.getServerSocket());
        }

        private void cleanSocketState(SocketChannel dst) {
            //Logger.log("DelayedWriter cleanSocketState " + dst);
            synchronized (delayedInfo) {
                delayedInfo.remove(dst);
            }
        }

        public String toString() {
            return getClass().getName() + " for " + server.getHostname() + ":" +
            server.getPort();
        }
    }

    private class DelayedDataInfo {
        private SocketChannel dst;
        private ByteBuffer buffer;
        private SocketChannel src;
        private boolean clientToServer;
        private Connection conn;

        DelayedDataInfo(SocketChannel dst, ByteBuffer buffer,
            SocketChannel src, boolean clientToServer, Connection conn) {
            this.dst = dst;
            this.buffer = buffer;
            this.src = src;
            this.clientToServer = clientToServer;
            this.conn = conn;
        }

        SocketChannel getDest() {
            return dst;
        }

        ByteBuffer getBuffer() {
            return buffer;
        }

        SocketChannel getSource() {
            return src;
        }

        boolean isClientToServer() {
            return clientToServer;
        }

        Connection getConnection() {
            return conn;
        }
    }
}


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

    /**
     * Constructor for Server
     *
     * @param hostname The host name of the remote server
     * @param port Port number used to connect to the server
     */
    Server(String hostname, int port) {
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
                //Logger.err("Could not close client socket " + ioe.getMessage());
            }

            try {
                serverChannel.close();
            } catch (IOException ioe) {
                //Logger.err("Could not close server socket " + ioe.getMessage());
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
        for (Enumeration connectionEnum = tempConnections.elements();
                connectionEnum.hasMoreElements();) {
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

        //Logger.log("Server Connection closed to " + getHostname() + ": " + getPort() + ". Remaining: " + connections.size());
    }

    /**
     * Callback used by the ConnectionRelay when it has cleaned its state after
     * a connection has closed.
     *
     * @param conn
     */
    void dataTransferConnectionClosed(Connection conn) {
        connections.remove(conn);

        //Logger.logOptional("dataTransferConnectionClosed Connection closed to " + getHostname() + ": " + getPort() + ". Remaining: " + connections.size());
    }
}
