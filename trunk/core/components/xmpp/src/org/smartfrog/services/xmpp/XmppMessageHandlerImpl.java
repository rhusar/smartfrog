/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xmpp;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A message handler that only accepts messages Created 14-Aug-2007 13:51:30
 */

public class XmppMessageHandlerImpl extends XmppPacketHandlerImpl implements Remote {


    public XmppMessageHandlerImpl() throws RemoteException {
    }


    /**
     * Tests whether or not the specified packet should pass the filter.
     *
     * @param packet the packet to test.
     * @return true if and only if <tt>packet</tt> passes the filter.
     */
    public boolean accept(Packet packet) {
        return packetIsMessage(packet);
    }

    /**
     * Convert the package to a message and relay to {@link #processMessage(Message)}
     *
     * @param packet the packet to process.
     * @throws IllegalArgumentException if the packet is not a message
     */
    public void processPacket(Packet packet) {
        if (!packetIsMessage(packet)) {
            //sanity check in  case a subclass overrides the accept cal;;
            throw new IllegalArgumentException("Not a message " + packet);
        }
        processMessage((Message) packet);
    }

    /**
     * A single thread is responsible for invoking all listeners, so it's very important that implementations of this
     * method not block for any extended period of time.
     *
     * @param message message to process
     */
    public void processMessage(Message message) {
        sfLog().info(message);
    }
}
