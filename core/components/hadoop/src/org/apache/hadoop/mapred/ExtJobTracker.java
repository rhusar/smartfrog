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
import org.apache.hadoop.fs.FileSystem;
import org.smartfrog.services.hadoop.core.ServiceInfo;
import org.smartfrog.services.hadoop.core.ServiceStateChangeHandler;
import org.smartfrog.services.hadoop.core.ServiceStateChangeNotifier;

import java.io.IOException;

/**
 *
 * Created 19-May-2008 12:01:47
 *
 */

public class ExtJobTracker extends JobTracker implements ServiceInfo  {

    private static final Log LOG= LogFactory.getLog(ExtJobTracker.class);
    private ServiceStateChangeNotifier notifier;

    public ExtJobTracker(JobConf conf) throws IOException, InterruptedException {
        this(null, conf);
    }

    public ExtJobTracker(ServiceStateChangeHandler owner, JobConf conf) throws IOException, InterruptedException {
        super(conf);
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
        LOG.info("State change: JobTracker is now " + newState);
        notifier.onStateChange(oldState, newState);
    }

    /**
     * This service terminates itself by stopping the {@link JobEndNotifier}
     * and then closing down the job tracker
     *
     * @throws IOException exceptions which will be logged
     */
    @Override
    protected void innerClose() throws IOException {
        super.innerClose();
        //also: shut down the filesystem
        closeTheFilesystemQuietly();
    }

    /**
     * Get the port used for IPC communications
     * @return the port number; not valid if the service is not LIVE
     */
    //@Override
    public int getIPCPort() {
        return getTrackerPort();
    }

    /**
     * Get the port used for HTTP communications
     * @return the port number; not valid if the service is not LIVE
     */
    //@Override
    public int getWebPort() {
        return getInfoPort();
    }

    /**
     * Get the current number of workers
     *
     * @return the worker count
     */
    //@Override
    public int getLiveWorkerCount() {
        return getNumResolvedTaskTrackers();
    }

    /**
     * Get the filesystem. Will be null when the service is not live
     * @return the filesystem or null
     */
    public FileSystem getFileSystem() {
        return fs;
    }
}
