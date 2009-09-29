package org.smartfrog.services.amazon.ec2farmer.test;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class EC2FarmerOfflineTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/amazon/ec2farmer/test/";

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
