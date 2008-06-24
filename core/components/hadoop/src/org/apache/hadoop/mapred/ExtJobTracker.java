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
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.apache.hadoop.util.HadoopComponentLifecycle;

import java.io.IOException;

/**
 *
 * Created 19-May-2008 12:01:47
 *
 */

public class ExtJobTracker extends JobTracker implements HadoopComponentLifecycle {

    private static final Log log= LogFactory.getLog(ExtJobTracker.class);

    private HadoopComponentLifecycle.State lifecycleState = HadoopComponentLifecycle.State.CREATED;

    public ExtJobTracker(ManagedConfiguration conf) throws IOException, InterruptedException {
        super(conf);

        lifecycleState = HadoopComponentLifecycle.State.STARTED;
    }

    /**
     * Initialize; read in and validate values.
     *
     * @throws IOException for any initialisation failure
     */
    public void init() throws IOException {

    }

    /**
     * Start any work (in separate threads)
     *
     * @throws IOException for any initialisation failure
     */
    public void start() throws IOException {

    }

    /**
     * Ping: only valid when started.
     *
     * @throws IOException for any ping failure
     */
    public void ping() throws IOException {

    }

    /**
     * Shut down. This must be idempotent and turn errors into log/warn events -do your best to clean up even in the
     * face of adversity.
     */
    public void terminate() {
        try {
            stopTracker();
        } catch (IOException e) {
            log.info("when terminating the tracker",e);
        }
    }

    public synchronized void stopTracker() throws IOException {
        super.stopTracker();
        JobEndNotifier.stopNotifier();
    }

    /**
     * Get the current state
     *
     * @return the lifecycle state
     */
    public HadoopComponentLifecycle.State getLifecycleState() {
        return lifecycleState;
    }
}
