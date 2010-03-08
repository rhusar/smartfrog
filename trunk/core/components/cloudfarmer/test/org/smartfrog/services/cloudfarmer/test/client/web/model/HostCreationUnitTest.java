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
package org.smartfrog.services.cloudfarmer.test.client.web.model;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.NodeLink;
import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.MasterWorkerRoles;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.DynamicSmartFrogClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequest;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequestList;
import org.smartfrog.services.cloudfarmer.server.mock.MockClusterFarmerImpl;

import java.util.List;


/**
 * Created 30-Oct-2009 13:32:29
 */

@SuppressWarnings({"ProhibitedExceptionThrown"})
public class HostCreationUnitTest extends TestCase {

    private MockClusterFarmerImpl farmer;
    DynamicSmartFrogClusterController controller;
    private static final String WORKER = MasterWorkerRoles.WORKER;
    private static final String MASTER = MasterWorkerRoles.MASTER;

    private ClusterRoleInfo master, worker;
    private static final int CLUSTER_SIZE = 50;
    private static final Log log = LogFactory.getLog(HostCreationUnitTest.class);
    private static final NodeLink[] MasterLinks = {
            new NodeLink("root", "http", 8080, "/")
    };


    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        farmer = new MockClusterFarmerImpl();
        farmer.initForMockUse(CLUSTER_SIZE, "internal", "external", true, 500);
        master = new ClusterRoleInfo(MASTER);
        master.setRoleSize(1, 1);
        master.setLinks(MasterLinks);
        worker = new ClusterRoleInfo(WORKER);
        worker.setRoleSize(1, 100);
        master.setLinks(MasterLinks);
        addTestRoles();
        controller = new DynamicSmartFrogClusterController(farmer);
    }

    private void addTestRoles() {
        farmer.addRole(MASTER, master);
        farmer.addRole(WORKER, worker);
    }

    private void assertInRole(ClusterNode node, String role) {
        assertNotNull(node);
        assertEquals(role, node.getRole());
    }

    public void testAsyncHostCreation() throws Throwable {
        //add a master automatically
        RoleAllocationRequestList requests = new RoleAllocationRequestList(2);

        requests.add(new RoleAllocationRequest(MASTER, 0, 1, 1, null));
        requests.add(new RoleAllocationRequest(WORKER, -1, 5, 8, null));


        ClusterController.HostCreationThread workerThread = controller.asyncCreateHosts(requests, 0, null, null);
        log.info("Notify object = " + workerThread.getNotifyObject() + "; finished = " + workerThread.isFinished());

        //workerThread.waitForNotification(60000);
        long timeout = System.currentTimeMillis() + 60000;
        //spin for a bit
        log.info("Main thread sleeping");
        while (!workerThread.isFinished() && System.currentTimeMillis() < timeout) {
            Thread.sleep(1000);
        }
        log.info("Main thread finished sleeping");
        if (workerThread.isThrown()) {
            log.info("rethrowing worker thread exception ", workerThread.getThrown());
            throw workerThread.getThrown();
        }
        assertTrue("Worker is not finished", workerThread.isFinished());
        HostInstanceList hosts = workerThread.getHostList();
        assertTrue("Host list is only " + hosts.size(), hosts.size() >= 6);
        HostInstance masterInstance = hosts.getMaster();
        assertNotNull("Hosts have no master", masterInstance);
        NodeLink[] nodeLinks = masterInstance.getLinks();
        assertNotNull("master links are null", nodeLinks);
        assertTrue("master links list is empty", nodeLinks.length > 0);
        List<HostInstance> workerList = hosts.getListInRole(WORKER);
        assertTrue("Worker list is only " + hosts.size(), workerList.size() >= 5);

    }
}
