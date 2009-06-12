/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.jetty.listeners;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.passwords.PasswordProvider;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * Extension of the socket class to provide TLS/SSL security
 * Created 08-Oct-2007 15:21:43
 *
 */

public class SSLJettySocketConnectorImpl extends JettySocketConnectorImpl implements SSLJettySocketConnector {


    public SSLJettySocketConnectorImpl() throws RemoteException {
    }


    /**
     * Override the base class and create a socket with SSL/TLS all set up; including keystore and
     * password.
     * @return an {@link SslSocketConnector} instance
     */
    @Override
    protected Connector createConnector() throws SmartFrogException, RemoteException {
        SslSocketConnector ssl = new SslSocketConnector();
        String keystore = FileSystem.lookupAbsolutePath(this, ATTR_KEYSTORE, null, null, true, null);
        String keystoreType = sfResolve(ATTR_KEYSTORETYPE, "", true);
        PasswordProvider provider=(PasswordProvider) sfResolve(ATTR_PASSWORD,(Prim)null,true);
        String password = provider.getPassword();
        ssl.setKeystore(keystore);
        ssl.setKeystoreType(keystoreType);
        ssl.setKeyPassword(password);
        ssl.setTruststore(keystore);
        ssl.setTruststoreType(keystoreType);
        ssl.setTrustPassword(password);
        ssl.setProtocol(sfResolve(ATTR_PROTOCOL,"",true));
        return ssl;
    }
}
