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
import java.net.InetAddress;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


/**
 * Extends ServerSocket so that it contains the security context, and
 * configures properly the socket returned in the accept(), getting hold of
 * the session...
 *
 */
public class SFServerSocket extends ServerSocket {
    /** A security environment that handles the configuration of sockets. */
    private SFSecurityEnvironment secEnv;

    /** A debugging utility to print messages. */
    private SFDebug debug;

    /**
     * Class Constructor.
     * <P>
     * If the bind address is <code>null</code>, then the system will pick up
     * an ephemeral port and a valid local address to bind the socket.
     * <P>     
     * @param bindAddr bind address
     * @param port The port number
     * @param secEnv A security environment that handles the configuration of
     *        sockets.
     *
     * @throws IOException  In case error while creating server socket
     */
    public SFServerSocket(int port, InetAddress bindAddr, SFSecurityEnvironment secEnv)
        throws IOException {
        super(port,0,bindAddr);
        this.secEnv = secEnv;
        debug = SFDebug.getInstance("SFServerSocket");
    }

    /**
     * Listens for a connection to be made to this socket and accepts it. The
     * method blocks until a connection is made.
     *
     * <p>
     * A new Socket <code>s</code> is created and, if there is a security
     * manager, the security manager's <code>checkAccept</code> method is
     * called with <code>s.getInetAddress().getHostAddress()</code> and
     * <code>s.getPort()</code> as its arguments to ensure the operation is
     * allowed. This could result in a SecurityException. We attach our custom
     * socket to the SocketImpl returned by the accept and wrap around it the
     * ssl socket.
     * </p>
     *
     * @return the new Socket
     *
     * @throws IOException if an I/O error occurs when waiting for a
     *            connection.
     *
     * @see SecurityManager#checkAccept
     */
    public Socket accept() throws IOException {
        try {
            SFSocket s = new SFSocket(secEnv, false);
            super.implAccept(s);

            SSLSocketFactory sslsf = secEnv.getSSLSocketFactory(false);
            SSLSocket sslSocket = (SSLSocket) sslsf.createSocket(s,
                    s.getInetAddress().getHostName(), s.getPort(), true);
            s.setSSLSocket(sslSocket);

            // Configure sslSocket in here.
            secEnv.handleSocket(s);

            return sslSocket;
        } catch (IOException e) {
            // At least we can see what happens...
            if (debug != null) {
                debug.println("accept got IOException " + e.getMessage());
            }

            throw e;
        }
    }
}
