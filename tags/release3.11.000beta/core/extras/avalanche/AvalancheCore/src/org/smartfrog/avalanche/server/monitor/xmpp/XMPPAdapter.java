/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.monitor.xmpp;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import org.smartfrog.avalanche.server.monitor.handlers.DefaultHostStateChangeHandler;
import org.smartfrog.avalanche.server.monitor.handlers.MessageHandler;


/**
 * Server side XMPP adapter, it registers a listener 
 * for messages to Avalanche monitoring server. It also subscribes for 
 * notifications for presence information. 
 * @author sanjaydahiya
 *
 */
public class XMPPAdapter {

	private String xmppServer = "localhost" ;
	private int xmppServerPort = 5223 ;
//	private Map handlers ; 
	boolean useSSL = true ; 
	XMPPConnection connection ;
	private static Log log = LogFactory.getLog(XMPPAdapter.class);
	private EventListener listener = new EventListener(); 
	
	/**
	 * If an XMPP connection is not already for this adapter, this method creates a new 
	 * connection. If a connection already exists this is ignored. 
	 * For forced reconnection, call close() method before init to clear any stale connection. 
	 * 
	 * @throws XMPPException
	 */
	public void init() throws XMPPException{
		if( null == connection ){
			if( useSSL ){
				connection = new SSLXMPPConnection(xmppServer, xmppServerPort);
			}else{
				xmppServerPort = 5222 ;
				connection = new XMPPConnection(xmppServer, xmppServerPort) ;
			}
		}
	}
	
	public XMPPConnection getConnection() {
		return connection;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	public String getXmppServer() {
		return xmppServer;
	}

	public void setXmppServer(String xmppServer) {
		this.xmppServer = xmppServer;
	}

	public int getXmppServerPort() {
		return xmppServerPort;
	}

	public void setXmppServerPort(int xmppServerPort) {
		this.xmppServerPort = xmppServerPort;
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
	
	/**
	 * Closes XMPP connection. 
	 *
	 */
	public void close(){
		connection.close();
		connection = null ;
	}

}
