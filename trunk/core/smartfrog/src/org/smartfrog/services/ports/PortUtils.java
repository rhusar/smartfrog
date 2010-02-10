/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.ports;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.InetAddress;

/**
 * Created 28-May-2008 15:22:20
 */

public class PortUtils {

    private PortUtils() {
    }

    /**
     * Wait for a hostname to resolve
     * @param hostname hostname to look for
     * @param resolveTimeout timeout
     * @param sleep time in millis to sleep
     * @return the address
     * @throws UnknownHostException if the host did not resolve
     * @throws InterruptedException if the wait was interrupted
     */
    public static InetAddress waitForHostnameToResolve(String hostname, int resolveTimeout, int sleep)
            throws UnknownHostException, InterruptedException {
        long timeout = System.currentTimeMillis() + (resolveTimeout * 1000);
        UnknownHostException error;
        do {
            try {
                InetAddress addr = InetAddress.getByName(hostname);
                return addr;
            } catch (UnknownHostException e) {
                error = e;
                Thread.sleep(sleep);
            }
        } while (System.currentTimeMillis() < timeout);
        throw error;
    }
    
    /**
     * probe a port for being open
     *
     * @param hostname hostname to resolve/check
     * @param port port to check
     * @param connectTimeout timeout
     * @throws IOException failure to connect, including timeout
     */
    public static void checkPort(String hostname, int port, int connectTimeout) throws IOException {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        checkPort(address, connectTimeout);
    }
    

    /**
     * probe a port for being open
     *
     * @param address        address to check
     * @param connectTimeout timeout
     * @throws IOException failure to connect, including timeout
     */
    public static void checkPort(InetSocketAddress address, int connectTimeout) throws IOException {
        Socket socket = null;
        socket = new Socket();
        try {
            socket.connect(address, connectTimeout);
        } catch (SecurityException e) {
            throw (IOException) new IOException("Failed to connect to " + address).initCause(e);
        } catch (SocketTimeoutException ste) {
            throw (SocketTimeoutException)new SocketTimeoutException("Timeout connecting to "+ address).initCause(ste);
        } catch (UnknownHostException unknownHost) {
            
        }
        finally {
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
     * @param port port number
     * @param connectTimeout timeout for connections
     * @return true iff the port is open
     */
    public static boolean isLocalPortOpen(int port, int connectTimeout) {
        InetSocketAddress localPort = new InetSocketAddress("localhost", port);
        try {
            checkPort(localPort,connectTimeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
