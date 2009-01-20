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

import org.apache.hadoop.mapred.ExtJobTracker;
import org.apache.hadoop.mapred.ExtTaskTracker;
import org.apache.hadoop.util.Service;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.cluster.HadoopServiceImpl;
import org.smartfrog.services.hadoop.components.cluster.PortEntry;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.io.IOException;
import java.rmi.RemoteException;
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
    protected String getServiceName() {
        return NAME;
    }


    protected ExtTaskTracker getTaskTracker() {
        return (ExtTaskTracker) getService();
    }

    /**
    * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
    * but spawn off any main loops!
    *
    * @throws SmartFrogException failure while starting
    * @throws RemoteException    In case of network/rmi error
    */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        createAndDeployService();
    }

    /** {@inheritDoc} */
    @Override
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
        List<PortEntry> ports = super.buildPortList(conf);
        ports.add(resolvePortEntry(conf, ConfigurationAttributes.MAPRED_TASK_TRACKER_HTTP_ADDRESS));
        return ports;
    }

    /**
     * after deployment, call {@link ExtJobTracker#offerService()} to start the service.
     * This call will not return until the work is finished
     *
     * @param hadoopService service that has been deployed
     * @throws IOException        IO problems
     * @throws SmartFrogException smartfrog problems
     */
    @Override
    protected void onServiceDeploymentComplete(Service hadoopService) throws IOException, SmartFrogException {
        super.onServiceDeploymentComplete(hadoopService);
        try {
            getTaskTracker().offerService();
        } catch (InterruptedException e) {
            //this is ok, it is time to terminate
            sfLog().ignore("Task tracker was interrupted", e);
        } catch (IOException e) {
            throw e;
        } catch (SmartFrogException e) {
            throw e;
        } catch(Exception e) {
            throw new SmartFrogRuntimeException(e.toString(), e, this);
        }
    }


}
