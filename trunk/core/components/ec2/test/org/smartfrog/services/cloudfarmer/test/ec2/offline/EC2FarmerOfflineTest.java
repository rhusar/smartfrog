package org.smartfrog.services.cloudfarmer.test.ec2.offline;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class EC2FarmerOfflineTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/ec2/offline";

    public EC2FarmerOfflineTest(String name) {
        super(name);
    }

    public void testFarmHasRoles() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmHasRoles");
    }

    public void testFarmLacksRole() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testFarmLacksRole");
    }

    public void testMasterLacksImageID() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testMasterLacksImageID");
    }
}
