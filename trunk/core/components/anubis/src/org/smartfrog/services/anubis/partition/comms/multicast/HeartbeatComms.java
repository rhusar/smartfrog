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
package org.smartfrog.services.anubis.partition.comms.multicast;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.smartfrog.services.anubis.basiccomms.multicasttransport.MulticastAddress;
import org.smartfrog.services.anubis.basiccomms.multicasttransport.MulticastComms;
import org.smartfrog.services.anubis.partition.protocols.heartbeat.HeartbeatReceiver;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.views.View;
import org.smartfrog.services.anubis.partition.wire.Wire;
import org.smartfrog.services.anubis.partition.wire.msg.Heartbeat;
import org.smartfrog.services.anubis.partition.wire.msg.HeartbeatMsg;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;


public class HeartbeatComms extends MulticastComms implements HeartbeatCommsIntf {

    private Identity                 me                       = null;
    private HeartbeatReceiver        connectionSet            = null;
    private Map                      messageHandlers          = Collections.synchronizedMap(new HashMap());
    private LogSF                    log = LogFactory.getLog(this.getClass().toString());

    /**
     * for testing purposes
     */
    private View                     ignoring                 = null;
    private Object                   ingnoringMonitor         = new Object();


   public HeartbeatComms(MulticastAddress address, HeartbeatReceiver cs, String threadName, Identity id) throws Exception {
       super(threadName, address);
       me = id;
       connectionSet = cs;
       setPriority(Thread.MAX_PRIORITY);
   }

    protected void deliverBytes(byte[] bytes) {

        Object obj = null;
        try {
            obj = Wire.fromWire(bytes);
        } catch (Exception ex) {
            if( log.isErrorEnabled() )
                log.error("Error reading wire form message", ex);
            return;
        }

        if (obj instanceof Heartbeat)
            handleHeartbeat( (Heartbeat) obj);
        else
            handleNonHeartbeat(obj);
    }

    private void handleHeartbeat(Heartbeat hb) {
        /**
         * if not right magic discard it
         */
        if( !hb.getSender().equalMagic(me) )
            return;

        /**
         * for testing purposes - can ignore messages from
         * specified senders
         */
        if ( isIgnoring(hb.getSender()) )
             return;

         /**
          * Deliver new heartbeat message - this needs to be synchronized
          * on the connection set so that nothing changes in the process.
          *
          * The connection returned by getConnection may or may not be active
          * (i.e. it may be quiescing) if it is not active it is up to the
          * connection itself to deal with the heartbeat.
          */
        connectionSet.receiveHeartbeat(hb);
    }


    public void registerMessageHandler(Class type, MessageHandler handler) {
        messageHandlers.put(type, handler);
    }

    public void deregisterMessageHandler(Class type) {
        messageHandlers.remove(type);
    }

    private void handleNonHeartbeat(Object msg) {
        MessageHandler handler = (MessageHandler)messageHandlers.get(msg.getClass());
        if( handler != null )
            handler.deliverObject(msg);
        else
            if( log.isErrorEnabled() )
                log.error(me + " No handler found for message: " + msg);
    }

    /**
     * Change of interface - connection users send objects or messages.
     * sendObject(Object) is not permitted because the only heartbeats
     * can be sent on a heartbeat connection. But the method of the base class
     * MulticastComms to send is to take an arbitrary Object.
     * sendObject().
     *
     * @param obj
     */
    //public void sendObject(Object obj) { return; }

    public void sendHeartbeat(HeartbeatMsg msg) {
        try { super.sendObject(msg.toWire()); }
        catch (Exception ex) {
            if( log.isErrorEnabled() )
                log.error("Error sending heartbeat message", ex);
        }
    }

    /**
     * determine which nodes to ignore if any.
     * This will be called from the connection set - ignoring is used in a
     * critical section in deliverObject() that uses connectionSet as its
     * monitor.
     * @param ignoringUpdate a view
     */
    public void setIgnoring(View ignoringUpdate){
        synchronized(ingnoringMonitor) {
            ignoring = (ignoringUpdate.isEmpty() ? null : ignoringUpdate);
        }
    }


    public boolean isIgnoring(Identity id) {
        synchronized(ingnoringMonitor) {
            return (ignoring != null) && ignoring.contains(id);
        }
    }

    public void terminate() {
        shutdown();
    }

}



