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
package org.smartfrog.services.cloudfarmer.client.web.hadoop;

import org.smartfrog.services.cloudfarmer.api.LocalSmartFrogDescriptor;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.client.web.hadoop.descriptions.TemplateNames;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterAllocationCompleted;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.DynamicSmartFrogClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequest;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequestList;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * Created 18-Nov-2009 15:44:39
 */

public class HadoopDeploymentCallback implements ClusterAllocationCompleted, HadoopRoles, TemplateNames {

    private ClusterController controller;
    private boolean creatingMaster;
    private String status;
    private Throwable thrown;
    private HostInstance master;
    private int taskSlots;


    public HadoopDeploymentCallback(ClusterController controller) {
        this.controller = controller;
    }

    public boolean isCreatingMaster() {
        return creatingMaster;
    }

    public void setCreatingMaster(boolean creatingMaster) {
        this.creatingMaster = creatingMaster;
    }

    public HostInstance getMaster() {
        return master;
    }

    public void setMaster(HostInstance master) {
        this.master = master;
    }

    public String getStatus() {
        return status;
    }


    public Throwable getThrown() {
        return thrown;
    }

    public void resetThrown() {
        thrown = null;
    }

    public int getTaskSlots() {
        return taskSlots;
    }

    public void setTaskSlots(int taskSlots) {
        this.taskSlots = taskSlots;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void farmerAvailabilityFailure(boolean timedOut, long timeout, Throwable exception, Object extraData)
            throws IOException, SmartFrogException {
        StringBuilder text = new StringBuilder();
        text.append("Farmer is not available:\n");
        if (timedOut) {
            text.append("connection timed out\n");
        }
        if (exception != null) {
            text.append(exception.toString());
        }
        status = text.toString();
        thrown = exception;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void allocationSucceeded(RoleAllocationRequestList requests, HostInstanceList hosts, Object extraData)
            throws IOException, SmartFrogException {
        //this is where the cluster gets rolled out
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void allocationFailed(RoleAllocationRequestList requests, HostInstanceList hosts, Throwable failureCause,
                                 Object extraData) throws IOException, SmartFrogException {
        StringBuilder text = new StringBuilder();
        text.append("Allocation failed ").append(failureCause);
        status = text.toString();
        ;
    }

    /**
     * This is where roles are installed 
     * {@inheritDoc}
     */
    @Override
    public void allocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException {
        String role = request.getRole();
        if (MASTER.equals(role)) {
            masterAllocationRequestSucceeded(request, newhosts);
        } else if (WORKER.equals(role)) {
            workerAllocationRequestSucceeded(request, newhosts);
        }
    }

    /**
     * Handle a master allocation request by pushing out the master configuration
     *
     * @param request  the request that just succeeded
     * @param newhosts the new hosts
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    private void masterAllocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException {
        master = newhosts.getMaster();
        LocalSmartFrogDescriptor descriptor = loadSFApp(HADOOP_MASTER_SF);
        deployApplication(master, request.getRole(), descriptor);

    }

    /**
     * Handle a master allocation request by pushing out the worker configuration
     *
     * @param request  the request that just succeeded
     * @param newhosts the new hosts
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    private void workerAllocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException {
        //we'd better have a non-null master here
        if (master == null) {
            throw new SmartFrogDeploymentException("Cannot bring up worker nodes without a master node");
        }
        LocalSmartFrogDescriptor descriptor = loadSFApp(HADOOP_MASTER_SF);
        for (HostInstance host:newhosts) {
            deployApplication(host, request.getRole(), descriptor);
        }
    }

    /**
     * Deploy an application to a specific host
     * @param instance host instance
     * @param name application name
     * @param descriptor SF descriptor
     * @throws SmartFrogException deployment problems
     * @throws IOException        IO problems
     */
    private void deployApplication(HostInstance instance, String name, LocalSmartFrogDescriptor descriptor)
            throws SmartFrogException, IOException {
        NodeDeploymentService deploymentService = createNodeDeploymentService(instance);
        deploymentService.deployApplication(name, descriptor.getComponentDescription());
    }

    /**
     * Create a node deployment service
     * @param instance host instance
     * @return a deployment service bound to that instance
     * @throws SmartFrogException problems bringing up an instance
     * @throws IOException IO Problems
     */
    private NodeDeploymentService createNodeDeploymentService(HostInstance instance)
            throws SmartFrogException, IOException {
        DynamicSmartFrogClusterController sfcontroller = (DynamicSmartFrogClusterController) controller;
        NodeDeploymentService deploymentService = sfcontroller.createNodeDeploymentService(instance);
        return deploymentService;
    }

    /**
     * @param resource   resource to load
     * @return a descriptor bonded to the master
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    protected LocalSmartFrogDescriptor loadSFApp(String resource) throws IOException, SmartFrogException {
        ///set the binding values
        System.setProperty(BINDING_MASTER_HOSTNAME, master.getHostname());
        System.setProperty(BINDING_TASKTRACKER_SLOTS, "" + getTaskSlots());

        //load the CD -so that any binding problem shows up early
        LocalSmartFrogDescriptor localApp = new LocalSmartFrogDescriptor();
        localApp.parseResource(resource);
        localApp.throwParseExceptionIfNeeded();
        return localApp;
    }
}
