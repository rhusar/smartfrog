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
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;

/**
 * The listener listens for messages.
 */
public class XmppListenerImpl extends AbstractXmppPrim implements XmppListener,
        ConnectionListener, PacketListener, LocalXmppPacketHandler {
    //the connection
    private XMPPConnection connection;

    //the exception that caused the connection to be closed
    private volatile Exception closedConnection;

    private boolean reconnect;

    private long timeout;

    private long reconnectStarted;

    private List<LocalXmppPacketHandler> handlers=new ArrayList<LocalXmppPacketHandler>();

    public XmppListenerImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException
     *                                  failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        // add the xmpp event extension provider        
        ProviderManager.addExtensionProvider(XMPPEventExtension.rootElement, XMPPEventExtension.namespace, new XMPPEventExtensionProvider());
        handlers.add(this);
        reconnect = sfResolve(ATTR_RECONNECT, reconnect, true);
        timeout = sfResolve(ATTR_TIMEOUT, 0, true) * 60000L;
        connectAndRegister();
    }

    /**
     * Connect to the server, register anything that needs to listen
     * @throws SmartFrogException if needed
     */
    protected void connectAndRegister() throws SmartFrogException {
        connection = login();
        registerAllHandlers();
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
     * @throws SmartFrogLivenessException
     *                                  component is terminated
     * @throws RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        innerPing();
    }

    /**
     * Override point.
     * @throws SmartFrogLivenessException
     */
    protected void innerPing() throws SmartFrogLivenessException {
        checkConnection();
    }

    /**
     * Check the connection.
     * <ol>
     * <li>If disconnected, try to reconnect.</li>
     * <li>If the connection was closed, raise an exception with the cause</li>
     * <li>If the connection is null, raise that as an exception</li>
     * @throws SmartFrogLivenessException
     */
    protected void checkConnection() throws SmartFrogLivenessException {
        if(connection==null && reconnect) {
            reconnect();
        } else {
            if (closedConnection != null) {
                throw new SmartFrogLivenessException(closedConnection);
            }
            if (connection == null) {
                throw new SmartFrogLivenessException("Connection has been closed");
            }
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
        if (isConnected()) {
            try {
                connection.close();
            } catch (Exception e) {
                //ignore this
            } finally {
                connection = null;
            }
        }
    }                                           


    /**
     * Our filter is always a message filter
     * @return a new message filter
     */
    public PacketFilter getFilter() {
        return new MessageFilter();
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

    private synchronized boolean reconnect() throws SmartFrogLivenessException {
        if(isConnected()) {
            throw new IllegalStateException("Cannot reconnect -we are already connected");
        }
        if(reconnect) {
            if(reconnectStarted==0) {
                reconnectStarted= System.currentTimeMillis();
            }
            try {
                connectAndRegister();
                reconnectStarted=0;
                return true;
            } catch (SmartFrogException e) {
                sfLog().debug("Failing to reconnect",e);
                if (timeout > 0 &&
                        System.currentTimeMillis() > (reconnectStarted + timeout)) {
                    throw new SmartFrogLivenessException("Could not reconnect",e);
                }
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Register or reregister all packet handlers
     */
    protected synchronized void registerAllHandlers() {
        for(LocalXmppPacketHandler handler:handlers) {
            addHandler(handler);
        }
    }

    private void addHandler(LocalXmppPacketHandler handler) {
        sfLog().info("Adding handler: " + handler);
        connection.addPacketListener(handler,handler.getFilter());
    }

    /**
     * Add a new handler to be registered now or when we are next connected.
     * You do not need to be online to register a handler.
    * this is not remotable; you need to call it locally.
    * @param handler the handler to register
    */
    public synchronized void registerPacketHandler(LocalXmppPacketHandler handler) {
        handlers.add(handler);
        if(isConnected()) {
            addHandler(handler);
        }
    }

    /**
     * Unregister a packet handler.
     * This removes it from the handler list, and unregisters it from any active connection.
     * @param handler handler to unregister
     * @return true if we unregistered.
     */
    public synchronized boolean unregisterPacketHandler(LocalXmppPacketHandler handler) {
        if(!handlers.remove(handler)) {
            return false;
        }
        if (isConnected()) {
            connection.removePacketListener(handler);
        }
        return true;
    }


    /**
     * Get the active connection
     * @return the connection; this will be null when disconnected
     */
    public XMPPConnection getConnection() {
        return connection;
    }

    /**
     * Is this connection made?
     * @return true if we are connected
     */
    public boolean isConnected() {
        return connection!=null;
    }

    /**
     * Send a text message if connected
     * @param recipient who gets the message
     * @param subject subject of the message
     * @param text the text
     * @return true if the message was sent
     */
    public boolean sendMessage(String recipient,String subject,String text, PacketExtension ext) {
        if(isConnected()) {
            Message m = new Message(recipient);
            m.setSubject(subject);
            m.setBody(text);
            m.setType(Message.Type.NORMAL);
            if (ext != null)
                m.addExtension(ext);
            connection.sendPacket(m);
            return true;
        }
        return false;
    }
}
