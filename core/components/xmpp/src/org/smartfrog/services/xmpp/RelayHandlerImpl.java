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

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 *
 * Created 14-Aug-2007 16:35:25
 *
 */

public class RelayHandlerImpl extends XmppMessageHandlerImpl {

    private Vector<String> recipients;
    public static final String ATTR_TO = "to";

    public RelayHandlerImpl() throws RemoteException {
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
        Object to = sfResolve(ATTR_TO,true);
        if(to instanceof Vector) {
            recipients=(Vector<String>) to;
        } else {
            recipients=new Vector<String>(1);
            recipients.add(to.toString());
        }
    }

    /**
     * Process the next packet sent to this packet listener.<p>
     *
     * A single thread is responsible for invoking all listeners, so it's very important that implementations of this
     * method not block for any extended period of time.
     *
     * @param packet the packet to process.
     */
    public void processPacket(Packet packet) {
        super.processPacket(packet);
        Message m=(Message) packet;
        for(String to:recipients) {
            getListener().sendMessage(to, m.getSubject(), m.getBody());
        }

    }
}
