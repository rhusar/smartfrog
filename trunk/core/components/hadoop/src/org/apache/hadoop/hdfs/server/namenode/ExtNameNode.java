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
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.core.ServiceInfo;
import org.smartfrog.services.hadoop.core.ServiceStateChangeNotifier;
import org.smartfrog.services.hadoop.core.BindingTuple;
import org.smartfrog.sfcore.prim.Prim;
import org.apache.hadoop.util.NodeUtils;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class ExtNameNode extends NameNode implements ServiceInfo, ConfigurationAttributes {

    private boolean checkRunning;
    private Prim owner;
    private ManagedConfiguration conf;
    public static final String NAME_NODE_IS_STOPPED = "NameNode is stopped";
    public static final String NO_FILESYSTEM = "Filesystem is not running";
    private int minWorkerCount;
    private ServiceStateChangeNotifier notifier;

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
        return new ExtNameNode(owner, conf);
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
        minWorkerCount = conf.getInt(ManagerNode.ATTR_MIN_WORKER_COUNT,0);
        notifier = new ServiceStateChangeNotifier(this, owner);
    }

    /**
     * Return an extended service name
     *
     * @return new service name
     */
    @Override
    public String getServiceName() {
        return "ExtNameNode";
    }


    /**
     * Get the stopped exception
     *
     * @return true if we have stopped
     */
    public boolean isStopped() {
        return getServiceState() == ServiceState.CLOSED;
    }

    /**
     * Ping the node
     *
     * @throws IOException if the node is unhappy @param status
     */
    @Override
    public synchronized void innerPing(ServiceStatus status)
            throws IOException {
        super.innerPing(status);
        if (checkRunning) {
            int workers = getLiveWorkerCount();
            if(workers < minWorkerCount ){
                status.addThrowable(new LivenessException("The number of worker nodes is only "
                      + workers
                      +"\n - less than the minimum of " + minWorkerCount));
            }
        }
    }

    /**
     * Get the current number of workers
     * @return the worker count
     */
    //@Override
    public int getLiveWorkerCount() {
        return getNamesystem().heartbeats.size();
    }

    /**
     * {@inheritDoc}
     *
     * @return the binding information
     */
    //@Override
    public List<BindingTuple> getBindingInformation() {
        List<BindingTuple> bindings = new ArrayList<BindingTuple>();
        bindings.add(NodeUtils.toBindingTuple(FS_DEFAULT_NAME, "hdfs", getNameNodeAddress()));
        bindings.add(NodeUtils.toBindingTuple(DFS_HTTP_ADDRESS, "http", getHttpAddress()));
        return bindings;
    }

    /**
     * Override point - method called whenever there is a state change.
     *
     * The base class logs the event.
     *
     * @param oldState existing state
     * @param newState new state.
     */
    //@Override
    protected void onStateChange(ServiceState oldState, ServiceState newState) {
        super.onStateChange(oldState, newState);
        LOG.info("State change: NameNode is now "+ newState);
        //when we go live, we also push out our new URL

        //tell anyone listening
        notifier.onStateChange(oldState, newState);
    }

    /**
     * Get the port used for IPC communications
     *
     * @return the port number; not valid if the service is not LIVE
     */
    //@Override
    public int getIPCPort() {
        return getNameNodeAddress().getPort() ;
    }

    /**
     * Get the port used for HTTP communications
     *
     * @return the port number; not valid if the service is not LIVE
     */
    //@Override
    public int getWebPort() {
        return getHttpAddress().getPort();
    }

    public void setNotifier(ServiceStateChangeNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
