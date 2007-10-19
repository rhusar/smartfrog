/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.monitor.handlers;

import org.jivesoftware.smack.packet.Presence;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.shared.xmpp.XMPPAdapter;
import org.smartfrog.avalanche.shared.ActiveProfileUpdater;
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
	private XMPPAdapter adminAdapter;
	private XMPPAdapter listenerAdapter;

    public HostUpdateRosterHandler(XMPPAdapter adminAdapter, XMPPAdapter listenerAdapter){
		// Save XMPP adapters
        this.adminAdapter = adminAdapter;
		this.listenerAdapter = listenerAdapter;
	}

    /**
     * Returns the complete hostname of a given host
     * Uses this machine's DNS to look up the hostname
     * @param strHostOrIp is the IP or host name of a system
     * @return the host name of IP or host given
     * @deprecated
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
     * Updates the ActiveProfile of a given host
     * @param hostName hostname
     */
    private void updateAvailability(String hostName) {
        ActiveProfileUpdater updater = new ActiveProfileUpdater();
        boolean hostAvailable = false;
        Presence p = null;
        p = listenerAdapter.getRoster().getPresence(hostName + "@" + listenerAdapter.getXmppServer());
        hostAvailable = ((p != null) && (p.getType().equals(Presence.Type.AVAILABLE)));
        updater.setMachineAvailability(hostName, hostAvailable);
    }

    /**
     * Creates a XMPP user for the new host.
     * @param h HostType object of the new host
     */
	public void hostAdded(HostType h) {
		String hostAddress = h.getId().toLowerCase();

        // if client is a valid machine on the network
        if (hostAddress != null) {
            // Create user on the XMPP server
            adminAdapter.createUser(hostAddress, hostAddress, hostAddress);
            // Add the new user to the webuser's buddy list
            listenerAdapter.addUserToRoster(hostAddress);

            // Create an ActiveProfile
            ActiveProfileUpdater updater = new ActiveProfileUpdater();
            updater.createActiveProfile(hostAddress);

            // Update the Profile according to the user's presence
            this.updateAvailability(hostAddress);
        }
    }

    /**
     * Deletes the XMPP username used by the host
     * @param h HostType object of the old host
     */
	public void hostDeleted(HostType h) {
        String hostAddress = h.getId().toLowerCase();

        // if client is a valid machine on the network
        if (hostAddress != null) {
            // Delete user
            listenerAdapter.removeUserFromRoster(hostAddress);
            adminAdapter.deleteUser(hostAddress, hostAddress);

            // Remove the ActiveProfile
            ActiveProfileUpdater updater = new ActiveProfileUpdater();
            updater.removeActiveProfile(hostAddress);
        }
    }
}
