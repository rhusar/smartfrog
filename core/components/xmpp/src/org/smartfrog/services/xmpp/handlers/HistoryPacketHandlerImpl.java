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

import org.jivesoftware.smack.packet.Packet;
import org.smartfrog.services.xmpp.XmppPacketHandlerImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created 14-Aug-2007 14:19:36
 *
 */

public class HistoryPacketHandlerImpl extends XmppPacketHandlerImpl implements Remote {

    private List<Packet> messages;
    private int limit;
    public static final String ATTR_LIMIT = "limit";

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
        limit=sfResolve(ATTR_LIMIT,limit,true);
        clear();
    }

    public void clear() {
        messages=new LinkedList<Packet>();
    }

    public synchronized int getSize() {
        return messages.size();
    }

    public synchronized void add(Packet message) {
        messages.add(message);
        if(limit >= 0 && messages.size()>limit) {
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


}
