/* (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * The Xmpp client can post messages
 */
public class XmppClientImpl extends AbstractXmppPrim implements XmppClient {


    private String destination;
    private String message;

    public XmppClientImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        destination = sfResolve(ATTR_DESTINATION, destination, false);
        message = sfResolve(ATTR_MESSAGE, message, false);
        if (message != null) {
            post(message);
        }
        //workflow triggered shutdown
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                null,
                null,
                null);
    }

    /**
     * Post a message to the default destination.
     *
     * @param text text to send
     * @throws RemoteException    on networking trouble
     * @throws SmartFrogException if there is no default destination, or something went wrong with the communications
     */
    public void post(String text)
            throws RemoteException, SmartFrogException {
        post(destination, text);
    }

    /**
     * Post a message to the specified recipient
     *
     * @param recipient target user
     * @param text      text to send
     * @throws RemoteException    on networking trouble
     * @throws SmartFrogException if something went wrong with the communications
     */
    public void post(String recipient, String text)
            throws RemoteException, SmartFrogException {
        if (recipient == null) {
            throw new SmartFrogRuntimeException("No recipient for XMPP message");
        }
        try {
            XMPPConnection connection;
            connection = login();
            try {
                Message m = new Message(recipient);
                m.setBody(text);
                m.setType(Message.Type.NORMAL);
                connection.sendPacket(m);
            } finally {
                closeConnection(connection);
            }
        } catch (IllegalStateException e) {
            //smack uses IllegalStateException for signalling problems
            throw new SmartFrogException(e);
        }
    }
}
