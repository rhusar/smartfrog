package org.smartfrog.services.database.test.system.core.mysql;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;

/**
 * Mysql tests
 */
public class MysqlTest extends DeployingTestBase {
    private static final String BASE = "/org/smartfrog/services/database/test/system/core/mysql/";

    public MysqlTest(String name) {
        super(name);
    }

    public void testTableManipulationTest() throws Throwable {
        deployAndTerminate("TableManipulationTest");
    }


    public void testConnectionOpenTest() throws Throwable {
        deployAndTerminate("ConnectionOpenTest");
    }

    public void testIsMysqlLiveTest() throws Throwable {
        deployAndTerminate("IsMysqlLiveTest");
    }

    public void testMysqlStartTest() throws Throwable {
        deployAndTerminate("MysqlStartTest");
    }
    
    public void testShutdownTest() throws Throwable {
        deployAndTerminate("ShutdownTest");
    }

    private void deployAndTerminate(String template) throws Throwable {
        application = deployExpectingSuccess(
                BASE + template +".sf",
                template);
        TestBlock block = (TestBlock) application;
        expectSuccessfulTermination(block);
    }


}
