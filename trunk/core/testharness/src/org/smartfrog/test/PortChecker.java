/**
 *
 */

package org.smartfrog.test;

import org.smartfrog.sfcore.utils.TimeoutInterval;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class PortChecker {
    
    private int connectTimeout = 5000;
    private long shutdownTimeout = 20000;
    private int pollInterval = 1000;

    private Collection<PortPair> ports = new ArrayList<PortPair>();

    public PortChecker() {
    }

    public void clearPortCheck() {
        ports = new ArrayList<PortChecker.PortPair>();
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getShutdownTimeout() {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(long shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    public int getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(final int pollInterval) {
        this.pollInterval = pollInterval;
    }

    /**
     * Add a new port to the list to check
     *
     * @param name the port name
     * @param port the port number
     */
    public void addPortCheck(String name, int port) {
        addPortCheck(createPortPair(name, port));
    }
    /**
     * Check for a specific port pair
     * @param portPair portpair
     */
    public void addPortCheck(PortChecker.PortPair portPair) {
        ports.add(portPair);
    }

    /**
     * Create a portpair
     * @param name the port name
     * @param port the port number
     * @return the new port pair
     */
    public PortChecker.PortPair createPortPair(final String name, final int port) {
        return new PortChecker.PortPair(name, port);
    }

    /**
     * Block until the ports are closed or the shutdown timeout is met
     * @return an empty string or a list of ports at fault
     */
    protected String  blockUntilPortsAreClosed() {
        TimeoutInterval ti = new TimeoutInterval(shutdownTimeout);
        StringBuilder portsAtFault = new StringBuilder();
        boolean portIsOpen = true;
        while (!ti.hasTimedOut() && portIsOpen) {
            portIsOpen = false;
            portsAtFault = new StringBuilder();
            for (PortChecker.PortPair pair : ports) {
                if (pair.isOpen(connectTimeout)) {
                    portIsOpen = true;
                    portsAtFault.append(pair);
                    portsAtFault.append('\n');
                }
            }
            if (!ti.sleep(pollInterval)) {
                break;
            }
        }
            return portsAtFault.toString();
        }
        
    /**
     * Here is where the port gets probed
     *
     * @param address        address to check
     * @param connectTimeout timeout
     * @throws IOException failure to connect, including timeout
     */
    public static void checkPort(InetSocketAddress address, int connectTimeout) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(address, connectTimeout);
        } catch (Exception e) {
            throw (IOException) new IOException("Failed to connect to " + address
                    + " after " + connectTimeout + " millisconds"
                    + ": " + e).initCause(e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Test for a local port being open
     *
     * @param port           port number
     * @param connectTimeout timeout for connections
     * @return true iff the port is open
     */
    public static boolean isLocalPortOpen(int port, int connectTimeout) {
        InetSocketAddress localPort = new InetSocketAddress("localhost", port);
        try {
            checkPort(localPort, connectTimeout);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Wait for a port to open.
     * @param address internet address
     * @param totalTimeoutMillis total time to spin
     * @param connectTimeoutMillis connect time
     * @param sleepMillis sleep time
     * @throws InterruptedException if the sleep was interrupted
     * @throws IOException connection failures
     */
    public static void waitForPortOpen(InetSocketAddress address,
                                       int totalTimeoutMillis,
                                       int connectTimeoutMillis,
                                       int sleepMillis) throws InterruptedException, IOException {
        long endtime = System.currentTimeMillis() + totalTimeoutMillis;
        IOException caught = null;
        boolean connected = false;
        while (!connected && endtime > System.currentTimeMillis()) {
            try {
                checkPort(address, connectTimeoutMillis);
                connected = true;
            } catch (IOException e) {
                caught = e;
                Thread.sleep(sleepMillis);
            }
        }
        if (!connected) {
            throw caught;
        }
    }

    /**
     * A name/port pair
     */
    public static class PortPair {

        protected PortPair(String name, int port) {
            this.port = port;
            this.name = name;
        }

        public final int port;
        public final String name;

        @Override
        public String toString() {
            return name + " on port " + port;
        }

        /**
         * check for being open
         *
         * @return true if a connection can be made
         */
        public boolean isOpen(int connectTimeout) {
            return PortCheckingTestBase.isLocalPortOpen(port, connectTimeout);
        }


    }
}
