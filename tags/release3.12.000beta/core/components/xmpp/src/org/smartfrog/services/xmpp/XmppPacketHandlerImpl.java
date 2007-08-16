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
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.XMPPConnection;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import java.rmi.Remote;

/** The packet handler logs everything and print it out Created 14-Aug-2007 13:51:30 */

public class XmppPacketHandlerImpl extends PrimImpl implements
        XmppMessageHandler, LocalXmppPacketHandler, PacketFilter,Remote {

    private XmppListenerImpl listener;

    public static final String ERROR_WRONG_TYPE_OR_PROCESS = "The listener must be an instance of XmppListenerImpl in the same process";

    public XmppPacketHandlerImpl() throws RemoteException {
    }

    /**
     * The base implementation returns this; children are free to override it
     * @return this
     */
    public PacketFilter getFilter() {
        return this;
    }


    /**
     * Get the current listener
     * @return the listener
     */
    protected XmppListenerImpl getListener() {
        return listener;
    }

    /**
     * Get the active connection (may be null)
     * @return the active connection (may be null)
     */
    protected XMPPConnection getConnection() {
        return listener.getConnection();
    }

    /**
    * Can be called to start components. Subclasses should override to provide functionality Do not block in this
    * call, but spawn off any main loops!
    *
    * @throws SmartFrogException failure while starting
    * @throws RemoteException    In case of network/rmi error
    */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Prim owner=null;
        owner=sfResolve(ATTR_LISTENER,owner,true);
        if(!(owner instanceof XmppListenerImpl)) {
            throw new SmartFrogDeploymentException(ERROR_WRONG_TYPE_OR_PROCESS);
        }
        listener=(XmppListenerImpl) owner;
        listener.registerPacketHandler(this);
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if(listener!=null) {
            listener.unregisterPacketHandler(this);
            listener=null;
        }
    }

    /**
     * Tests whether or not the specified packet should pass the filter.
     *
     * @param packet the packet to test.
     * @return true
     */
    public boolean accept(Packet packet) {
        return true;
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
        sfLog().info(packet);
    }
}
