/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.partition.comms.blocking;


import java.nio.channels.SocketChannel;

import org.smartfrog.services.anubis.basiccomms.connectiontransport.ConnectionAddress;
import org.smartfrog.services.anubis.basiccomms.connectiontransport.ConnectionComms;
import org.smartfrog.services.anubis.partition.comms.Connection;
import org.smartfrog.services.anubis.partition.comms.IOConnection;
import org.smartfrog.services.anubis.partition.comms.MessageConnection;
import org.smartfrog.services.anubis.partition.comms.multicast.HeartbeatConnection;
import org.smartfrog.services.anubis.partition.protocols.partitionmanager.ConnectionSet;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.wire.Wire;
import org.smartfrog.services.anubis.partition.wire.msg.HeartbeatMsg;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;


public class MessageConnectionImpl extends ConnectionComms implements IOConnection {

    private Identity                me                = null;
    private MessageConnection       messageConnection = null;
    private MessageConnectionServer server            = null;
    private ConnectionSet           connectionSet     = null;
    private boolean                 announceTerm      = true;
    private LogSF                   log               = null;

    /**
     * for testing purposes - can set to ignoring incoming messages
     */
    private boolean                 ignoring          = false;

    public MessageConnectionImpl(Identity id, ConnectionSet cs, ConnectionAddress address, MessageConnection mc) {
        super("Anubis: node " + mc.getSender().id + " Connection Comms", address);
        me = id;
        connectionSet = cs;
        messageConnection = mc;
        setPriority(MAX_PRIORITY);
        log = LogFactory.getLog(this.getClass().toString());
    }

    public MessageConnectionImpl(Identity id, SocketChannel channel, MessageConnectionServer mcs, ConnectionSet cs) {
        super("Anubis: new Connection Comms", channel);
        me = id;
        server = mcs;
        connectionSet = cs;
        setPriority(MAX_PRIORITY);
        log = LogFactory.getLog(this.getClass().toString());
    }

    public void send(byte[] bytes) {
        super.send(bytes);
    }

    public void deliver(byte[] bytes) {

        if( ignoring )
            return;

        if( messageConnection == null ) {
            initialMsg(bytes);
        } else {
            messageConnection.deliver(bytes);
        }
    }

    private void initialMsg(byte[] bytes) {

        Object obj = null;
        try {
            obj = Wire.fromWire(bytes);
        } catch (Exception ex) {
            if( log.isErrorEnabled() )
                log.error(me + " failed to unmarshall initial message on new connection - shutdown", ex);
            shutdown();
        }


        /**
         * must be a heartbeat message
         */
        if( !(obj instanceof HeartbeatMsg ) ) {
            if( log.isErrorEnabled() )
                log.error(me + " did not receive a heartbeat message first - shutdown", new Exception());
            shutdown();
            return;
        }

        HeartbeatMsg hbmsg = (HeartbeatMsg)obj;

        /**
         * There must be a valid connection (heartbeat connection)
         */
        if( !connectionSet.getView().contains(hbmsg.getSender()) ) {
            if( log.isErrorEnabled() )
                log.error(me + " did not have incoming connection from " + hbmsg.getSender().toString() + " in the connection set");
            shutdown();
            return;
        }

        Connection con = connectionSet.getConnection(hbmsg.getSender());

        /**
         * If it is a message connection then attempt to assign this
         * impl to that connection. If successful then record the message
         * connection so all further messages go directly to it. If not
         * successful then shutdown the this implementation object and
         * abort.
         */
        if( con instanceof MessageConnection ) {
            if( ((MessageConnection)con).assignImpl(this) ) {
                messageConnection = (MessageConnection)con;
                setName("Anubis: node " + con.getSender().id + " Connection Comms");
                messageConnection.deliver(bytes);
            } else {
                if( log.isErrorEnabled() )
                    log.error(me + " failed to assign incoming connection from " + con.getSender().toString());
                shutdown();
            }
            return;
        }

        /**
         * By now we should be left with a heartbeat connection - sanity check
         */
        if( !(con instanceof HeartbeatConnection) ) {
            if( log.isErrorEnabled() )
                log.error(me + " ?!? incoming connection from " + con.getSender().toString() + " is in connection set, but not heartbeat or message type");
            shutdown();
            return;
        }
        HeartbeatConnection hbcon = (HeartbeatConnection)con;

        /**
         * If the connection is a heartbeat connection then the other end must
         * be setting up the connection without this end having requested it.
         * That means the other end must want it, so check the msgLink field for
         * this end is set - this is a sanity check.
         *
         * *********************************************************************
         *
         * The case can happen, so the above comment is incorrect.
         * If the user does a connect and then
         * disconnect without sending a message, then the other end could
         * initiate a connection neither end needs in response to the initial
         * connect. Do not count this as an error, but do log its occurance.
         */
        if( !hbmsg.getMsgLinks().contains(me.id) ) {
            if( log.isErrorEnabled() )
                log.error(me + " VALID CASE - FOR INFORMATION ONLY:=> incoming connection from " + con.getSender().toString() + " when neither end wants the connection");
            // next two lines removed to allow this case
            // shutdown();
            // return;
        }

        /**
         * Now we are left with a valid heartbeat connection and the other
         * end is initiating a message connection, so create this end.
         *
         * Note that the connection set only finds out about the newly created
         * message connection when it is informed by the call to
         * connectionSet.useNewMessageConnection(), so it can not terminate the
         * connection before the call to messageConnection.assignImpl(). Also, we
         * created the message connection, so we know it does not yet have an impl.
         * Hence we can assume it will succeed in assigning the impl.
         */
        messageConnection = new MessageConnection(me, connectionSet, hbcon.getProtocol(), hbcon.getCandidate());
        messageConnection.assignImpl(this);
        messageConnection.deliver(bytes);

        /**
         * if the call to connectionSet.useNewMessageConnection() then a connection
         * has been created since we checked for it above with connectionSet.getConnection().
         * The other end will not make two connection attempts at the same time, but if this
         * thread is delayed during the last 20 lines of code for long enough for the following to
         * happen:
         * 1. other end time out connection +
         * 2. quiesence period +
         * 3. this end rediscover other end in multicast heartbeats +
         * 4. other end initiates new connection attempt +
         * 5. new connection attempt gets accepted (new thread created for it) +
         * 6. read first heartbeat and get through this code in the new thread.
         * Then it could beat this thread to it. If all this happens (and based on
         * the premise "if it can happen it will happen") then this thread should rightly
         * comit suicide in disgust!!!!
         */
        if( !connectionSet.useNewMessageConnection(messageConnection) ) {
            if( log.isErrorEnabled() )
                log.error(me + "Concurrent creation of message connections from " + messageConnection.getSender());
            shutdown();
            return;
        }
        setName("Anubis: node " + messageConnection.getSender().id + " Connection Comms");
    }


    /**
     * Close down the connection.
     * 
     * Closing is called by {@link #shutdown()}.  shutdown is used to terminate the
     * connection both here (from the "outside") and in the implementation
     * of ConnectionComms (from the "inside"). The connection closes itself
     * from the inside if there is some kind of error on the connection.
     *
     * Here closing is used to clean up by telling the server socket to
     * remove any record it has of this connection and telling the
     * messageConnection that the connection has closed. The later can
     * be disabled (as is done in terminate()).
     */
    public void closing() {
        if(server != null)
            server.removeConnection(this);
        if( announceTerm && (messageConnection != null) )
            messageConnection.closing();
    }

    /**
     * Shut down the connection.
     * Terminate is used to instruct the implementation to shutdown the
     * connection. announceTerm is set to false so that the closing()
     * method does not call back to the messageConnection.
     */
    public void terminate() { announceTerm = false; shutdown(); }
    public void silent() { announceTerm = false; }

    /**
     * set ignoring value to determine if connections should be ignored
     * @param ignoring
     */
    public void setIgnoring(boolean ignoring) { this.ignoring = ignoring; }

    public void logClose(String reason, Throwable throwable) {

        if( ignoring )
            return;

        if( messageConnection == null ) {

             if( log.isDebugEnabled() )
                 log.debug(me + " shutdown unassigned message connection transport:" + reason, throwable);

        } else {
            messageConnection.logClose(reason, throwable);
        }
    }

}
