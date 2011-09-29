/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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

import java.rmi.RemoteException;

/**
 * This component echoes back Created 12-Feb-2008 10:37:03
 */

public class EchoHandlerImpl extends XmppMessageHandlerImpl {

    public EchoHandlerImpl() throws RemoteException {
    }

    /**
     * Echo the message it back to the caller
     *
     * @param message the message to process.
     */
    public void processMessage(Message message) {

        String from = message.getFrom();
        String to = message.getTo();
        if (from.equals(to)) {
            //don't echo messages from ourselves. That would be silly.
            sfLog().debug("Ignoring a message sent by ourselves");
        } else {
            message.setTo(from);
            try {
                getListener().sendMessage(message);
            } finally {
                //reset the destination
                message.setTo(to);
            }
        }
    }
}
