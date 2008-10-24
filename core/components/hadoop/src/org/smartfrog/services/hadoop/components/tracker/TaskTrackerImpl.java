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

import org.apache.hadoop.mapred.ExtTaskTracker;
import org.apache.hadoop.util.Service;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.cluster.HadoopServiceImpl;
import org.smartfrog.services.hadoop.components.cluster.PortEntry;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 23-May-2008 14:20:41
 */

public class TaskTrackerImpl extends HadoopServiceImpl implements HadoopCluster {

    private static final String NAME = "TaskTracker";

    public TaskTrackerImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     *
     * @return the name of the Hadoop service deployed
     */
    @Override
    protected String getName() {
        return NAME;
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
        createAndDeployService();
    }

    /** {@inheritDoc} */
    protected Service createTheService(ManagedConfiguration configuration) throws IOException, SmartFrogException {
        ExtTaskTracker tracker = new ExtTaskTracker(this, configuration);
        return tracker;
    }

    /**
     * Get a list of ports that should be closed on startup and after termination. This list is built up on startup and
     * cached.
     *
     * @param conf the configuration to use
     * @return null or a list of ports
     */
    @Override
    protected List<PortEntry> buildPortList(ManagedConfiguration conf)
            throws SmartFrogResolutionException, RemoteException {
        List<PortEntry> ports = new ArrayList<PortEntry>();
        ports.add(resolvePortEntry(conf, ConfigurationAttributes.MAPRED_TASK_TRACKER_HTTP_ADDRESS));
        return ports;
    }


}
