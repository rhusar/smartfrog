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
 * Task tracker with some lifecycle support
 */

public class ExtTaskTracker extends TaskTracker implements HadoopComponentLifecycle {

    private static final Log log = LogFactory.getLog(ExtTaskTracker.class);

    public ExtTaskTracker(ManagedConfiguration conf) throws IOException {
        super(conf);
    }

    /**
     * Initialize; read in and validate values.
     *
     * @throws IOException for any initialisation failure
     */
    public void init() throws IOException {
        //no-op
    }

    /**
     * Start any work (in separate threads)
     *
     * @throws IOException for any initialisation failure
     */
    public void start() throws IOException {
        //no-op
    }

    /**
     * Ping: only valid when started.
     *
     * @throws IOException for any ping failure
     */
    public void ping() throws IOException {
        //no-op
    }

    /**
     * Shut down. This must be idempotent and turn errors into log/warn events -do your best to clean up even in the
     * face of adversity.
     */
    public void terminate() {
        try {
            shutdown();
        } catch (IOException e) {
            log.warn("When terminating " + e.getMessage(), e);
        }
    }

    /**
     * Get the current state
     *
     * @return the lifecycle state
     */
    public HadoopComponentLifecycle.State getLifecycleState() {
        if (shuttingDown) {
            return HadoopComponentLifecycle.State.TERMINATED;
        } else {
            return HadoopComponentLifecycle.State.STARTED;
        }
    }


}
