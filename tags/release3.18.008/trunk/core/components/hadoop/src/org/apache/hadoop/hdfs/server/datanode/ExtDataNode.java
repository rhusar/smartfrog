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


package org.apache.hadoop.hdfs.server.datanode;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.util.NodeUtils;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.core.BindingTuple;
import org.smartfrog.services.hadoop.core.ServiceInfo;
import org.smartfrog.services.hadoop.core.ServiceStateChangeNotifier;
import org.smartfrog.services.hadoop.core.PingHelper;
import org.smartfrog.services.hadoop.core.InnerPing;
import org.smartfrog.services.hadoop.core.ServicePingStatus;
import org.smartfrog.services.hadoop.core.LivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is in the Apache hadoop packages to get at package scoped operations and internal datastructures that are only
 * visible in package scope. <p/> To use these classes in a secure classloader, both the hadoop-core and sf-hadoop JARs
 * will need to be signed by the same entities.
 */
public class ExtDataNode extends DataNode implements ServiceInfo, ConfigurationAttributes, InnerPing {

    private volatile boolean stopped;
    private ExtDataNodeThread worker;
    private Prim owner;
    private ServiceStateChangeNotifier notifier;
    ManagedConfiguration conf;
    private final PingHelper pingHelper = new PingHelper(this);
    
    public ExtDataNode(Prim owner, ManagedConfiguration conf, AbstractList<File> dataDirs)
            throws IOException {
        super(conf, dataDirs);
        this.conf = conf;
        this.owner = owner;
        notifier = new ServiceStateChangeNotifier(this, owner);
    }

  /**
   * Return an extended service name
   * @return new service name
   */
  @SuppressWarnings({"RefusedBequest"})
  @Override
  public String getServiceName() {
    return "ExtDataNode";
  }

    /**
     * Start our parent and the worker thread
     *
     * @throws IOException if necessary
     * @throws InterruptedException if the thread was interrupted on startup
     */
    @Override
    public void serviceStart() throws IOException, InterruptedException {
      try {
          super.serviceStart();
      } catch (BindException e) {
          InetSocketAddress http = getHttpAddress(getConf());
          InetSocketAddress https = getHttpsAddress(getConf(), http.getHostName());
          InetSocketAddress ipc = getIpcAddress(getConf());

          throw (BindException) new BindException("Failed to bind listening ports : " + e
                  + " HTTP=" + http
                  + " HTTPS=" + https
                  + " IPC=" + ipc).initCause(e);
      }
      register();
      startWorkerThread();
    }

    public InetSocketAddress getHttpsAddress(Configuration conf, String hostname) {

        return NetUtils.createSocketAddr(conf.get(
                DFS_DATANODE_HTTPS_ADDRESS,
                hostname + ":" + 0));
    }

    public InetSocketAddress getHttpAddress(Configuration conf) {
        return NodeUtils.resolveAddress(conf, DFS_DATANODE_INFO_BIND_ADDRESS);
    }

    public InetSocketAddress getIpcAddress(Configuration conf) {
        return NodeUtils.resolveAddress(conf, DFS_DATANODE_BIND_ADDRESS);
    }

    /**
     * Shut down this instance of the datanode. Returns only after shutdown is complete.
     */
    @Override
    public synchronized void serviceClose() throws IOException {
        LOG.info("Closing ExtDataNode");
        super.serviceClose();
    }


  /**
     * Set our stopped flag
     */
    private synchronized void stopped() {
        stopped = true;
    }

    /**
     * Get the stopped exception
     *
     * @return true if we have stopped
     */
    public synchronized boolean isStopped() {
        return stopped;
    }

    /**
     * Get the port used for IPC communications
     *
     * @return the port number; not valid if the service is not LIVE
     */
    public int getIPCPort() {
        return getSelfAddr().getPort();
    }

    /**
     * Get the port used for HTTP communications
     *
     * @return the port number; not valid if the service is not LIVE
     */
    public int getWebPort() {
        //return this.infoServer.getPort();
        return ServiceInfo.PORT_UNDEFINED;
    }


    /**
     * {@inheritDoc}
     *
     * @return the binding information
     */
    public List<BindingTuple> getBindingInformation() {
        List<BindingTuple> bindings=new ArrayList<BindingTuple>();
        InetSocketAddress dfsAddress = getSelfAddr();
        bindings.add(NodeUtils.toBindingTuple(DFS_DATANODE_ADDRESS, "http", dfsAddress));
        bindings.add(new BindingTuple(DFS_DATANODE_HTTP_ADDRESS, 
                NodeUtils.toURL("http", dnRegistration.getHost(), dnRegistration.getInfoPort() )));
        return bindings;
    }

    /**
     * Get the current number of workers
     *
     * @return the worker count
     */

    public int getLiveWorkerCount() {
        return 0;
    }

    /**
     * Override point - method called whenever there is a state change.
     *
     * The base class logs the event.
     *
     * @param oldState existing state
     * @param newState new state.
     */
    @Override
    protected void onStateChange(ServiceState oldState, ServiceState newState) {
        super.onStateChange(oldState, newState);
        String message = "State change: DataNode is now " + newState;
        LOG.info(message);
        notifier.onStateChange(oldState,newState);
    }

    /**
     * Override the normal run and note that we got stopped
     */
    @Override
    public void run() {
        try {
            super.run();
        } finally {
            stopped();
        }
    }


    /**
     * Ping: checks that a component considers itself live.
     *
     * This method makes the ping public
     *
     * @return the current service state.
     * @throws IOException for any ping failure
     */
    @Override
    public ServicePingStatus ping() throws IOException {
        return pingHelper.ping();
    }

    /**
     * {@inheritDoc}
     *
     * This implementation checks for the IPC server running and the DataNode being registered to a namenode.
     *
     * @param status the initial status
     * @throws IOException       for any ping failure
     * @throws LivenessException if the IPC server is not defined
     */
    @Override
    public void innerPing(ServicePingStatus status) throws IOException {
        if (ipcServer == null) {
            status.addThrowable(new LivenessException("No IPC Server running"));
        }
        if (dnRegistration == null) {
            status.addThrowable(
                    new LivenessException("Not registered to a namenode"));
        }
    }    

    /**
     * Start the worker thread
     */
    private synchronized void startWorkerThread() {
        if (worker == null) {
            worker = new ExtDataNodeThread();
            worker.start();
        }
    }


  /**
     * This is a private worker thread that can be interrupted better
     */
    private class ExtDataNodeThread extends WorkflowThread {

        /**
         * Creates a new thread
         */
        private ExtDataNodeThread() {
            super(ExtDataNode.this.owner, true);
            setName(getServiceName());
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run object, then that <code>Runnable</code>
         * object's <code>run</code> method is called; otherwise, this method does nothing and returns. <p> Subclasses
         * of <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        @Override
        public void execute() throws Throwable {
          try {
            ExtDataNode.this.run();
          } catch (Throwable e) {
              LOG.error("error while in state " + getState() + ": " + e, e);
              throw e;
          }
        }

        /**
         * Add an interrupt to the thread termination
         */
        @Override
        public synchronized void requestTermination() {
            if (!isTerminationRequested()) {
                LOG.info("Terminating the ExtDataNodeThread");
                super.requestTermination();
                //and interrupt
                interrupt();
            }
        }
    }

}
