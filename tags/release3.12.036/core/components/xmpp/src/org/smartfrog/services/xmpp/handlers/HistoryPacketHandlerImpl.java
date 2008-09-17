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
package org.smartfrog.services.xmpp.handlers;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.smartfrog.services.xmpp.WireMessage;
import org.smartfrog.services.xmpp.XmppPacketHandlerImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created 14-Aug-2007 14:19:36
 */

public class HistoryPacketHandlerImpl extends XmppPacketHandlerImpl implements HistoryPacketHandler {

    private ArrayList<Packet> messages;
    private int limit;
    private boolean dumpOnTerminate;

    public HistoryPacketHandlerImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        limit = sfResolve(ATTR_LIMIT, limit, true);
        dumpOnTerminate = sfResolve(ATTR_DUMP_ON_TERMINATE, false, true);
        clear();
    }


    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (dumpOnTerminate) {
            dump();
        }
    }

    public void clear() {
        messages = new ArrayList<Packet>();
    }

    public synchronized int getSize() {
        return messages.size();
    }

    public synchronized void add(Packet message) {
        messages.add(message);
        if (limit >= 0 && messages.size() > limit) {
            messages.remove(0);
        }
    }


    /**
     * Process the next packet by adding the message
     *
     * @param packet the packet to process.
     */
    public void processPacket(Packet packet) {
        add(packet);
    }


    /**
     * Test for the handler having received a message
     *
     * @param sender sender
     * @param regexp regexp to assert for
     * @return the message in a wire format, or null
     */
    public synchronized WireMessage hasMessage(String sender, String regexp) {
        for (Packet packet : messages) {
            if (packetIsMessage(packet)) {
                Message message = (Message) packet;
                String body = message.getBody();
                if (body != null && body.matches(regexp)) {
                    return new WireMessage(message);
                }
            }
        }
        return null;
    }


    /**
     * Dump all packets at info level
     */
    public synchronized void dump() {
        sfLog().info(toXML());
    }

    /**
     * Dump all packets at info level
     *
     * @return an concatenation of all packets' XML forms
     */
    public synchronized String toXML() {
        StringBuilder builder = new StringBuilder();
        for (Packet packet : messages) {
            builder.append(packet.toXML());
            builder.append('\n');
        }
        return builder.toString();
    }

}
