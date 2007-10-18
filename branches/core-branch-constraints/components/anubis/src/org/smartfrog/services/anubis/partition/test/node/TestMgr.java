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



import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.smartfrog.services.anubis.basiccomms.connectiontransport.ConnectionAddress;
import org.smartfrog.services.anubis.basiccomms.connectiontransport.ConnectionAddressData;
import org.smartfrog.services.anubis.partition.PartitionManager;
import org.smartfrog.services.anubis.partition.Status;
import org.smartfrog.services.anubis.partition.protocols.partitionmanager.ConnectionSet;
import org.smartfrog.services.anubis.partition.test.msg.IgnoringMsg;
import org.smartfrog.services.anubis.partition.test.msg.PartitionMsg;
import org.smartfrog.services.anubis.partition.test.msg.ThreadsMsg;
import org.smartfrog.services.anubis.partition.test.msg.TimingMsg;
import org.smartfrog.services.anubis.partition.test.stats.StatsManager;
import org.smartfrog.services.anubis.partition.views.View;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


public class TestMgr extends CompoundImpl implements Compound {

    private Set               connections       = new HashSet();
    private PartitionManager  partitionManager  = null;
    private TestServer        connectionServer  = null;
    private ConnectionSet     connectionSet     = null;
    private StatsManager      statistics        = new StatsManager();
    private static final long STATSRATE         = 5;
    private long              statsInterval     = STATSRATE * 1000; // adjusts with heartbeat timing
    private long              lastStats         = 0;

    public TestMgr(String host, PartitionManager partitionManager) throws IOException, Exception {
        this.partitionManager = partitionManager;
        connectionServer = new TestServer(this, host);
    }


    public TestMgr() throws Exception {
        super();
    }

    public void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy();
        try {
            ConnectionAddress ca = ((ConnectionAddressData)sfResolve("contactAddress")).getConnectionAddress();
            connectionServer = new TestServer(this, ca.ipaddress.getHostName());
            partitionManager = (PartitionManager) sfResolveWithParser(
                "partitionManager");
        }
        catch (Exception ex) {
            throw new SmartFrogException(ex);
        }
    }

    public void sfStart() throws RemoteException, SmartFrogException {
        super.sfStart();
        connectionSet = (ConnectionSet)sfResolveWithParser("ATTRIB connectionSet");
        connectionSet.registerTestManager(this);
        start();
    }

    public void sfTerminateWith(TerminationRecord status) {
        stop();
        super.sfTerminateWith(status);
    }


    public void start() {
        connectionServer.start();
    }


    public void stop() {
        connectionServer.shutdown();
        Iterator iter = connections.iterator();
        while(iter.hasNext()) {
            ((TestConnection)iter.next()).shutdown();
        }
    }

    public void newConnection(SocketChannel channel) {
        TestConnection connection = new TestConnection(channel, this);
        if( connection.connected() ) {
            synchronized(connections) {
                connections.add(connection);
            }
            partitionManager.register(connection);
            updateStatus(connection);
            updateTiming(connection);
            connection.start();
        }
    }

    public void closing(TestConnection connection) {
        partitionManager.deregister(connection);
        synchronized(connections) {
            connections.remove(connection);
        }
    }

    public ConnectionAddress getAddress() {
        return connectionServer.getAddress();
    }

    public void updateStatus(TestConnection tc) {
        Status status = partitionManager.getStatus();
        tc.sendObject(new PartitionMsg(status.view, status.leader));
    }

    public void updateTiming() {
        Iterator iter = connections.iterator();
        while( iter.hasNext() )
            updateTiming((TestConnection)iter.next());
    }

    public void updateTiming(TestConnection tc) {
        tc.sendObject(new TimingMsg( connectionSet.getInterval(),
                                     connectionSet.getTimeout() ));
    }

    public void setTiming(long interval, long timeout) {
        connectionSet.setTiming(interval, timeout);
        updateTiming();
        statsInterval = STATSRATE * interval;
    }

    public void updateStats(long timenow) {
        if( lastStats < (timenow - statsInterval) ) {
            Iterator iter = connections.iterator();
            while( iter.hasNext() )
                updateStats((TestConnection)iter.next());
            lastStats = timenow;
        }
    }

    public void updateStats(TestConnection tc) {
        tc.sendObject( statistics.statsMsg() );
    }

    public void updateThreads(TestConnection tc) {
        String status = connectionSet.getThreadStatusString();
        tc.sendObject( new ThreadsMsg(status) );
    }

    public void schedulingInfo(long time, long delay) {
        statistics.schedulingInfo(time, delay);
        updateStats(time);
    }


    /**
     * set the nodes to ignore
     * @param ignoring
     */
    public void setIgnoring(View ignoring) {
        connectionSet.setIgnoring(ignoring);
        updateIgnoring(ignoring);
    }

    public void updateIgnoring(View ignoring) {
        Iterator iter = connections.iterator();
        while( iter.hasNext() )
            updateIgnoring(ignoring, (TestConnection)iter.next());
    }

    public void updateIgnoring(View ignoring, TestConnection tc) {
        tc.sendObject(new IgnoringMsg(ignoring));
    }


}
