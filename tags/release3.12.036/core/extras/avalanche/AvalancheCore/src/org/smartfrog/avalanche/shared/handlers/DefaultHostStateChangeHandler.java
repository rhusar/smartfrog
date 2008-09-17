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
import org.smartfrog.avalanche.shared.*;

/**
 * Updates state of the host in Active Profile. This is invoked when a host goes down or a
 * new host comes up.
 *
 * @author sanjaydahiya
 */
public class DefaultHostStateChangeHandler implements HostStateChangeHandler {
    private final static Log log = LogFactory.getLog(DefaultHostStateChangeHandler.class);

    /**
     * Updates the state of a given host in the database
     * @param e is the event that contains the information about the host.
     */
    public void handleEvent(HostStateEvent e) {
        log.info("Hosts State Changed: " + e.getHostName() + " : " + e.isAvailable());
        ActiveProfileUpdater updater = new ActiveProfileUpdater();
        updater.setMachineAvailability(e.getHostName(), e.isAvailable());
	}
}
