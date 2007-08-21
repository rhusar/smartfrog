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
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.shared.MonitoringConstants;
import org.smartfrog.avalanche.shared.ActiveProfileUpdater;
import org.smartfrog.avalanche.shared.XMPPEventExtension;
import org.smartfrog.avalanche.shared.handlers.XMPPPacketHandler;
import org.jivesoftware.smack.packet.Packet;

/**
 * This is a server side handler, this updates the active profile of a host
 * on receiving a message. The handler is indepenent of monitoring protocols
 * it uses an interface MonitoringEvent which can be implemented in different
 * ways for differnet monitoring protocols.
 *
 * @author sanjaydahiya
 */
public class ActiveProfileUpdateHandler implements XMPPPacketHandler {
    ActiveProfileManager profileManager;
    private static Log log = LogFactory.getLog(ActiveProfileUpdateHandler.class);

    public ActiveProfileUpdateHandler() {

    }
    
    /**
     * Callback method, this is invoked by event listener on the server when a
     * new Monitoring Event is received. This method updates the Active Profile on
     * a host with the host/module state received in the event.
     * @param p the to-be-handled event
     */
    public void handlePacket(Packet p) {
        if (p != null)
        {
            // get the xmpp event
            XMPPEventExtension pe = (XMPPEventExtension) p.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
		    log.info("Event received: " + pe);

            // decide what to do
            ActiveProfileUpdater updater = new ActiveProfileUpdater();
            switch (pe.getMessageType()) {
                case MonitoringConstants.MODULE_STATE_CHANGED:
                case MonitoringConstants.MODULE_OPERATION_FAILED:
                    // Set module state
                    updater.setModuleState(pe);
                    break;
                case MonitoringConstants.HOST_VANISH:
                case MonitoringConstants.HOST_STARTED:
                    // Log message to history
                    updater.addNewMessage(pe);
                    break;
                case MonitoringConstants.VM_MESSAGE:
                    updater.processVMMessage(pe);
                    break;
                default:
                    break;

            }
        }
    }
}
