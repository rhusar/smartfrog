package org.smartfrog.services.cloudfarmer.test.mock;

import junit.framework.TestCase;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.NoClusterSpaceException;
import org.smartfrog.services.cloudfarmer.api.UnsupportedClusterRoleException;
import org.smartfrog.services.cloudfarmer.server.mock.MockClusterFarmerImpl;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.hadoop.HadoopRoles;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * This walks the mock farmer through its life
 */
@SuppressWarnings({"ProhibitedExceptionDeclared"})
public class MockFarmerUnitTest extends TestCase {

    private MockClusterFarmerImpl farmer;
    private static final String WORKER = HadoopRoles.WORKER;
    private static final String MASTER = HadoopRoles.MASTER;

    private ClusterRoleInfo master, worker;
    private static final int CLUSTER_SIZE = 50;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        farmer = new MockClusterFarmerImpl();
        farmer.initForMockUse(CLUSTER_SIZE, "internal", "external", true, 0);
        master = new ClusterRoleInfo(MASTER);
        master.setRoleSize(1, 1);
        worker = new ClusterRoleInfo(WORKER);
        worker.setRoleSize(1, 100);
        addTestRoles();
    }

    private void assertInRole(ClusterNode node, String role) {
        assertNotNull(node);
        assertEquals(role, node.getRole());
    }

    public void testAddListRemove() throws Throwable {
        ClusterNode[] first = farmer.create(MASTER, 1, 1);
        assertEquals(1, first.length);
        assertInRole(first[0], MASTER);
        ClusterNode[] nodes = farmer.create(WORKER, 1, 1);
        assertEquals(1, nodes.length);
        ClusterNode node = nodes[0];
        assertInRole(node, WORKER);
        ClusterNode[] listed = listByRole(WORKER, 1);
        assertEquals("Only one worker listed", 1, listed.length);
        assertSame(listed[0], nodes[0]);
        assertInRole(listed[0], WORKER);
        listed = listByRole(MASTER, 1);
        assertEquals("Only one master listed", 1, listed.length);
        //tests that roles propagate down
        assertInRole(listed[0], MASTER);
        listByRole("other", 0);
        ClusterNode node2 = farmer.lookup(node.getId());
        assertSame(node, node2);
        node2 = farmer.lookupByHostname(node.getHostname());
        assertSame(node, node2);
    }

    public void testBulkCreate() throws Throwable {
        ClusterNode[] third = farmer.create(WORKER, 4, 4);
        assertEquals(4, third.length);
        listByRole(WORKER, 4);
    }

    public void testNoNegatives() throws Throwable {
        try {
            farmer.create(MASTER, -10, 1);
            fail("should not have reached here");
        } catch (SmartFrogException e) {
            //expected
        }
    }

    public void testNoRoomAtAll() throws Throwable {
        try {
            farmer.create(WORKER, CLUSTER_SIZE + 10, CLUSTER_SIZE + 30);
            fail("should not have reached here");
        } catch (NoClusterSpaceException e) {
            //expected
        }
    }

    public void testNotFullySatisfied() throws Throwable {
        ClusterNode[] nodes = farmer.create(WORKER, CLUSTER_SIZE, CLUSTER_SIZE + 20);
        assertEquals(CLUSTER_SIZE, nodes.length);
        assertEquals(0, farmer.countFreeNodes());
        //and now there should be no room
        try {
            ClusterNode[] clusterNodes = farmer.create(MASTER, 1, 1);
            fail("there should have been no room for a master, but the cluster still thinks it has space "
                    + farmer.countFreeNodes()
                    + " and it returned an array of nodes of length " + clusterNodes.length);
        } catch (NoClusterSpaceException e) {
            //expected
        }
    }

    public void testTwoRoles() throws Throwable {
        String[] roles = farmer.listAvailableRoles();
        assertEquals(2, roles.length);
    }

    public void testAddRole() throws Throwable {
        farmer.addRole("Test", worker);
        String[] roles = farmer.listAvailableRoles();
        assertEquals(3, roles.length);
        ClusterRoleInfo[] roleInfos = farmer.listClusterRoles();
        assertEquals(3, roleInfos.length);
    }

    private void addTestRoles() {
        farmer.addRole(MASTER, master);
        farmer.addRole(WORKER, worker);
    }


    public void testCreateRole() throws Throwable {
        farmer.addRole("NewRole", master);
        farmer.create("NewRole", 1, 1);
    }

    public void testNoCreateInvalidRole() throws Throwable {
        try {
            farmer.create("Invalid", 1, 1);
            fail("should not have reached here");
        } catch (UnsupportedClusterRoleException e) {
            //expected
        }
    }

    private ClusterNode[] listByRole(String role, int expected) throws IOException, SmartFrogException {
        ClusterNode[] listed;
        listed = farmer.list(role);
        assertEquals("Expected to find " + expected + " nodes of role '" + role + "' but got " + listed.length,
                expected, listed.length);
        for (int i = 0; i < expected; i++) {
            ClusterNode node = listed[i];
            assertEquals(" Node " + i + " with value " + node + " is not a " + role, role, node.getRole());
        }
        return listed;
    }

    /**
     * Test that the list of roles works
     * @throws Throwable on failure
     */
    public void testListReturnsRoles() throws Throwable {
        int clusterSize = 3;
        ClusterNode[] clusterNodes = createSmallHadoopCluster(clusterSize);
        int workers = 0;
        int masters = 0;
        for (ClusterNode cn : clusterNodes) {
            if (cn.isInRole(MASTER)) masters++;
            if (cn.isInRole(WORKER)) workers++;
        }
        assertEquals("One master", 1, masters);
        assertEquals("workers", clusterSize, workers);
    }

    private ClusterNode[] createSmallHadoopCluster(int clusterSize) throws IOException, SmartFrogException {
        ClusterNode[] first = farmer.create(MASTER, 1, 1);
        assertEquals(1, first.length);
        assertInRole(first[0], MASTER);
        ClusterNode[] nodes = farmer.create(WORKER, clusterSize, clusterSize);
        assertEquals(clusterSize, nodes.length);
        ClusterNode[] clusterNodes = farmer.list();
        return clusterNodes;
    }

    public void testHostInstanceListPropagatesRoles() throws Throwable {
        int clusterSize = 3;
        ClusterNode[] clusterNodes = createSmallHadoopCluster(clusterSize);
        HostInstanceList hil= new HostInstanceList(clusterNodes);
        List<HostInstance> masters = hil.getListInRole(MASTER);
        List<HostInstance> workers = hil.getListInRole(WORKER);
        assertEquals(clusterSize, workers.size());
        assertEquals(1, masters.size());

    }


}
