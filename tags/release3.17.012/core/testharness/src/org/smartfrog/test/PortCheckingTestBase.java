/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test;

import org.smartfrog.sfcore.utils.TimeoutInterval;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created 13-Feb-2009 14:20:42
 */

public class PortCheckingTestBase extends DeployingTestBase {
    protected boolean checkPorts = false;
    protected boolean failOnCheckFailure = false;
    protected int connectTimeout = 5000;
    protected long shutdownTimeout = 20000;
    private int pollInterval = 1000;
    private List<PortPair> ports = new ArrayList<PortPair>();


    public PortCheckingTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture,by extracting the hostname and classes dir
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        enablePortCheck();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (checkPorts) {
            blockUntilPortsAreClosed();
        }
    }

    protected void clearPortCheck() {
        ports = new ArrayList<PortPair>();
    }

    /**
     * Add a new port to the list to check
     *
     * @param name the port name
     * @param port the port number
     */
    protected void addPortCheck(String name, int port) {
        ports.add(new PortPair(name, port));
    }

    protected void blockUntilPortsAreClosed() {
        TimeoutInterval ti = new TimeoutInterval(shutdownTimeout);
        StringBuilder portsAtFault = new StringBuilder();
        boolean portIsOpen=true;
        while (!ti.hasTimedOut() && portIsOpen) {
            portIsOpen = false;
            portsAtFault = new StringBuilder();
            for (PortPair pair : ports) {
                if (pair.isOpen()) {
                    portIsOpen = true;
                    portsAtFault.append(pair);
                    portsAtFault.append('\n');
                }
            }
            if (!ti.sleep(pollInterval)) {
                break;
            }
        }
        if(portIsOpen) {
            String message = "Ports still open after " + ti.getDelay() + " milliseconds:\n" + portsAtFault;
            getLog().warn(message);
            if (failOnCheckFailure) {
                fail(message);
            }
        }
    }

    protected boolean isPortOpen(int port) {
        return isLocalPortOpen(port, connectTimeout);
    }

    protected void enablePortCheck() {
        checkPorts = true;
    }

    public boolean isCheckPorts() {
        return checkPorts;
    }

    public void setCheckPorts(boolean checkPorts) {
        this.checkPorts = checkPorts;
    }

    protected void enableFailOnPortCheck() {
        failOnCheckFailure = true;
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

    protected class PortPair {

        private PortPair(String name, int port) {
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
        public boolean isOpen() {
            return isLocalPortOpen(port, connectTimeout);
        }

        public void assertClosed() {
            if (isOpen()) {
                fail(this + " is running when it should not be");
            }
        }

        public void assertOpen() {
            if (!isOpen()) {
                fail(this + " is not running when it should be");
            }
        }
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
        } catch (SecurityException e) {
            throw (IOException) new IOException("Failed to connect to " + address).initCause(e);
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
        } catch (IOException e) {
            return false;
        }
    }
}
