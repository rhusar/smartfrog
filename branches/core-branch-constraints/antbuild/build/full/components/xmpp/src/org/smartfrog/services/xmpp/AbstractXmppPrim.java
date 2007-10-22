/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xmpp;

import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 */
public abstract class AbstractXmppPrim extends PrimImpl implements Xmpp {


    private String server, login, password, resource, serviceName;
    private int port;
    private boolean presence, requireEncryption, useTLS;

    protected AbstractXmppPrim() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        server = sfResolve(ATTR_SERVER, server, true);
        serviceName = sfResolve(ATTR_SERVICE_NAME, server, true);
        login = sfResolve(ATTR_LOGIN, login, true);
        password = sfResolve(ATTR_PASSWORD, password, true);
        port = sfResolve(ATTR_PORT, port, true);
        presence = sfResolve(ATTR_PRESENCE, presence, true);
        requireEncryption = sfResolve(ATTR_REQUIRE_ENCRYPTION, requireEncryption, true);
        resource = sfResolve(ATTR_RESOURCE, resource, true);
        useTLS = sfResolve(ATTR_USE_TLS, useTLS, true);
    }


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public boolean isPresence() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Create a connection to the server with a login, based on our state
     *
     * @return a logged in connection
     *
     * @throws SmartFrogException if something went wrong
     */
    public XMPPConnection login() throws SmartFrogException {
        XMPPConnection connection = null;
        String serverInfo= server + ":" + port + " as " + login;
        String connectionInfo = "connecting to " + serverInfo;
        sfLog().debug(connectionInfo);
        try {
            if (useTLS) {
                connection = new SSLXMPPConnection(server, port, serviceName);
            } else {
                connection = new XMPPConnection(server, port, serviceName);
            }
            connection.login(login, password, resource, presence);
            //check the encryption status
            if(requireEncryption && !connection.isSecureConnection()) {
                throw new SmartFrogException("Failed to set up a secure connection to "+ serverInfo);
            }
            return connection;
        } catch (XMPPException e) {
            if (connection != null) {
                connection.close();
            }
            throw new SmartFrogException(
                    connectionInfo,
                    e);
        } catch (IllegalStateException e) {
            throw new SmartFrogException(
                    connectionInfo,
                    e);
        }
    }


}
