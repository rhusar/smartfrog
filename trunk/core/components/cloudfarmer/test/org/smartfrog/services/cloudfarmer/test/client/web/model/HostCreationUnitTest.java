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
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.DynamicSmartFrogClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.server.mock.MockClusterFarmerImpl;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;


/**
 * Created 30-Oct-2009 13:32:29
 */

public class HostCreationUnitTest extends TestCase {

    private MockClusterFarmerImpl farmer;
    DynamicSmartFrogClusterController controller;
    private static final String WORKER = "worker";
    private static final String MASTER = "master";

    private ClusterRoleInfo master, worker;
    private static final int CLUSTER_SIZE = 50;
    Log log = LogFactory.getLog(HostCreationUnitTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        farmer = new MockClusterFarmerImpl();
        farmer.initForMockUse(CLUSTER_SIZE, "internal", "external", true, 500);
        master = new ClusterRoleInfo(MASTER);
        master.setRoleSize(1, 1);
        worker = new ClusterRoleInfo(WORKER);
        worker.setRoleSize(1, 100);
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
        List<ClusterController.RoleAllocationReqest> requests = new ArrayList<ClusterController.RoleAllocationReqest>(
                2);
        requests.add(new ClusterController.RoleAllocationReqest("master", 0, 1, 1));
        requests.add(new ClusterController.RoleAllocationReqest("worker", -1, 5, 8));
        ClusterController.AsynchronousHostCreationThread workerThread = controller.asyncCreateHosts(requests);
        log.info("Notify object = " + workerThread.getNotifyObject() + "; finished = "+ workerThread.isFinished());
        
        //workerThread.waitForNotification(60000);
        long timeout = System.currentTimeMillis()+60000;
        //spin for a bit
        while(!workerThread.isFinished() && System.currentTimeMillis()<timeout) {
            Thread.sleep(1000);
        }
        if(workerThread.isThrown()) {
            throw workerThread.getThrown();
        }
        assertTrue("Worker is not finished", workerThread.isFinished());
        HostInstanceList hosts = workerThread.getHostList();
        assertTrue("Host list is only "+hosts.size(), hosts.size() >=6 );
        assertNotNull("Hosts have no master", hosts.getMaster());
        List<HostInstance> workerList = hosts.getListInRole("worker");
        assertTrue("Worker list is only " + hosts.size(), workerList.size() >= 5);

    }
}
