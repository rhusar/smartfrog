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
package org.smartfrog.services.hadoop.components.tracker;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.cluster.HadoopComponentImpl;
import org.apache.hadoop.mapred.ExtJobTracker;

import java.rmi.RemoteException;
import java.io.IOException;

/**
 * Created 19-May-2008 13:55:33
 */

public class JobTrackerImpl extends HadoopComponentImpl implements HadoopCluster {

    ExtJobTracker tracker;

    public JobTrackerImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            tracker = new ExtJobTracker(createConfiguration());
            tracker.offerService();
        } catch (IOException e) {
            throw new SmartFrogLifecycleException("When creating the job tracker " + e.getMessage(), e, this);
        } catch (InterruptedException e) {
            throw new SmartFrogLifecycleException("When creating the job tracker " + e.getMessage(), e, this);
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (tracker != null) {
            tracker.terminate();
        }
    }
}
