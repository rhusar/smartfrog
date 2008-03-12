package org.smartfrog.avalanche.server.monitor.xmpp;

/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.smartfrog.avalanche.shared.handlers.XMPPPacketHandler;
import org.smartfrog.services.xmpp.MonitoringEvent;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.avalanche.shared.handlers.DefaultHostStateChangeHandler;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * XMPPAdapter is used by client components for sending monitoring events
 * over XMPP. In case of smartfrog there is a smartfrog wrapper for this component
 * on all machines, which will be used by other smartfrog components.
 * All messages sent by this clients are addressed to User Id for listener in Avalanche
 * Server.
 * @author sanjaydahiya
 *
 */
public class XMPPAdapter {

	public static final int default_xmpp_port = 5222;
	public static final int default_xmpp_port_ssl = 5223;

    private String xmppServer = "localhost";
	private int xmppServerPort = default_xmpp_port_ssl;

    private EventListener listener = new EventListener();

    private String xmppUserName = "user";
	private String xmppPassword = "password";

	private boolean useSSL = true ;
	private XMPPConnection connection ;

	private static final Log log = LogFactory.getLog(XMPPAdapter.class);

	/**
     * Create a new instance of XMPPAdapter
	 * @param xmppServer is the hostname of the XMPP server
	 * @param useSSL if SSL should be used for messages.
	 */
	public XMPPAdapter(String xmppServer, boolean useSSL) {
        // Save the hostname
        this.xmppServer = xmppServer;

        // setup the event listener
        listener.setup();

        // Save useSSL value and set the ports accordingly
        this.useSSL = useSSL ;
        if (useSSL) {
			xmppServerPort = default_xmpp_port_ssl ;
		} else {
			xmppServerPort  = default_xmpp_port ;
		}
	}

    // Get connection
    public XMPPConnection getConnection() {
		return connection;
	}

    // Get and set status of SSL usage
    public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

    public boolean isUseSSL() {
		return useSSL;
	}

    // Get and set username
	public String getXmppUserName() {
		return xmppUserName;
	}

	public void setXmppUserName(String xmppUserName) {
		if (xmppUserName != null)
            this.xmppUserName = xmppUserName;
	}

    // Get and set password
    public String getXmppPassword() {
		return xmppPassword;
	}

	public void setXmppPassword(String xmppPassword) {
        if (xmppPassword != null)
            this.xmppPassword = xmppPassword;
	}

    // Get and set XMPP server hostname
    public String getXmppServer() {
		return xmppServer;
	}

	public void setXmppServer(String xmppServer) {
        if (xmppServer != null)
            this.xmppServer = xmppServer;
	}

    // Get and set XMPP server port
    public int getXmppServerPort() {
		return xmppServerPort;
	}

	public void setXmppServerPort(int xmppServerPort) {
        this.xmppServerPort = xmppServerPort;
	}

    private String getCurrentServerInfo() {
        return "\"" + xmppServer + ":" + xmppServerPort + "\"";
    }

    private String getCurrentConnectionInfo() {
        return "(User: \"" + xmppUserName + "\", " +
                "Password: \"" + xmppPassword + "\", " +
                "Connection: " + this.getCurrentServerInfo() + ")";
    }

    /**
	 * If an XMPP connection is not already for this adapter, this method creates a new
	 * connection. If a connection already exists this is ignored.
	 * For forced reconnection, call close() method before init to clear any stale connection.
	 *
	 * @throws XMPPException is thrown if anything went wrong.
	 */
    public void init() throws XMPPException {
        if( null == connection ){
//            XMPPConnection.DEBUG_ENABLED = true;
            if( useSSL ){
                connection = new SSLXMPPConnection(xmppServer, xmppServerPort);
                log.info("Created new XMPP encrypted connection to " + this.getCurrentServerInfo());
            }else{
				connection = new XMPPConnection(xmppServer, xmppServerPort);
                log.info("Created new XMPP unencrypted connection to " + this.getCurrentServerInfo());
            }
		}
    }

    /**
     * Logs in with a set username and password.
     * @throws XMPPException is thrown if the login failed.
     */
    public void login() throws XMPPException {
        // Log in to XMPP Server
        try {
            log.info("Trying to log in. " + getCurrentConnectionInfo());
            connection.login(xmppUserName, xmppPassword);
        } catch (XMPPException e) {
            log.error("Login failed. " + getCurrentConnectionInfo());
            throw e;
        }

        log.info("Logged in successfully. " + getCurrentConnectionInfo());

        // Accept incoming roster subscription requests by default.
		connection.getRoster().setSubscriptionMode(Roster.SUBSCRIPTION_ACCEPT_ALL);
    }

    /**
     * Simply closes the existing connection.
     */
    public void close() {
		if ( null != connection ){
			connection.close();
			connection = null ;
            log.info("Closed connection. " + getCurrentConnectionInfo());
        } else {
            log.info("Connection was already closed. " + getCurrentConnectionInfo());
        }
	}

	/**
	 * Sends xmpp event to server, from where listener should pick it. The message is addressed to listener
	 * If message delivery fails, its logged and discarded. The failed events are not queued for later
	 * delivery as the event may have no relevance later, also this event may lead to invocation of a number
	 * of handlers on the server.
	 * @param event is the event that is encapsulated and send to the Avalanche listening user.
	 * @throws XMPPException is thrown if anything went wrong sending the message
	 */
	public void sendEvent(String recipient, MonitoringEvent event) throws XMPPException{
		try {
            if (getConnection() == null) {
                close();
                init();
                login();
            }
            // Setup and send the event as message
            Message msg = new Message(recipient, Message.Type.HEADLINE);
            msg.setBody("AE");
			msg.addExtension(new XMPPEventExtension(event));
            log.info("Sending message: " + msg + ". " + getCurrentConnectionInfo());
            connection.sendPacket(msg);
        } catch(XMPPException e ){
			// Failed sending message. Log this message and move on.
			log.error("Failed sending event: " + event + ". " + getCurrentConnectionInfo());
            // close connection if error, try reconnect on next message
			close();
		}
	}

    /**
     * Add a XMPPPacketHandler to the list of handlers.
     * Handlers are not registered until registerListeners() is called.
     * @param handler a XMPPPacketHandler
     */
    public void addHandler(XMPPPacketHandler handler){
		listener.addHandler(handler);
	}

    /**
     * Retrieves the Roster (Buddy list) of the currently connected user
     * @return the roster object of the current user
     */
    public Roster getRoster(){
		return connection.getRoster();
	}

    /**
     * Registers a packet filter, a roster listener, and any custom handlers (added by addHandler())
     * to the connection managed by this instance of XMPPAdapter
     * @throws XMPPException throws a XMPPException if registering the handlers failed.
     */
    public void registerListeners() throws XMPPException {
        log.info("Adding PacketListener and PacketFilter to connection." + this.getCurrentConnectionInfo());
        getConnection().addPacketListener(listener, new EventListener.XMPPPacketFilter()) ;

        log.info("Adding RosterListener.");
        // configure handler chain for host state change events
		LivenessListener llistener = new LivenessListener(getRoster());
		llistener.addLivenessHandler(new DefaultHostStateChangeHandler());
		getRoster().addRosterListener(llistener);
	}

    /**
     * Creates a new XMPP user within the context of the current connection.
     * Uses current server and user account to create the new account.
     * @param newUsername is the username of the new user
     * @param newPassword the new user's password
     * @param newFullname the fullname of the new user
     * @return returns true if everything went ok
     */
    public boolean createUser(String newUsername, String newPassword, String newFullname) {
        boolean returnValue = false;
        try {
            // Get account management
            AccountManager am = getConnection().getAccountManager();
            // If account creation is possible
            if (am.supportsAccountCreation()){
                    // Set a few attributes - required by most servers
                    Map attrs = new HashMap();
                    attrs.put("email", newUsername + "@" + getXmppServer());
                    attrs.put("name", newFullname);
                    attrs.put("registered", "false");
                    // Create the account with the given data
                    am.createAccount(newUsername, newPassword, attrs);
                    log.info("Account \"" + newUsername + "\" was created successfully. " + getCurrentConnectionInfo()) ;
                    returnValue = true;
            } else {
                 log.error("Account \"" + newUsername + "\" could not be created. Creation not allowed. " + getCurrentConnectionInfo());
            }
        } catch (XMPPException xe) {
            log.error("Account \"" + newUsername + "\" could not be created. " + getCurrentConnectionInfo() + "\nException: " + xe);
        }
        return returnValue;
    }

    /**
     * Deletes a XMPP user by logging on as a given existing user.
     * @param existingUserName username of the to-be-deleted user
     * @param existingUserPassword the password of the user
     * @return returns true if everything went ok.
     */
    public boolean deleteUser(String existingUserName, String existingUserPassword) {
        try {
            // Setting up the connection
            XMPPAdapter adapter = new XMPPAdapter(getXmppServer(), isUseSSL());
            adapter.setXmppServerPort(getXmppServerPort());
            // Setting user credentials
            adapter.setXmppUserName(existingUserName);
            adapter.setXmppPassword(existingUserPassword);
            // Logging onto server
            adapter.init();
            adapter.login();
            // Delete user's account
            adapter.getConnection().getAccountManager().deleteAccount();
            log.info("Successfully deleted account \"" + existingUserName + "\". " + getCurrentConnectionInfo());
            return true;
        } catch (XMPPException xe) {
            log.error("Account \"" + existingUserName + "\" could not be deleted. " + getCurrentConnectionInfo(),xe);
            return false;
        }
    }

    /**
    * Removes a given user from the roster of the current user
    * The context is the current connection (XMPP server, logged-in user)
    *
    * @param existingUsername is the username to be removed from the roster
    * @return returns true if everything went ok
    */
    public boolean removeUserFromRoster(String existingUsername) {
        try {
            Roster roster = getRoster();
            // Remove roster entry
            RosterEntry re = roster.getEntry(existingUsername);
            roster.removeEntry(re);
            log.info("Successfully removed roster for host \"" + existingUsername + "\".");
            return true;
        } catch (XMPPException xe) {
            log.error("Error while removing roster for user \"" + existingUsername + "\"",xe);
            return false;
        }
    }

    /**
     * Adds a given username to the roster (buddy list) of the currently
     * logged-in user on the current connection.
     * @param existingUsername is the username to be added to the roster
     * @return returns true if everything went ok
     */
    public boolean addUserToRoster(String existingUsername) {
        // Try to add new user to the Avalanche user's buddy list
        try {
            // Add roster entry
            getRoster().createEntry(existingUsername + "@" + getXmppServer(), existingUsername, null);
            log.info("Successfully added roster for host \"" + existingUsername + "\".");
            return true;
        } catch (XMPPException xe) {
            log.error("Could not add roster for host \"" + existingUsername + "\"",xe);
            return false;
        }
    }
}

