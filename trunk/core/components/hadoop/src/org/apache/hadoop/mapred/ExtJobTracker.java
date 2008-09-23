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

import java.io.IOException;

/**
 *
 * Created 19-May-2008 12:01:47
 *
 */

public class ExtJobTracker extends JobTracker {

    private static final Log LOG= LogFactory.getLog(ExtJobTracker.class);

    public ExtJobTracker(JobConf conf) throws IOException, InterruptedException {
        super(conf);

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
    }

  /**
   * This service terminates itself by stopping the {@link JobEndNotifier} and
   * then closing down the job tracker
   *
   * @throws IOException exceptions which will be logged
   */
  @Override
  protected void innerTerminate() throws IOException {
    super.innerTerminate();
    //also: shut down the filesystem
    closeTheFilesystemQuietly();
  }
}
