/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.monitor.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.shared.xmpp.XMPPAdapter;
import java.net.UnknownHostException;
import java.net.InetAddress;

/**
 * Whenever a new Host is added to Avalanche server, this handler is called 
 * it creates a user in XMPP server for the new Host. Similarly it is also 
 * called if a host is deleted from XMPP server and it deletes the user for 
 * deleted host from XMPP server. 
 * @see org.smartfrog.avalanche.server.HostManager
 * @author sanjaydahiya
 *
 */
public class HostUpdateRosterHandler implements HostUpdateHandler {
	private XMPPAdapter adminAdapter ; 
	private XMPPAdapter listenerAdapter ; 
	private static Log log = LogFactory.getLog(HostUpdateRosterHandler.class);
	private Roster roster;

    public HostUpdateRosterHandler(XMPPAdapter adminAdapter, XMPPAdapter listenerAdapter){
		// Save XMPP adapters
        this.adminAdapter = adminAdapter;
		this.listenerAdapter = listenerAdapter;
        // Save roster
        roster = this.listenerAdapter.getRoster();
	}

    /**
     *
     * @param strHostOrIp is the IP or host name of a system
     * @return the host name of IP or host given
     * @throws UnknownHostException
     */
    private String resolve(String strHostOrIp) {
        try {
            InetAddress inetAdd = InetAddress.getByName(strHostOrIp);
            return inetAdd.getHostName().toLowerCase();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
	 * Creates a user for the host, in case of failure it logs the error.
	 */
	public void hostAdded(HostType h) {
		String hostAddress = resolve(h.getId());

        // if client is a valid machine on the network
        if (hostAddress != null) {
            // Create user on the XMPP server
            adminAdapter.createUser(hostAddress, hostAddress, hostAddress);

            // Try to add new user to the Avalanche user's buddy list
            try {
                // Add roster entry
                roster.createEntry(hostAddress + "@" + adminAdapter.getXmppServer(), hostAddress, null);
                log.info("Successfully added roster for host \"" + hostAddress + "\".");
            } catch (XMPPException xe) {
                log.error("Could not add roster for host \"" + hostAddress + "\". Exception: " + xe);
            }
        }
    }

	/**
	 * Deletes the username used by the host, in case of failure it logs the error.
	 */
	public void hostDeleted(HostType h) {
        String hostAddress = resolve(h.getId());

        // if client is a valid machine on the network
        if (hostAddress != null) {
            // Delete user
            adminAdapter.deleteUser(hostAddress, hostAddress);

            try {
                // Remove roster entry
                RosterEntry re = roster.getEntry(hostAddress);
                roster.removeEntry(re);
                log.info("Successfully removed roster for host \"" + hostAddress + "\".");
            } catch (Exception e) {
                log.error("Error while removing roster for user \"" + hostAddress + "\". Exception:" + e);
            }
        }
    }
}
