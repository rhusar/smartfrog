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
package org.smartfrog.services.anubis.partition.test.node;



import java.nio.channels.SocketChannel;

import org.smartfrog.services.anubis.basiccomms.connectiontransport.ConnectionComms;
import org.smartfrog.services.anubis.partition.PartitionNotification;
import org.smartfrog.services.anubis.partition.test.msg.GetStatsMsg;
import org.smartfrog.services.anubis.partition.test.msg.GetThreadsMsg;
import org.smartfrog.services.anubis.partition.test.msg.PartitionMsg;
import org.smartfrog.services.anubis.partition.test.msg.SetIgnoringMsg;
import org.smartfrog.services.anubis.partition.test.msg.SetTimingMsg;
import org.smartfrog.services.anubis.partition.views.View;
import org.smartfrog.services.anubis.partition.wire.Wire;
import org.smartfrog.services.anubis.partition.wire.msg.untimed.SerializedMsg;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

public class TestConnection
        extends ConnectionComms
        implements PartitionNotification {

    private TestMgr          testManager;
    private LogSF            log = LogFactory.getLog(this.getClass().toString());

    public TestConnection(SocketChannel channel, TestMgr testManager) {
        super("Anubis: PMConsole connection", channel);
        this.testManager      = testManager;
    }

    /**
     * Connection comms interface
     * @param bytes
     */
    public void deliver(byte[] bytes) {

        SerializedMsg msg = null;
        try {
            msg = (SerializedMsg) Wire.fromWire(bytes);
        } catch (Exception ex) {
            if( log.isWarnEnabled() )
                log.warn(ex);
            return;
        }

        Object obj = msg.getObject();

        if( obj instanceof SetTimingMsg ) {
            SetTimingMsg m = (SetTimingMsg)obj;
            testManager.setTiming(m.interval, m.timeout);
        } else if( obj instanceof GetStatsMsg ) {
            testManager.updateStats(this);
        } else if( obj instanceof SetIgnoringMsg ) {
            testManager.setIgnoring( ((SetIgnoringMsg)obj).ignoring );
        } else if( obj instanceof GetThreadsMsg ) {
            testManager.updateThreads(this);
        } else {
            if( log.isErrorEnabled() )
                log.error("Unrecognised object received in test connection at node" + obj, new Exception());
        }
    }

    public void send(byte[] bytes) {
        if( log.isErrorEnabled() )
            log.error("Should not call send(byte[] bytes) in TestConnection", new Exception());
    }

    public void sendObject(Object obj) {
        try {
            SerializedMsg msg = new SerializedMsg(obj);
            super.send(msg.toWire());
        } catch (Exception ex) {
            if( log.isWarnEnabled() )
                log.warn(ex);
        }
    }

    public void closing() {
        testManager.closing(this);
    }

    /**
     * partition notification interface
     * @param view
     * @param leader
     */
    public void partitionNotification(View view, int leader)  {
        try {
            super.send( (new SerializedMsg(new PartitionMsg(view, leader))).toWire() );
        } catch (Exception ex) {
            if( log.isWarnEnabled() )
                log.warn(ex);
        }
    }
    public void objectNotification(Object o, int i, long l) {}
}
