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
import org.smartfrog.services.hadoop.core.ServiceStateChangeNotifier;
import org.smartfrog.services.hadoop.core.ServiceStateChangeHandler;
import org.smartfrog.services.hadoop.core.ServiceInfo;

import java.io.IOException;

/**
 * Task tracker with some lifecycle support
 */

public class ExtTaskTracker extends TaskTracker implements ServiceInfo {

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
        LOG.info("State change: TaskTracker is now " + newState);
        notifier.onStateChange(oldState, newState);
    }

    /**
     * Get the port used for IPC communications
     *
     * @return the port number; not valid if the service is not LIVE
     */
    public int getIPCPort() {
        return ServiceInfo.PORT_UNUSED;
    }

    /**
     * Get the port used for HTTP communications
     *
     * @return the port number; not valid if the service is not LIVE
     */
    public int getWebPort() {
        return server.getPort();
    }

    /**
     * Get the current number of workers
     *
     * @return the worker count
     */

    public int getLiveWorkerCount() {
        return workerThreads;
    }
}
