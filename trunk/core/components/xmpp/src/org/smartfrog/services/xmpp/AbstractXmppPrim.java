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

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 */
public abstract class AbstractXmppPrim extends PrimImpl implements Xmpp {


    private String server, login, password, resource, serviceName;
    private int port;
    private boolean presence, requireEncryption, useTLS;
    public static final String ERROR_NO_SECURE_CONNECTION = "Failed to set up a secure connection to ";
    private String status;
    private int subscriptionMode;

    protected AbstractXmppPrim() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        server = sfResolve(ATTR_SERVER, server, true);
        serviceName = sfResolve(ATTR_SERVICE_NAME, server, true);
        if (login == null) {
            login = sfResolve(ATTR_LOGIN, login, true);
        }
        if (password == null) {
            password = sfResolve(ATTR_PASSWORD, password, true);
        }
        port = sfResolve(ATTR_PORT, port, true);
        presence = sfResolve(ATTR_PRESENCE, presence, true);
        requireEncryption = sfResolve(ATTR_REQUIRE_ENCRYPTION, requireEncryption, true);
        resource = sfResolve(ATTR_RESOURCE, resource, true);
        status = sfResolve(ATTR_STATUS, "", true);
        subscriptionMode = sfResolve(ATTR_SUBSCRIPTION_MODE, 0, true);
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
     * @throws SmartFrogException if something went wrong
     */
    public XMPPConnection login() throws SmartFrogException {
        XMPPConnection connection = null;
        String serverInfo = server + ":" + port + " as " + login;
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
            if (requireEncryption && !connection.isSecureConnection()) {
                throw new SmartFrogException(ERROR_NO_SECURE_CONNECTION + serverInfo);
            }
            configureRoster(connection);

            //set the presence information up
            if (presence) {
                Presence presenceMessage = new Presence(Presence.Type.AVAILABLE);
                presenceMessage.setStatus(status);
                connection.sendPacket(presenceMessage);
            }
            return connection;
        } catch (XMPPException e) {
            closeConnection(connection);
            throw new SmartFrogException(
                    connectionInfo,
                    e);
        } catch (IllegalStateException e) {
            throw new SmartFrogException(
                    connectionInfo,
                    e);
        }
    }

    /**
     * Override point: configure the roster of this connection. The default implementation rejects all requests
     *
     * @param connection connection to configure
     */
    protected void configureRoster(XMPPConnection connection) {
        Roster roster = connection.getRoster();
        roster.setSubscriptionMode(getSubscriptionMode());
    }

    /**
     * Shut down a connection. Can take up to 150 mS; the thread sleeps during this time
     *
     * @param connection connection to close; can be null
     */
    protected static void closeConnection(XMPPConnection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (IllegalStateException ignored) {
                //ignored
            }
        }
    }

    /**
     * get the current subscription mode
     * @return
     */
    public int getSubscriptionMode() {
        return subscriptionMode;
    }
}
