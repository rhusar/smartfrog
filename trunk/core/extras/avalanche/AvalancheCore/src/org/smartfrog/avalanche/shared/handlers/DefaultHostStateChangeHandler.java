/**
 (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org
 */
package org.smartfrog.avalanche.shared.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.shared.handlers.HostStateChangeHandler;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.shared.HostStateEvent;

/**
 * Updates state of the host in Active Profile. This is invoked when a host goes down or a
 * new host comes up.
 *
 * @author sanjaydahiya
 */
public class DefaultHostStateChangeHandler implements HostStateChangeHandler {
    private static Log log = LogFactory.getLog(DefaultHostStateChangeHandler.class);
    ActiveProfileManager profileManager;

    public DefaultHostStateChangeHandler() {
        AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
        try {
            profileManager = factory.getActiveProfileManager();
        } catch (ModuleCreationException e) {
            // TODO : Bad, bring down the whole system
            e.printStackTrace();
            log.fatal(e);
        }
    }

    /**
     * updates the host availability status in database.
     * TODO: Host state is set as "Available" or "Not Available" change to something
     * better. .
     */
    public void handleEvent(HostStateEvent e) {
        log.info("Hosts State Changed: " + e.getHostName() + " : " + e.isAvailable());

        String hostPath = e.getHostName();
        String hostPresence = e.isAvailable() ? "Available" : "Not Available";

        try {
            ActiveProfileType type = profileManager.getProfile(hostPath);

            // No ActiveProfile found - create one
            if (type == null) {
                log.info("Creating new ActiveProfile for host " + hostPath);
                try {
                    type = profileManager.newProfile(hostPath);
                } catch (Exception x) {

                }
            }

            // Profile could not be created - log error
            if (type != null)
                    type.setHostState(hostPresence);
            else
                log.error("Could not retrieve ActiveProfileType for host " + hostPath);

            profileManager.setProfile(type);
        } catch (DatabaseAccessException ex) {
            log.error(ex);
		}
	}

}
