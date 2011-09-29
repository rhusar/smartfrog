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
package org.smartfrog.services.cloudfarmer.client.components;

import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.logging.LogRemoteImpl;
import org.smartfrog.sfcore.logging.LogRemote;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a workflow component that is bound to a farmer, and which creates/destroys nodes through its lifecycle.
 *
 * It is primarily for testing, but it can be used in production. A key weakness is that it is synchronous -it relies on
 * the operation to complete rapidly
 */

public class FarmCustomerImpl extends PrimImpl implements FarmCustomer {
    protected ClusterFarmer farmer;
    private ClusterNode[] nodes = new ClusterNode[0];
    protected String role;
    protected int min;
    protected int max;
    private boolean deleteOnTerminate;
    private boolean pingChecksNodes;
    private List<String> expectedHostnames;
    private CustomerThread worker;
    private static final int SHUTDOWN_TIMEOUT = 2000;
    private ComponentDescription toDeploy;
    private String toDeployName;
    private AtomicInteger inAllocationOperation = new AtomicInteger(0);
    private Prim target;
    private String hostPrefix;

    public FarmCustomerImpl() throws RemoteException {
    }


    /**
     * Create the nodes on startup
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        hostPrefix = sfResolve(ATTR_HOST_ATTR_PREFIX, "", true);
        role = sfResolve(ATTR_ROLE, "", true);
        min = sfResolve(ATTR_MIN, 0, true);
        max = sfResolve(ATTR_MAX, 0, true);
        deleteOnTerminate = sfResolve(ATTR_DELETE_ON_TERMINATE, true, true);
        farmer = (ClusterFarmer) sfResolve(ATTR_FARMER, (Prim) null, true);
        target = sfResolve(ATTR_TARGET, (Prim) null, true);
        expectedHostnames = ListUtils.resolveStringList(this, new Reference(ATTR_EXPECTED_HOSTNAMES), true);
        toDeploy = sfResolve(ATTR_TO_DEPLOY, toDeploy, false);
        toDeployName = sfResolve(ATTR_TO_DEPLOY_NAME, toDeployName, true);
        pingChecksNodes = sfResolve(ATTR_PING_CHECKS_NODES, pingChecksNodes, true);
        if (max > 0) {
            worker = new CustomerThread();
            worker.start();
        } else {
            ComponentHelper helper = new ComponentHelper(this);
            helper.sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                    "No nodes to create",
                    sfCompleteName,
                    null);
        }

    }


    /**
     * Check the nodes are there on a liveness call
     *
     * @param source source of call
     * @throws SmartFrogLivenessException failure to find a node in that role
     * @throws RemoteException            network problems
     */
    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (!pingChecksNodes || inAllocationOperation()) {
            return;
        }
        try {
            ClusterNode[] listed = farmer.list(role);
            Map<String, ClusterNode> map = new HashMap<String, ClusterNode>(listed.length);
            StringBuilder nodeList = new StringBuilder();
            boolean hasDuplicate = false;
            for (ClusterNode node : listed) {
                String nodeID = node.getId();
                if (map.get(nodeID) != null) {
                    hasDuplicate = true;
                }
                map.put(nodeID, node);
                nodeList.append(nodeID).append(" ");
            }
            if (hasDuplicate) {
                throw new SmartFrogLivenessException("Duplicate nodes in the farm: " + nodeList);
            }
            for (ClusterNode node : nodes) {
                if (map.get(node.getId()) == null) {
                    throw new SmartFrogLivenessException("Cannot find entry for " + node
                    +" in "+ nodeList);
                }
            }
        } catch (Exception e) {
            sfLog().error(e);
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(e);
        }
    }

    /**
     * Terminate nodes on shutdown
     *
     * @param status termination status
     */
    @Override
    public void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        WorkflowThread.requestAndWaitForThreadTermination(worker, SHUTDOWN_TIMEOUT);
        if (deleteOnTerminate) {
            try {
                farmer.delete(nodes);
            } catch (Exception e) {
                sfLog().info(e);
            }
        }
        nodes = null;
    }

    protected void enterAllocationOperation() {
        inAllocationOperation.incrementAndGet();
    }

    protected void exitAllocationOperation() {
        inAllocationOperation.decrementAndGet();
    }

    protected boolean inAllocationOperation() {
        return inAllocationOperation.get() > 0;
    }


    /**
     * This is the customer thread that pushed out the files
     */
    public class CustomerThread extends WorkflowThread {
        protected LogRemote exportedLog;

        /**
         * Create a basic thread. Notification is bound to a local notification object.
         */
        public CustomerThread() {
            super(FarmCustomerImpl.this, true);
        }

        /**
         * Allocate the machines
         *
         * @throws Throwable if anything went wrong
         */
        @SuppressWarnings({"ProhibitedExceptionDeclared"})
        @Override
        public void execute() throws Throwable {
            ClusterNode[] clusterNodes;
            enterAllocationOperation();
            try {
                clusterNodes = farmer.create(role, min, max);
            } finally {
                exitAllocationOperation();
            }
            //set the owner's attributes
            nodes = clusterNodes;
            int created = clusterNodes.length;
            sfReplaceAttribute(ATTR_DEPLOYED, created);
            String info = "Created " + created + " nodes of role " + role;
            sfLog().info(info);
            //check the expected value
            int expected = sfResolve(ATTR_EXPECTED, -1, true);
            if (expected >= 0 && expected != created) {
                throw new SmartFrogDeploymentException(info
                        + " - instead of the expected number " + expected);
            }
            //put the host list up
            Vector<String> hostnameList = new Vector<String>(created);
            Vector<ClusterNode> clusterNodeList = new Vector<ClusterNode>(created);
            StringBuilder hostnames = new StringBuilder();
            HashMap<String, ClusterNode> nodeMap = new HashMap<String, ClusterNode>(created);
            int counter = 0;
            for (ClusterNode node : clusterNodes) {
                String hostname = node.isExternallyVisible() ?
                        node.getExternalHostname() : node.getHostname();
                if (hostname == null) {
                    //trouble, the host does not know what it is
                    throw new SmartFrogException("Node doesn't know its hostname " + node.toString());
                }
                hostnameList.add(hostname);
                nodeMap.put(hostname, node);
                hostnames.append(hostname).append(' ');
                clusterNodeList.add(node);
                target.sfReplaceAttribute(hostPrefix + counter, hostname);
                counter++;
            }
            //publish the attributes
            target.sfReplaceAttribute(ATTR_CLUSTERNODES, clusterNodeList);
            target.sfReplaceAttribute(ATTR_HOSTNAMES, hostnameList);


            //now validate the expected hostname list
            for (String hostname : expectedHostnames) {
                if (nodeMap.get(hostname) == null) {
                    throw new SmartFrogDeploymentException("Failed to find expected host "
                            + hostname
                            + " in the list of allocated hosts: "
                            + hostnames);
                }
            }

            //now, if the toDeploy field is not empty, push something out to all of them
            if (toDeploy != null && !toDeployName.isEmpty()) {
                for (ClusterNode node : clusterNodes) {
                    NodeDeploymentService service = farmer.createNodeDeploymentService(node);
                    sfLog().info("Deploying application " + toDeployName + " to " + node.getHostname());
                    exportedLog = LogRemoteImpl.createExportedLog(sfLog());
                    String messages = service.deployApplication(toDeployName, 
                            toDeploy, 
                            null);
                    if (messages != null && !messages.isEmpty()) {
                        sfLog().info(messages);
                    }
                }
            }


        }
    }

}
