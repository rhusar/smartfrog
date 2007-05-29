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
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;

import javax.net.ssl.SSLSocketFactory;


/**
 * This interface encapsulates JSSE requirements for configuring a secure
 * connection that can be used by RMI.
 *
 */
public interface SFSecurityEnvironment {
    /**
     * Returns a socket factory that can be used by RMI to establish a JSSE
     * secure channnel.
     *
     * @param useClientMode If the factory generates SSL sockets that initiate
     *        the handshake.
     *
     * @return A socket factory that can be used by RMI to establish a JSSE
     *         secure channnel.
     */
    public SSLSocketFactory getSSLSocketFactory(boolean useClientMode);

    /**
     * Gets a  RMI wrapper factory to the client's SSL factory.
     *
     * @return A  RMI wrapper factory to the client's SSL factory.
     */
    public RMIClientSocketFactory getRMIClientSocketFactory();

    /**
     * Gets a  RMI wrapper factory to the client's SSL factory (without
     * initialized).
     *
     * @return A  RMI wrapper factory to the client's SSL factory (without
     *         initialized).
     */
    public RMIClientSocketFactory getEmptyRMIClientSocketFactory();

    /**
     * Gets a  RMI wrapper factory to the server's SSL factory.
     *
     * @return A  RMI wrapper factory to the server's SSL factory.
     */
    public RMIServerSocketFactory getRMIServerSocketFactory();

    /**
     * Gets an RMISocketFactory that is "safe" enough for the default case.
     * This will be installed as part of the security initialization. However,
     * this default factory will be overwritten by the one specified in the
     * object reference, so we should not expect that this "minimum security"
     * will always be enforced.
     *
     * @return An RMISocketFactory that is "safe" enough for the default case,
     *         i.e., when the object reference does not specify otherwise.
     */
    public RMISocketFactory getRMISocketFactory();

    /**
     * Handles a SFSocket with a SSL socket attached to it. This process
     * includes configuring it depending on use mode and possibly waiting
     * until the secure session gets established, and take an action according
     * to the resulting peer characteristics.
     *
     * @param s A SFSocket with a SSL socket attached.
     *
     * @throws IOException Failure while configuring or establishing secure
     *            session.
     */
    public void handleSocket(SFSocket s) throws IOException;
}
