/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.monitor.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;

import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.server.monitor.xmpp.XMPPAdapter;

/**
 * Whenever a new Host is added to Avalanche server, this handler is called 
 * it creates a user in XMPP server for the new Host. Similarly it is also 
 * called if a host is deleted from XMPP server and it deletes the user for 
 * deleted host from XMPP server. 
 * @see HostManager
 * @author sanjaydahiya
 *
 */
public class HostUpdateRosterHandler implements HostUpdateHandler {
	private XMPPAdapter adminAdapter ; 
	private XMPPAdapter listenerAdapter ; 
	private static Log log = LogFactory.getLog(HostUpdateRosterHandler.class);
	private Roster roster ; 
	public HostUpdateRosterHandler(XMPPAdapter adminAdapter, XMPPAdapter listenerAdapter){
		this.adminAdapter = adminAdapter;
		this.listenerAdapter = listenerAdapter ; 
		roster = this.listenerAdapter.getRoster();
	}
	/**
	 * Creates a user for the host, in case of failure it logs the error as fatal. 
	 */
	public void hostAdded(HostType h) {
		// create a user for this host if it doesnt exist 
		String hostAddress = null ; 
		try{
			AccountManager am = adminAdapter.getConnection().getAccountManager();
			
			
			java.net.InetAddress inetAdd = java.net.InetAddress.getByName(h.getId());
			hostAddress = inetAdd.getHostName() ; 
			
			log.debug("Using IP address for " + h.getId() + " -> " + hostAddress) ;
			// TODO: create user with a secure password. 
			if(am.supportsAccountCreation() ){
				Map attrs = new HashMap();
				attrs.put("email", hostAddress+ "@avalanche");
				attrs.put("name", h.getId());
				attrs.put("registered", "false");
				am.createAccount(hostAddress, hostAddress , attrs);
				log.info("Create XMPP Account for Host : " +hostAddress) ;
			}
		}catch(Exception e){
			log.error("Error !! "+ e) ;
		}
		
	/*  // Now registering for Roster in ActiveProfileUpdateHandler after we get a notification from the
	 *  // client that XMPP client in smartfrog is started. 
	 * 	// register roster
		// In a different try block as if account already exist last operation may fail
		// no harm in trying to register for roster. 
		try{
			roster.createEntry(hostAddress+"@"+adminAdapter.getXmppServer() , hostAddress, null ) ;
			log.info("Created Roster Entry for : " + hostAddress);
		}catch(Exception e){
			log.fatal("Roster Add failed for host :" +hostAddress);
		}
		*/
	}

	/**
	 * Creates a user for the host, in case of failure it logs the error as fatal. 
	 */
	public void hostDeleted(String hostId) {
		
		// login as host and delete the account in XMPP server. 
		XMPPAdapter hostAdapter = new XMPPAdapter();
		hostAdapter.setXmppServer(adminAdapter.getXmppServer());
		hostAdapter.setXmppServerPort(adminAdapter.getXmppServerPort());
		hostAdapter.setUseSSL(adminAdapter.isUseSSL());
		
		try{
			hostAdapter.init();
			java.net.InetAddress inetAdd = java.net.InetAddress.getByName(hostId);
			String hostAddress = inetAdd.getHostName() ; 
			
			
			hostAdapter.getConnection().login(hostAddress, hostAddress);
			
			AccountManager am = hostAdapter.getConnection().getAccountManager();
			am.deleteAccount();
			RosterEntry re = roster.getEntry(hostId) ;
			roster.removeEntry(re);
			
		}catch(Exception e){
			log.error("Account deletion was not complete : " + e);
		}
	}
}
