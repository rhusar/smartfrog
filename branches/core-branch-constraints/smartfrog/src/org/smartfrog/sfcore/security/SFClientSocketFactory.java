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
import java.io.ObjectInputStream;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


/**
 * A client socket factory that adapts a SSL client socket factory so that it
 * can be used to generate RMI client sockets. This factory is created "empty"
 * by the server, and fill up appropriately when the client unmarshalls it.
 *
 */
public class SFClientSocketFactory implements RMIClientSocketFactory,
    java.io.Serializable {
    /** A SSL client socket factory to be wrapped. */
    transient private SSLSocketFactory sslsf;

    /** A security environment that handles the configuration of sockets. */
    transient private SFSecurityEnvironment secEnv;

    /**
     * Class Constructor invoked by the server. Even though the actual
     * initialization is done when the factory is unmarshalled, we have to
     * initialize it here so equality works when we receive a local reference
     * as if it were remote (We could put the server "requirements" for a
     * valid client here, but in general we cannot trust the server to dictate
     * client's security requirements).
     */
    public SFClientSocketFactory() {
        secEnv = SFSecurity.getSecurityEnvironment();
        sslsf = secEnv.getSSLSocketFactory(true);
    }

    /**
     * Class Constructor invoked in the client when not received from the
     * server.
     *
     * @param secEnv A security environment shared by all the SSL sockets.
     */
    public SFClientSocketFactory(SFSecurityEnvironment secEnv) {
        this.secEnv = secEnv;
        this.sslsf = secEnv.getSSLSocketFactory(true);
    }

    /**
     * Unmarshalls the client socket factory and performs the actual
     * initialization. Since all the fields are transient the whole object is
     * actually created here from scratch. This avoids the server affecting
     * the security behaviour of the client...
     *
     * @param in Input stream used for unmarshalling.
     *
     * @throws IOException Error while unmarshalling.
     * @throws ClassNotFoundException Cannot find this class or it is not
     *            properly signed.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        //Everything is transient, but just in case...
        in.defaultReadObject();
        secEnv = SFSecurity.getSecurityEnvironment();
        sslsf = secEnv.getSSLSocketFactory(true);
    }

    /**
     * Create a client socket connected to the specified host and port.
     *
     * @param host the host name
     * @param port the port number
     *
     * @return a socket connected to the specified host and port.
     *
     * @throws IOException if an I/O error occurs during socket creation
     */
    public Socket createSocket(String host, int port) throws IOException {
        SFSocket s = new SFSocket(host, port, secEnv, true);
        SSLSocket sslSocket = (SSLSocket) sslsf.createSocket(s, host, port, true);
        s.setSSLSocket(sslSocket);

        // Configure sslSocket in here.
        secEnv.handleSocket(s);

        return sslSocket;
    }

    /**
     * Indicates whether some other object is "equal to" this one. We need to
     * override the default implementation because RMI uses a hashtable to
     * reuse connections provided that they have "equal" client factory.
     * However, since this factory is sent over the wire by value, you will
     * always get a different one. The approach that we follow is that two
     * factories are equal if their security environments are equal. This
     * works because we assume a one to one mapping between security
     * environment and factory (can't create two factories with different
     * configuration sharing the same environment). We might decide to change
     * this in the future though ...
     *
     * @param obj The reference object with which to compare.
     *
     * @return If this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
        if (secEnv == null) {
            return super.equals(obj);
        }

        if (obj instanceof SFClientSocketFactory) {
            return secEnv.equals(((SFClientSocketFactory) obj).secEnv);
        }

        return false;
    }

    /**
     * Returns a hash code value for this object. See 'equals' to understand
     * why we need to override this method.
     *
     * @return A hash code value for this object.
     */
    public int hashCode() {
        if (secEnv == null) {
            return super.hashCode();
        }

        return secEnv.hashCode();
    }
}
