/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.vast.architecture;

import org.smartfrog.avalanche.shared.handlers.XMPPPacketHandler;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.jivesoftware.smack.packet.Packet;

/**
 * Vast listener for incoming Avalanche messages.
 */
public class VastListener implements XMPPPacketHandler {
    /**
     * Reference to the environment constructor.
     */
    private EnvironmentConstructorImpl refEnvCon;

    public VastListener(EnvironmentConstructorImpl inEnvCon) {
        refEnvCon = inEnvCon;
    }

    public void handlePacket(Packet p) {
        if (p != null)
        {
            // get the xmpp event
            XMPPEventExtension pe = (XMPPEventExtension) p.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
            if (pe != null) {
                refEnvCon.sfLog().info("Event received: " + pe);

                switch (pe.getMessageType()) {
                    case MonitoringConstants.MODULE_STATE_CHANGED:
                    case MonitoringConstants.MODULE_OPERATION_FAILED:
                        // code goes here
                        break;
                    case MonitoringConstants.HOST_SHUTTING_DOWN:
                    case MonitoringConstants.HOST_VANISH:
                        refEnvCon.hostVanished(pe.getHost());
                        break;
                    case MonitoringConstants.HOST_STARTED:
                        refEnvCon.hostStarted(pe.getHost());
                        break;
                    case MonitoringConstants.VM_MESSAGE:
                        refEnvCon.handleVMMessages(pe);
                        break;
                    default:
                        refEnvCon.sfLog().info("No matching monitoring constant found.");
                        break;

                }
            }
        }
    }
}
