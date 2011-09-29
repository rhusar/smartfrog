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
package org.smartfrog.services.anubis.partition.protocols.partitionmanager;


import java.net.InetAddress;
import java.rmi.RemoteException;

import org.smartfrog.services.anubis.partition.PartitionManager;
import org.smartfrog.services.anubis.partition.comms.MessageConnection;
import org.smartfrog.services.anubis.partition.util.Config;
import org.smartfrog.services.anubis.partition.util.Identity;
import org.smartfrog.services.anubis.partition.views.BitView;
import org.smartfrog.services.anubis.partition.views.View;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class PartitionProtocol
        extends CompoundImpl
        implements Compound {

    private PartitionManager partitionMgr  = null;
    private ConnectionSet    connectionSet = null;
    private Identity         me            = null;
    private BitView          view          = new BitView();
    private Identity         leader        = null;
    private boolean          changed       = false;
    private boolean          terminated    = false;


    public PartitionProtocol() throws Exception {
        super();
    }

    public void sfDeploy() throws SmartFrogException, RemoteException  {
        super.sfDeploy();
        try {
            partitionMgr = (PartitionManager) sfResolve("partitionManager");
            connectionSet = (ConnectionSet) sfResolve("connectionSet");
            me = Config.getIdentity(this, "identity");
            leader = me;
        }
        catch (Exception ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    public void sfStart() throws SmartFrogException, RemoteException  {
        super.sfStart();
        view.add(me);
        view.stablize();
        view.setTimeStamp( me.epoch );
        partitionMgr.notify(view, leader.id);
    }

    public void sfTerminateWith(TerminationRecord status) {
        terminated = true;
        super.sfTerminateWith(status);
   }


    /**
     * Changed view is called during regular connection set checks if the
     * connection set has changed.
     *
     * removeCompletement() will remove nodes from the partition if
     * they are not in the connection set. If this happens then we
     * note that there have been changes and elect a new leader. The result
     * is that the partition can only contract when it is unstable (i.e. when
     * there is a view change).
     */
    public void changedView() {
        if( view.removeComplement(connectionSet.getView()) || view.isStable() ) {
            changed = true;
        }
        view.setTimeStamp( View.undefinedTimeStamp );
        view.destablize();
        leader = connectionSet.electLeader(view);
    }


    /**
     * remove a node from the partition - if it is there. If it was there
     * then destablise and elect a new leader.
     * @param id
     */
    public void remove(Identity id) {
        if( view.remove(id.id) ) {
            changed = true;
            view.setTimeStamp( View.undefinedTimeStamp );
            view.destablize();
            leader = connectionSet.electLeader(view);
        }
    }


    /**
     * copy the stable view from the connection set - includes the
     * time stamp in the copy. Note that a partition can expand
     * when it is stable (the connectionSet may be bigger than the
     * partition).
     *
     * Note that the node that wins the leader election at stability
     * believed it was leader prior to stability.
     */
    public void stableView() {
        changed = true;
        view.copyView(connectionSet.getView());
        leader = connectionSet.electLeader(view);
    }


    /**
     * Issue notifications from the partition manager.
     */
    public void notifyChanges() {
        if( changed ) {
            partitionMgr.notify(view, leader.id);
            changed = false;
        }
    }


    /**
     * Establish a connection to the remote node
     * @param id - id of remote node
     * @return - the message connection
     */
    public MessageConnection connect(int id) {
        return connectionSet.connect(id);
    }


    public InetAddress getNodeAddress(int id) {
        return connectionSet.getNodeAddress(id);
    }


    public void receiveObject(Object obj, Identity id, long time) {
        if( terminated )
            return;
        partitionMgr.receiveObject(obj, id.id, time);
    }




}
