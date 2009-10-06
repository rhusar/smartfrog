package org.smartfrog.services.cloudfarmer.test.mock;

import junit.framework.TestCase;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NoClusterSpaceException;
import org.smartfrog.services.cloudfarmer.api.UnsupportedClusterRoleException;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.server.mock.MockClusterFarmerImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * This walks the mock farmer through its life
 */
public class MockFarmerUnitTest extends TestCase {

    private MockClusterFarmerImpl farmer;
    private static final String WORKER = "worker";
    private static final String MASTER = "master";

    private ClusterRoleInfo master, worker;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        farmer = new MockClusterFarmerImpl();
        farmer.fixupCompleteName("farmer");
        master = new ClusterRoleInfo(MASTER);
        master.setRoleSize(1,1);
        worker = new ClusterRoleInfo(WORKER);
        worker.setRoleSize(1, 100);
        addTestRoles();
    }

    public void testAddListRemove() throws Throwable {
        ClusterNode[] first = farmer.create(MASTER, 1, 1);
        assertEquals(1, first.length);
        ClusterNode[] nodes = farmer.create(WORKER, 1, 1);
        assertEquals(1, nodes.length);
        ClusterNode node = nodes[0];
        ClusterNode[] listed = listByRole(WORKER, 1);
        assertSame(listed[0], nodes[0]);
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
        farmer.setClusterLimit(2);
        try {
            farmer.create(WORKER, 7, 20);
            fail("should not have reached here");
        } catch (NoClusterSpaceException e) {
            //expected
        }
    }

    public void testNotFullySatisfied() throws Throwable {
        farmer.setClusterLimit(2);
        ClusterNode[] nodes = farmer.create(WORKER, 1, 20);
        assertEquals(2, nodes.length);
        //and now there should be no room
        try {
            farmer.create(MASTER, 1, 1);
            fail("should not have reached here");
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
        return listed;
    }

}
