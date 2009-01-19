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
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;


/**
 * Extends a Socket so that it contains a reference to the SSL socket that is
 * wrapping it, the security context used to create that socket, and whether
 * it was created using client mode.
 *
 */
public class SFSocket extends Socket {
    /** Whether the wrapping SSL socket is using client mode. */
    private boolean useClientMode;

    /** A security environment that handles the configuration of sockets. */
    private SFSecurityEnvironment secEnv;

    /** SSL socket that is wrapping this socket */
    private SSLSocket ssls;

    /** A wrapped input stream for this socket. */
    private SFInputStream currentInputStream;

    /**
     * A list of subjects that we have successfully authenticated for our peer.
     */
    private String peerAuthenticatedSubjects;

    /**
     * Constructs SFSocket with security environment.Typically invoked by the
     * server.
     *
     * @param secEnv A security environment that configures the SSL socket.
     * @param useClientMode A flag stating whether the wrapping SSL socket is
     *        using client mode.
     */
    public SFSocket(SFSecurityEnvironment secEnv, boolean useClientMode) {
        this.secEnv = secEnv;
        this.useClientMode = useClientMode;
    }

    /**
     * Constructs SFSocket with host, port and security environment.
     * Typically invoked by the server.
     *
     * @param host The host name.
     * @param port The port number.
     * @param secEnv A security environment that configures the SSL socket.
     * @param useClientMode A flag stating whether the wrapping SSL socket is
     *        using client mode.
     *
     * @throws UnknownHostException If the IP address of the host could not
     *            be determined.
     * @throws IOException If an I/O error occurs when creating the socket.
     */
    public SFSocket(String host, int port, SFSecurityEnvironment secEnv,
        boolean useClientMode) throws UnknownHostException, IOException {
        super(host, port);
        this.secEnv = secEnv;
        this.useClientMode = useClientMode;
    }

    /**
     * Sets the SSL socket that wraps this socket.
     *
     * @param ssls The SSL socket that wraps this socket.
     *
     * @see #getSSLSocket
     */
    public void setSSLSocket(SSLSocket ssls) {
        this.ssls = ssls;
    }

    /**
     * Gets the SSL socket that wraps this socket.
     *
     * @return The SSL socket that wraps this socket.
     *
     *  @see #setSSLSocket
     */
    public SSLSocket getSSLSocket() {
        return ssls;
    }

    /**
     * Gets a flag stating whether the wrapping SSL socket is using client
     * mode.
     *
     * @return A flag stating whether the wrapping SSL socket is using client
     *         mode.
     */
    public boolean getUseClientMode() {
        return useClientMode;
    }

    /**
     * Gets a list of subjects that we have successfully authenticated for our
     * peer.
     *
     * @return A list of subjects that we have successfully authenticated for
     *         our peer.
     */
    public String getPeerAuthenticatedSubjects() {
        return peerAuthenticatedSubjects;
    }

    /**
     * Sets a list of subjects that we have successfully authenticated for our
     * peer.
     *
     * @param peerAuthenticatedSubjects A list of subjects that we have
     *        successfully authenticated forour peer.
     */
    void setPeerAuthenticatedSubjects(String peerAuthenticatedSubjects) {
        SFSecurity.checkSFCommunity();
        this.peerAuthenticatedSubjects = peerAuthenticatedSubjects;
    }

    /**
     * Gets the input stream associated with this socket. The motivation to
     * override it is that in the current sun's implementation of RMI the
     * thread that calls the first read (to unmarshal the arguments) actually
     * performs the method invocation. Therefore, by attaching this socket to
     * that thread, it will become available during the method invocation for
     * security checks.
     *
     * @return A wrapped input stream for this socket.
     *
     * @throws IOException If an I/O error ocurrs when creating the input
     *            stream.
     */
    public InputStream getInputStream() throws IOException {
        if (currentInputStream == null) {
            currentInputStream = new SFInputStream(super.getInputStream(), this);
        }

        return currentInputStream;
    }
}
