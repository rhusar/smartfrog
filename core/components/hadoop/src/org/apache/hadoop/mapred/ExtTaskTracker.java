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
package org.apache.hadoop.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.NodeUtils;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.core.BindingTuple;
import org.smartfrog.services.hadoop.core.ServiceInfo;
import org.smartfrog.services.hadoop.core.ServiceStateChangeHandler;
import org.smartfrog.services.hadoop.core.ServiceStateChangeNotifier;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Task tracker with some lifecycle support and state information
 */

public class ExtTaskTracker extends TaskTracker implements ServiceInfo, ConfigurationAttributes {

    private static final Log LOG = LogFactory.getLog(ExtTaskTracker.class);
    private ServiceStateChangeNotifier notifier;

    public ExtTaskTracker(JobConf conf) throws IOException {
        this(null, conf);
    }

    public ExtTaskTracker(ServiceStateChangeHandler owner, JobConf conf) throws IOException {
        super(conf, false);
        notifier = new ServiceStateChangeNotifier(this, owner);
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
        String message = "State change: TaskTracker is now " + newState;
        if (jobTrackAddr != null) {
            message += " Job Tracker: " + jobTrackAddr;
        }
        LOG.info(message);
        notifier.onStateChange(oldState, newState);
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
    public ServiceStatus ping() throws IOException {
        return super.ping();
    }

    /**
     * Get the port used for IPC communications
     *
     * @return the port number; not valid if the service is not LIVE
     */
    @Override
    public int getIPCPort() {
        return ServiceInfo.PORT_UNUSED;
    }

    /**
     * Get the port used for HTTP communications
     *
     * @return the port number; not valid if the service is not LIVE
     */
    @Override
    public int getWebPort() {
        return httpPort;
    }

    /**
     * Get the current number of workers
     *
     * @return the worker count
     */

    @Override
    public int getLiveWorkerCount() {
        return workerThreads;
    }

    /**
     * {@inheritDoc}
     *
     * @return the binding information
     */
    @Override
    public List<BindingTuple> getBindingInformation() {
        List<BindingTuple> bindings = new ArrayList<BindingTuple>();
        //try and work out the underlying bindings by going back to the configuration
        InetSocketAddress httpAddr = NodeUtils.resolveAddress(getConf(), MAPRED_TASK_TRACKER_HTTP_ADDRESS);
        InetSocketAddress realHttpAddr = new InetSocketAddress(httpAddr.getAddress(), getWebPort());
        bindings.add(NodeUtils.toBindingTuple(MAPRED_TASK_TRACKER_HTTP_ADDRESS, "http", realHttpAddr));

        bindings.add(NodeUtils.toBindingTuple(MAPRED_TASK_TRACKER_REPORT_ADDRESS, "http",
                getTaskTrackerReportAddress()));
        return bindings;
    }

    /**
     * {@inheritDoc}
     * @return the exit state
     * @throws Exception if something went wrong
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    public State offerService() throws Exception {
        LOG.info("Task Tracker Service is being offered: " + toString());
        return super.offerService();
    }

    /**
     * {@inheritDoc}
     * @return the string value
     */
    @Override
    public String toString() {
        String address = "" + getTaskTrackerReportAddress();
        return super.toString() + ". web port=" + getWebPort() + " reporting " + address;
    }
}
