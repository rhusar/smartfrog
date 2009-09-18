package org.smartfrog.test.system.farmer;

import junit.framework.TestCase;
import org.smartfrog.services.farmer.ClusterNode;
import org.smartfrog.services.farmer.MockClusterFarmerImpl;
import org.smartfrog.services.farmer.NoClusterSpaceException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * This walks the mock farmer through its life
 */
public class MockFarmerUnitTest extends TestCase {

    private MockClusterFarmerImpl farmer;
    private static final String WORKER = "worker";
    private static final String MASTER = "master";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        farmer = new MockClusterFarmerImpl();
    }

    public void testAddListRemove() throws Throwable {
        ClusterNode[] first = farmer.create("first", 1, 1);
        assertEquals(1, first.length);
        ClusterNode[] nodes = farmer.create("test", 1, 1);
        assertEquals(1, nodes.length);
        ClusterNode node = nodes[0];
        ClusterNode[] listed = listByRole("test", 1);
        assertSame(listed[0], nodes[0]);
        listByRole("other", 0);
        ClusterNode node2 = farmer.lookup(node.getId());
        assertSame(node, node2);
        node2 = farmer.lookupByHostname(node.getHostname());
        assertSame(node, node2);
    }

    public void testBulkCreate() throws Throwable {
        ClusterNode[] third = farmer.create("third", 4, 4);
        assertEquals(4, third.length);
        listByRole("third", 4);
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
            farmer.create(MASTER, 7, 20);
            fail("should not have reached here");
        } catch (NoClusterSpaceException e) {
            //expected
        }
    }

    public void testNotFullySatisfied() throws Throwable {
        farmer.setClusterLimit(2);
        ClusterNode[] nodes = farmer.create(MASTER, 1, 20);
        assertEquals(2, nodes.length);
        //and now there should be no room
        try {
            farmer.create(MASTER, 1, 1);
            fail("should not have reached here");
        } catch (NoClusterSpaceException e) {
            //expected
        }
    }

    public void testNoRoles() throws Throwable {
        String[] roles = farmer.listAvailableRoles();
        assertEquals(0, roles.length);
    }

    public void testAddRole() throws Throwable {
        farmer.addRole(MASTER);
        farmer.addRole(WORKER);
        String[] roles = farmer.listAvailableRoles();
        assertEquals(2, roles.length);
    }


    public void testCreateRole() throws Throwable {
        farmer.addRole(MASTER);
        farmer.create(MASTER, 1, 1);
    }

    public void testNoCreateInvalidRole() throws Throwable {
        farmer.addRole(MASTER);
        farmer.create(WORKER, 1, 1);
    }

    private ClusterNode[] listByRole(String role, int expected) throws IOException, SmartFrogException {
        ClusterNode[] listed;
        listed = farmer.list(role);
        assertEquals("Expected to find " + expected + " nodes of role '" + role + "' but got" + listed.length,
                expected, listed.length);
        return listed;
    }

}
