/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.hadoop;

import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.MasterWorkerAllocationHandler;
import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.hadoop.descriptions.TemplateNames;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequest;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;


/**
 * Created 18-Nov-2009 15:44:39
 */

public class HadoopAllocationHandler extends MasterWorkerAllocationHandler implements TemplateNames {

    private int taskSlots;


    public HadoopAllocationHandler(ClusterController controller) {
        super(controller);
        setDeploymentRequired(true);
    }


    public int getTaskSlots() {
        return taskSlots;
    }

    public void setTaskSlots(int taskSlots) {
        this.taskSlots = taskSlots;
    }


    /**
    * Perform any system property setup ready to load the SF application
    */
    protected void bindSystemProperties() {
        super.bindSystemProperties();
        System.setProperty(BINDING_TASKTRACKER_SLOTS, "" + getTaskSlots());
    }

    /**
     * {@inheritDoc}
     * @return the used for a master.
     * @see TemplateNames
     */
    @Override
    protected String getMasterResourceName() {
        return TemplateNames.HADOOP_MASTER_WORKER_SF;
    }

    /**
     * {@inheritDoc}
     * @return the resource for a worker
     * @see TemplateNames
     */
    @Override
    protected String getWorkerResourceName() {
        return TemplateNames.HADOOP_WORKER_SF;
    }

    @Override
    protected void masterAllocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException {
        //this is the master instance
        bindMaster(newhosts);
        HostInstance newMaster = newhosts.getMaster();
        
        //now we need to get the config and set up the controller
        //loadSFApp()
        
        
        super.masterAllocationRequestSucceeded(request, newhosts);
    }
}
