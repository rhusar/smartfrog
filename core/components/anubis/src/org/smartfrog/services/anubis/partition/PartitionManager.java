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
package org.smartfrog.services.anubis.partition;


import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.smartfrog.services.anubis.Anubis;
import org.smartfrog.services.anubis.locator.util.ActiveTimeQueue;
import org.smartfrog.services.anubis.locator.util.TimeQueueElement;
import org.smartfrog.services.anubis.partition.comms.MessageConnection;
import org.smartfrog.services.anubis.partition.protocols.partitionmanager.PartitionProtocol;
import org.smartfrog.services.anubis.partition.test.node.TestMgr;
import org.smartfrog.services.anubis.partition.util.Config;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.views.BitView;
import org.smartfrog.services.anubis.partition.views.View;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.logging.LogImplAsyncWrapper;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class PartitionManager
        extends CompoundImpl
        implements Compound, Partition {

    static final int   UNDEFINED_LEADER  = -1;

    PartitionProtocol  partitionProtocol = null;
    Identity           me                = null;
    Set                notificationSet   = new HashSet();
    View               notifiedView      = null;
    int                notifiedLeader    = UNDEFINED_LEADER;
    boolean            testable          = false;
    TestMgr            testManager       = null;
    LogSF              log               = new LogImplAsyncWrapper(sfLog());
    ActiveTimeQueue    timer = null;
    boolean            terminated        = false;

    public PartitionManager() throws RemoteException {
        super();
    }

    public void sfDeploy() throws SmartFrogException, RemoteException  {
        try {
            super.sfDeploy();
            timer             = new ActiveTimeQueue();
            me                = Config.getIdentity(this, "identity");
            partitionProtocol = (PartitionProtocol)sfResolve("partitionProtocol");
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }

    public void sfStart() throws SmartFrogException, RemoteException  {
        try {
            super.sfStart();
            timer.start();

            if( log.isInfoEnabled() )
                log.info("Started partition manager at " + me + " " + Anubis.version);
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }

    public void sfTerminateWith(TerminationRecord status) {
        if( log.isInfoEnabled() )
            log.info("Terminating partition manager at " + me);
        timer.terminate();
        terminated = true;
        super.sfTerminateWith(status);
    }

    public synchronized void notify(View view, int leader) {

        if( view.isStable() &&
            leader == me.id &&
            notifiedLeader != leader &&
            notifiedLeader != UNDEFINED_LEADER &&
            log.isErrorEnabled() )
            log.error("Leader changed to me on stabalization, old leader = " + notifiedLeader +
                      ", new leader = " + leader + ", view = " + view);

        notifiedView = new BitView(view);
        notifiedLeader = leader;
        Iterator iter = ((Set)((HashSet)notificationSet).clone()).iterator();
        while( iter.hasNext() )
            safePartitionNotification((PartitionNotification)iter.next(),
                                      notifiedView,
                                      notifiedLeader);
    }


    /**
     * This method will invoke user code in the listener. It is timed, logs
     * timeliness errors and catches Throwables.
     *
     * @param listener
     */
    private void safePartitionNotification(PartitionNotification pn, View view, int leader) {
        long         timein  = System.currentTimeMillis();
        long         timeout = 0;
        class TimeoutErrorLogger extends TimeQueueElement {
            View view;
            int  leader;
            TimeoutErrorLogger(View v, int l) {
                view   = v;
                leader = l;
            }
            public void expired() {
                if( log.isErrorEnabled() )
                    log.error("User API Upcall took >200ms in " +
                              "partitionNotification(view, leader) where view=" +
                               view + ", leader=" + leader);
            }
        }
        TimeoutErrorLogger timeoutErrorLogger = new TimeoutErrorLogger(view, leader);


        timer.add(timeoutErrorLogger, (timein+200) );
        try {
            pn.partitionNotification(view, leader);
        } catch (Throwable ex) {
            if( log.isFatalEnabled() )
                log.fatal("User API Upcall threw Throwable in " +
                              "partitionNotification(view, leader) where view=" +
                               view + ", leader=" + leader, ex);
        }
        timeout = System.currentTimeMillis();
        timer.remove(timeoutErrorLogger);
        if( log.isTraceEnabled() )
            log.trace("User API Upcall took " + (timeout - timein) +
                      "ms in partitionNotification(view, leader) where view=" +
                      view + ", leader=" + leader);
    }




    public synchronized void receiveObject(Object obj, int sender, long time) {
        if( terminated )
            return;
        Iterator iter = ((Set)((HashSet)notificationSet).clone()).iterator();
        while( iter.hasNext() )
            safeObjectNotification((PartitionNotification)iter.next(), obj, sender, time);
    }

    /**
     * This method will invoke user code in the listener. It is timed, logs
     * timeliness errors and catches Throwables.
     *
     * @param listener
     */
    private void safeObjectNotification(PartitionNotification pn, Object obj, int sender, long time) {
        long         timein  = System.currentTimeMillis();
        long         timeout = 0;
        class TimeoutErrorLogger extends TimeQueueElement {
            Object obj;
            int    sender;
            long   time;
            TimeoutErrorLogger(Object o, int s, long t) {
                obj    = o;
                sender = s;
                time   = t;
            }
            public void expired() {
                if( log.isErrorEnabled() )
                    log.error("User API Upcall took >200ms in " +
                              "objectNotification(obj, sender, time) where obj=" +
                              obj + ", sender=" + sender + ", time=" +time );
            }
        }
        TimeoutErrorLogger timeoutErrorLogger = new TimeoutErrorLogger(obj, sender, time);


        timer.add(timeoutErrorLogger, (timein+200) );
        try {
            pn.objectNotification(obj, sender, time);
        } catch (Throwable ex) {
            if( log.isFatalEnabled() )
                log.fatal("User API Upcall threw Throwable in " +
                          "objectNotification(obj, sender, time) where obj=" +
                          obj + ", sender=" + sender + ", time=" +time, ex);
        }
        timeout = System.currentTimeMillis();
        timer.remove(timeoutErrorLogger);
        if( log.isTraceEnabled() )
            log.trace("User API Upcall took " + (timeout - timein) +
                      "ms in objectNotification(obj, sender, time) where obj=" +
                      obj + ", sender=" + sender + ", time=" +time);
    }



    public synchronized Status getStatus() {
        return new Status(notifiedView, notifiedLeader);
    }

    public synchronized void register(PartitionNotification pn) {
        notificationSet.add(pn);
    }

    public synchronized void deregister(PartitionNotification pn) {
        notificationSet.remove(pn);
    }

    public MessageConnection connect(int node) {
        return partitionProtocol.connect(node);
    }

    public InetAddress getNodeAddress(int node) {
        return partitionProtocol.getNodeAddress(node);
    }

    public int getId() {
        return me.id;
    }

}
