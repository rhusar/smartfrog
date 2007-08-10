package org.smartfrog.avalanche.shared;

/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Modifies the ActiveProfile of a given hosts
 */
public class ActiveProfileUpdater {
    private static Log log = LogFactory.getLog(ActiveProfileUpdater.class);

    /**
     * Updates the availability record of a given machine
     * This needs to be performed e.g. after a XMPP presence change is detected.
     *
     * @param profileManager is the ActiveProfileManager which holds the Profile of the specifed host.
     * @param hostId is the name of the machine
     * @param availability true if the machine is available; false if it is not.
     */
    public static void setMachineAvailability(ActiveProfileManager profileManager, String hostId, boolean availability) {
        try {
            ActiveProfileType type = profileManager.getProfile(hostId);

            // No ActiveProfile found - create one
            if (type == null) {
                try {
                    type = profileManager.newProfile(hostId);
                } catch (Exception x) {

                }
            }

            // Profile could not be created - log error
            if (type != null)
                type.setHostState(availability?"Available":"Not Available");
            else
                log.error("Could not retrieve ActiveProfileType for host " + hostId);

            profileManager.setProfile(type);
        } catch (Exception ex) {
            log.error(ex);
        }
    }
}
