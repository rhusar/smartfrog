/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.clusters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cloudfarmer.api.LocalSmartFrogDescriptor;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterAllocationCompleted;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.DynamicSmartFrogClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequestList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequest;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * Handler for cluster allocations
 */

public class ClusterAllocationHandler implements ClusterAllocationCompleted {
    protected static final Log LOG = LogFactory.getLog(ClusterAllocationHandler.class);
    protected ClusterController controller;
    protected String status;
    protected Throwable thrown;

    protected boolean deploymentRequired = false;

    public ClusterAllocationHandler(
            ClusterController controller) {
        this.controller = controller;
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

    public boolean isDeploymentRequired() {
        return deploymentRequired;
    }

    public void setDeploymentRequired(boolean deploymentRequired) {
        this.deploymentRequired = deploymentRequired;
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
        LOG.error(status, thrown);
    }

    /**
     * Deploy an application to a specific host
     * @param instance host instance
     * @param name application name
     * @param descriptor SF descriptor
     * @return true if something was deployed, false if there was no deployment service up and running 
     * and it was not required
     * @throws SmartFrogException deployment problems
     * @throws IOException        IO problems
     */
    protected boolean deployApplication(HostInstance instance, String name, LocalSmartFrogDescriptor descriptor)
            throws SmartFrogException, IOException {
        try {
            if(!isDeploymentSupported()) {
                if (deploymentRequired) {
                    checkDeploymentSupported();
                } 
                return false;
            }
            NodeDeploymentService deploymentService = createNodeDeploymentService(instance);
            deploymentService.deployApplication(name, descriptor.getComponentDescription());
            return true;
        } catch (Throwable e) {
            logAndRethrow(e);
            return false;
        }
    }


    /**
     * Catch, log and rethrow any exception
     * @param throwable what was caught
     * @throws IOException IO exception
     * @throws SmartFrogException the caught SF exception, or a wrapped exception of any other type
     */
    protected void logAndRethrow(Throwable throwable) throws IOException, SmartFrogException {
        LOG.error(throwable);
        if (throwable instanceof SmartFrogException) {
            throw (SmartFrogException) throwable;
        }
        if (throwable instanceof IOException) {
            throw (IOException) throwable;
        }
        throw new SmartFrogException(throwable);
    }


    /**
     * Test for deployment being supported
     * @return true iff deployment is supported
     * @throws SmartFrogException something failed
     * @throws IOException IO/RMI trouble
     */
    protected boolean isDeploymentSupported() throws SmartFrogException, IOException {
        if (!(controller instanceof DynamicSmartFrogClusterController)) {
            return false;
        }
        DynamicSmartFrogClusterController clusterController = getSfController();
        return clusterController.isFarmerAvailable() && clusterController.getFarmer().isDeploymentServiceAvailable();
    }

    protected void checkDeploymentSupported() throws SmartFrogException, IOException {
        if (!(controller instanceof DynamicSmartFrogClusterController)) {
            throw new SmartFrogException("Controller does not support deployment");
        }
        DynamicSmartFrogClusterController clusterController = getSfController();
        if (!clusterController.isFarmerAvailable()) {
            throw new SmartFrogException("Farmer is not available");
        }
        ClusterFarmer farmer = clusterController.getFarmer();
        String diagnostics = farmer.getDiagnosticsText();
        if (!farmer.isDeploymentServiceAvailable()) {
            throw new SmartFrogException("No deployment service for " + diagnostics);
        }
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
        try {
            DynamicSmartFrogClusterController sfcontroller = getSfController();
            NodeDeploymentService deploymentService = sfcontroller.createNodeDeploymentService(instance);
            return deploymentService;
        } catch (Throwable e) {
            logAndRethrow(e);
            return null;
        }
    }

    /**
     * Get the smartfrog controller. 
     * @return the controller cast to an SF controller
     * @throws ClassCastException if the controller is not a cloudfarmer implementation
     */
    protected DynamicSmartFrogClusterController getSfController() {
        return (DynamicSmartFrogClusterController) controller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void allocationRequestSucceeded(RoleAllocationRequest request, HostInstanceList newhosts)
            throws IOException, SmartFrogException {
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
        thrown = failureCause;
        LOG.error(status, thrown);
    }

    /**
     * Parse a SmartFrog resource. any failue is passed up
     * @param resource resource to parse
     * @return the parsed resource
     * @throws SmartFrogException if parsing failed
     */
    protected LocalSmartFrogDescriptor parseResource(String resource) throws SmartFrogException {
        //load the CD -so that any binding problem shows up early
        LocalSmartFrogDescriptor localApp = new LocalSmartFrogDescriptor();
        localApp.parseResource(resource);
        localApp.throwParseExceptionIfNeeded();
        return localApp;
    }
}
