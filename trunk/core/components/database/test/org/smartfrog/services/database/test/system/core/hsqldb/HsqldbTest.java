package org.smartfrog.services.database.test.system.core.hsqldb;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;

/**
 */
public class HsqldbTest extends DeployingTestBase {

    public HsqldbTest(String name) {
        super(name);
    }



    public void testTableManipulationTest() throws Throwable {
        application = deployExpectingSuccess(
                "/org/smartfrog/services/database/test/system/core/hsqldb/TableManipulationTest.sf",
                "TableManipulationTest");
        TestBlock block = (TestBlock) application;
        expectSuccessfulTermination(block);
    }


    public void testConnectionOpenTest() throws Throwable {
        application = deployExpectingSuccess(
                "/org/smartfrog/services/database/test/system/core/hsqldb/ConnectionOpenTest.sf",
                "ConnectionOpenTest");
        TestBlock block = (TestBlock) application;
        expectSuccessfulTermination(block);
    }
}
