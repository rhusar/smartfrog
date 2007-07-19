package org.smartfrog.avalanche.shared.xmpp;

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
import org.smartfrog.avalanche.server.monitor.handlers.MessageHandler;
import org.smartfrog.avalanche.shared.MonitoringEvent;
import org.smartfrog.avalanche.shared.XMPPEventExtension;
import org.smartfrog.avalanche.server.monitor.handlers.DefaultHostStateChangeHandler;

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

    private String xmppServer ;
	private int xmppServerPort ;

    private String xmppListenerName ;

    private EventListener listener = new EventListener();

    private String xmppUserName ;
	private String xmppPassword ;

	private boolean useSSL = false ;
	private XMPPConnection connection ;

	private static Log log = LogFactory.getLog(XMPPAdapter.class);

	/**
	 * @param xmppServer is the hostname of the XMPP server
	 * @param useSSL if SSL should be used for messages.
	 */
	public XMPPAdapter(String xmppServer, boolean useSSL) {
        // Save the hostname
        this.xmppServer = xmppServer;

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
		this.xmppUserName = xmppUserName;
	}

    // Get and set password
    public String getXmppPassword() {
		return xmppPassword;
	}

	public void setXmppPassword(String xmppPassword) {
		this.xmppPassword = xmppPassword;
	}

    // Get and set XMPP server hostname
    public String getXmppServer() {
		return xmppServer;
	}

	public void setXmppServer(String xmppServer) {
		this.xmppServer = xmppServer;
	}

    // Get and set XMPP server port
    public int getXmppServerPort() {
		return xmppServerPort;
	}

	public void setXmppServerPort(int xmppServerPort) {
		this.xmppServerPort = xmppServerPort;
	}

    // Get and set XMPP listener name
    public String getXmppListenerName() {
		return xmppListenerName;
	}

	public void setXmppListenerName(String xmppListenerName) {
		this.xmppListenerName = xmppListenerName;
	}

    private String getCurrentServerInfo() {
        return "\"" + xmppServer + ":" + xmppServerPort + "\"";
    }

    private String getCurrentConnectionInfo() {
        return "(User: \"" + xmppUserName + "\", Connection: " + this.getCurrentServerInfo() + ")";
    }

    /**
	 * If an XMPP connection is not already for this adapter, this method creates a new
	 * connection. If a connection already exists this is ignored.
	 * For forced reconnection, call close() method before init to clear any stale connection.
     *
     * Log in to server
	 *
	 * @throws XMPPException
	 */
    public void init() throws XMPPException {
        if( null == connection ){
            XMPPConnection.DEBUG_ENABLED = true;
            if( useSSL ){
                connection = new SSLXMPPConnection(xmppServer, xmppServerPort);
                log.info("Created new XMPP encrypted connection to " + this.getCurrentServerInfo());
            }else{
				connection = new XMPPConnection(xmppServer, xmppServerPort);
                log.info("Created new XMPP unencrypted connection to " + this.getCurrentServerInfo());
            }
		}
    }

    public void login() throws XMPPException {
        // Log in to XMPP Server
        connection.login(xmppUserName, xmppPassword);
        log.info("Logged in successfully. " + this.getCurrentConnectionInfo());

        // Accept incoming roster subscription requests by default.
		connection.getRoster().setSubscriptionMode(Roster.SUBSCRIPTION_ACCEPT_ALL);
    }

    public void close() {
		if ( null != connection ){
			connection.close();
			connection = null ;
            log.info("Closed connection. " + this.getCurrentConnectionInfo());
        } else {
            log.info("Connection was already closed. " + this.getCurrentConnectionInfo());
        }
	}

	/**
	 * Sends xmpp event to server, from where listener should pick it. The message is addressed to listener
	 * If message delivery fails, its logged and discarded. The failed events are not queued for later
	 * delivery as the event may have no relevance later, also this event may lead to invocation of a number
	 * of handlers on the server.
	 * @param event
	 * @throws XMPPException
	 */
	public void sendEvent(MonitoringEvent event) throws XMPPException{
		try {
            this.close();
            this.init();
            this.login();

            // Setup and send the event as message
            Message msg = new Message(xmppListenerName + "@"+ xmppServer, Message.Type.HEADLINE);
            msg.setBody("AE");
			msg.addExtension(new XMPPEventExtension(event));
            log.error("Sending message: " + msg + ". " + this.getCurrentConnectionInfo());
            connection.sendPacket(msg);
        } catch(XMPPException e ){
			// Failed sending message. Log this message and move on.
			log.error("Failed sending event: " + event + ". " + this.getCurrentConnectionInfo());
            // close connection if error, try reconnect on next message
			this.close();
		}
	}

    public void addHandler(MessageHandler handler){
		listener.addHandler(handler);
	}

	public Roster getRoster(){
		return connection.getRoster();
	}

	public void registerListeners() throws XMPPException{
		connection.addPacketListener(listener, new EventListener.XMPPPacketFilter()) ;

		// configure handler chain for host state change events
		LivenessListener llistener = new LivenessListener(connection.getRoster()) ;
		llistener.addLivenessHandler(new DefaultHostStateChangeHandler());

		connection.getRoster().addRosterListener(llistener);
	}

    public void createUser(String newUsername, String newPassword, String newFullname) {
        try {
            // Get account management
            AccountManager am = this.getConnection().getAccountManager();
            // If account creation is possible
            if (am.supportsAccountCreation()){
                    // Set a few attributes - required by most servers
                    Map attrs = new HashMap();
                    attrs.put("email", newUsername + "@" + xmppServer);
                    attrs.put("name", newFullname);
                    attrs.put("registered", "false");
                    // Create the account with the given data
                    am.createAccount(newUsername, newPassword, attrs);
                    log.info("Account \"" + newUsername + "\" was created successfully. " + this.getCurrentConnectionInfo()) ;
            } else {
                 log.error("Account \"" + newUsername + "\" could not be created. Creation not allowed. " + this.getCurrentConnectionInfo());
            }
        } catch (XMPPException xe) {
            log.error("Account \"" + newUsername + "\" could not be created. " + this.getCurrentConnectionInfo() + "\nException: " + xe);
        }
    }

    public void deleteUser(String existingUserName, String existingUserPassword) {
        try {
            // Setting up the connection
            XMPPAdapter adapter = new XMPPAdapter(this.getXmppServer(), this.isUseSSL());
            adapter.setXmppServerPort(this.getXmppServerPort());
            // Setting user credentials
            adapter.setXmppUserName(existingUserName);
            adapter.setXmppPassword(existingUserPassword);
            // Logging onto server
            adapter.init();
            adapter.login();
            // Delete user's account
            adapter.getConnection().getAccountManager().deleteAccount();
            log.info("Successfully deleted account \"" + existingUserName + "\". " + this.getCurrentConnectionInfo());
        } catch (XMPPException xe) {
            log.error("Account \"" + existingUserName + "\" could not be deleted. " + this.getCurrentConnectionInfo() + "\nException: " + xe);
        }
    }
}

