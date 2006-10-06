/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * The listener listens for messages.
 */
public class XmppListenerImpl extends AbstractXmppPrim implements XmppListener,
        ConnectionListener, PacketListener {
    protected XMPPConnection connection;
    protected Exception closedConnection;


    public XmppListenerImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        connection = login();
        connection.addConnectionListener(this);
        connection.addPacketListener(this, new MessageFilter());
    }


    /**
     * Liveness call in to check if this component is still alive. This method
     * can be overriden to check other state of a component. An example is
     * Compound where all children of the compound are checked. This basic check
     * updates the liveness count if the ping came from its parent. Otherwise
     * (if source non-null) the liveness count is decreased by the
     * sfLivenessFactor attribute. If the count ever reaches 0 liveness failure
     * on tha parent has occurred and sfLivenessFailure is called with source
     * this, and target parent. Note: the sfLivenessCount must be decreased
     * AFTER doing the test to correctly count the number of ping opportunities
     * that remain before invoking sfLivenessFailure. If done before then the
     * number of missing pings is reduced by one. E.g. if sfLivenessFactor is 1
     * then a sfPing from the parent sets sfLivenessCount to 1. The sfPing from
     * a non-parent would reduce the count to 0 and immediately fail.
     *
     * @param source source of call
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (closedConnection != null) {
            throw new SmartFrogLivenessException(closedConnection);
        }
        if (connection == null) {
            throw new SmartFrogLivenessException("Connection has been closed");
        }
    }


    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                //ignore this
            }
        }
    }


    /**
     * Process the next packet sent to this packet listener.<p>
     * <p/>
     * A single thread is responsible for invoking all listeners, so it's very
     * important that implementations of this method not block for any extended
     * period of time.
     *
     * @param packet the packet to process.
     */
    public void processPacket(Packet packet) {
        if (!(packet instanceof Message)) {
            return;
        }
        Message message = (Message) packet;
        StringBuffer buffer = new StringBuffer();
        buffer.append(message.getFrom());
        buffer.append(": ");
        buffer.append(message.getBody());
        sfLog().info(buffer);
    }

    /**
     * Notification that the connection was closed normally.
     */
    public synchronized void connectionClosed() {
        connection = null;
    }

    /**
     * Notification that the connection was closed due to an exception.
     *
     * @param e the exception.
     */
    public synchronized void connectionClosedOnError(Exception e) {
        closedConnection = e;
        connection = null;
    }
}
