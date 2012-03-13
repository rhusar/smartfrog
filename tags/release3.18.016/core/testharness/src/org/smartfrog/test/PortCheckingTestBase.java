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

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created 13-Feb-2009 14:20:42
 */

public abstract class PortCheckingTestBase extends DeployingTestBase {
    private PortChecker portChecker = new PortChecker();
    protected boolean checkPorts = false;
    protected boolean failOnCheckFailure = false;

    public PortCheckingTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture,by extracting the hostname and classes dir
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        enablePortCheck();
    }

    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (checkPorts) {
            blockUntilPortsAreClosed();
        }
    }

    protected void clearPortCheck() {
        portChecker.clearPortCheck();
    }

    /**
     * Add a new port to the list to check
     *
     * @param name the port name
     * @param port the port number
     */
    protected void addPortCheck(String name, int port) {
        portChecker.addPortCheck(name, port);
    }

    /**
     * Create a portpair
     * @param name the port name
     * @param port the port number
     * @return the new port pair
     */
    protected PortChecker.PortPair createPortPair(final String name, final int port) {
        return new PortChecker.PortPair(name, port);
    }

    /**
     * Check for a specific port pair
     * @param portPair portpair
     */
    protected void addPortCheck(PortChecker.PortPair portPair) {
        portChecker.addPortCheck(portPair);
    }

    /**
     * Block until the ports are closed or the shutdown timeout is met
     */
    protected void blockUntilPortsAreClosed() {

        String portsAtFault = portChecker.blockUntilPortsAreClosed();
        if (!portsAtFault.isEmpty()) {
            String message = "Ports still open after " + portChecker.getShutdownTimeout() 
                    + " milliseconds:\n" 
                    + portsAtFault;
            getLog().warn(message);
            if (failOnCheckFailure) {
                fail(message);
            }
        }
    }

    protected boolean isPortOpen(int port) {
        return PortChecker.isLocalPortOpen(port, portChecker.getConnectTimeout());
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
        return portChecker.getConnectTimeout();
    }

    public void setConnectTimeout(int connectTimeout) {
        portChecker.setConnectTimeout(connectTimeout);
    }

    public long getShutdownTimeout() {
        return portChecker.getShutdownTimeout();
    }

    public void setShutdownTimeout(long shutdownTimeout) {
        portChecker.setShutdownTimeout(shutdownTimeout);
    }

    /**
     * Here is where the port gets probed
     *
     * @param address        address to check
     * @param connectTimeout timeout
     * @throws IOException failure to connect, including timeout
     */
    public static void checkPort(InetSocketAddress address, int connectTimeout) throws IOException {
        PortChecker.checkPort(address, connectTimeout);
    }

    /**
     * Test for a local port being open
     *
     * @param port           port number
     * @param connectTimeout timeout for connections
     * @return true iff the port is open
     */
    public static boolean isLocalPortOpen(int port, int connectTimeout) {
        return PortChecker.isLocalPortOpen(port, connectTimeout);
    }

    /**
     * Wait for a port to open.
     * @param address internet address
     * @param totalTimeoutMillis total time to spin
     * @param connectTimeoutMillis connect time
     * @param sleepMillis sleep time
     * @throws IOException connection failures
     */
    public static void waitForPortOpen(InetSocketAddress address,
                                   int totalTimeoutMillis,
                                   int connectTimeoutMillis,
                                   int sleepMillis) throws IOException {
        PortChecker.waitForPortOpen(address, totalTimeoutMillis, connectTimeoutMillis, sleepMillis);
    }
}
