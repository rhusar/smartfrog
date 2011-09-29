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
import org.smartfrog.services.xmpp.XmppMessageHandlerImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;

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
    public static final Reference REF_TO=new Reference(ATTR_TO);

    public RelayHandlerImpl() throws RemoteException {
    }


    /**
     * Start up by extracting the list of recipients
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Object to = sfResolve(REF_TO,true);
        if(to instanceof Vector) {
            recipients= ListUtils.resolveStringList(this, REF_TO,true);
        } else {
            recipients=new Vector<String>(1);
            recipients.add(to.toString());
        }
    }

    /**
     * Process the next message sent to this packet listener.<p>
     *
     * A single thread is responsible for invoking all listeners, so it's very important that implementations of this
     * method not block for any extended period of time.
     *
     * @param message the packet to process.
     */
    public void processMessage(Message message) {
        super.processMessage(message);
        String destination = message.getTo();
        for(String to:recipients) {
            //avoid loops by not sending any messages back to ourselves.
            if(!(to.equals(destination))) {
                getListener().sendMessage(to, message.getSubject(), message.getBody(), null);
            }
        }
    }
}
