/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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


package org.apache.hadoop.hdfs.server.namenode;

import org.smartfrog.services.hadoop.components.cluster.FileSystemNode;
import org.smartfrog.services.hadoop.components.cluster.ManagerNode;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;

/**
 *
 */
public class ExtNameNode extends NameNode {

    private boolean checkRunning;
    private Prim owner;
    private ManagedConfiguration conf;
    public static final String NAME_NODE_IS_STOPPED = "NameNode is stopped";
    public static final String NO_FILESYSTEM = "Filesystem is not running";
    private boolean expectNodeTermination;
    private boolean terminationInitiated;
    private final Reference completeName;
    private static final String NAME_NODE_HAS_HALTED = "Name node has halted";
    private int minWorkerCount;

    /**
     * Create a new name node and deploy it.
     *
     * @param owner owner
     * @param conf  configuration
     * @return the new name node
     * @throws IOException if the node would not deploy
     */
    public static ExtNameNode createAndDeploy(Prim owner, ManagedConfiguration conf)
            throws IOException {
        ExtNameNode nameNode = new ExtNameNode(owner, conf);
        deploy(nameNode);
        return nameNode;
    }

    /**
     * create a new name node, but do not start it
     *
     * @param owner owner
     * @param conf  configuration
     * @return the new name node
     * @throws IOException if the node would not deploy
     */
    public static ExtNameNode create(Prim owner,
                                     ManagedConfiguration conf)
            throws IOException {
        ExtNameNode enn = new ExtNameNode(owner, conf);
        return enn;
    }

    /**
     * Start NameNode. This starts all the worker threads
     *
     * @param owner owner
     * @param conf  configuration
     * @throws IOException io problems
     */
    private ExtNameNode(Prim owner, ManagedConfiguration conf) throws IOException {
        super(conf);
        this.conf = conf;
        this.owner = owner;
        //any other config here.
        checkRunning = conf.getBoolean(FileSystemNode.ATTR_CHECK_RUNNING, true);
        expectNodeTermination = conf.getBoolean(FileSystemNode.ATTR_EXPECT_NODE_TERMINATION, true);
        completeName = owner.sfCompleteName();
        minWorkerCount = conf.getInt(ManagerNode.ATTR_MIN_WORKER_COUNT,0);
    }

    /**
     * Get the stopped exception
     *
     * @return true if we have stopped
     */
    public synchronized boolean isStopped() {
        return getServiceState() == ServiceState.TERMINATED;
    }

    /**
     * If the name node is terminated, we optionally terminate the owning component
     * @param oldState old service state
     * @param newState new service state
     */
/*
    @Override
    protected void onStateChange(ServiceState oldState, ServiceState newState) {
        super.onStateChange(oldState, newState);
        if (newState == ServiceState.TERMINATED) {
            TerminationRecord tr;
            if (expectNodeTermination) {
                tr = TerminationRecord.normal(NAME_NODE_HAS_HALTED, completeName);
            } else {
                tr = TerminationRecord.abnormal(NAME_NODE_HAS_HALTED, completeName);
            }
            ComponentHelper helper = new ComponentHelper(owner);
            helper.targetForWorkflowTermination(
                    tr);
        }
    }*/

    /**
     * Ping the node
     *
     * @throws IOException if the node is unhappy @param status
     */
    @Override
    public synchronized void innerPing(ServiceStatus status)
            throws IOException {
        if (checkRunning) {
            int workers = getLiveWorkerCount();
            if(workers < minWorkerCount ){
              throw new LivenessException("The number of worker nodes is only "
                      + workers
                      +"\n - less than the minimum of " + minWorkerCount);
            };
        }
    }

    /**
     * Get the current number of datanodes
     * @return the datanode count
     */
    public int getLiveWorkerCount() {
        return namesystem.heartbeats.size();
    }


    /**
     * Override point - aethod called whenever there is a state change.
     *
     * The base class logs the event.
     *
     * @param oldState existing state
     * @param newState new state.
     */
    @Override
    protected void onStateChange(ServiceState oldState, ServiceState newState) {
        super.onStateChange(oldState, newState);
        LOG.info("State change: NameNode is now "+ newState);
    }
}
