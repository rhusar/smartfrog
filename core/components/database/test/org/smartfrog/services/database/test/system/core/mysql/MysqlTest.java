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


    public void testCheckNoMysql() throws Throwable {
        deployAndTerminate("CheckNoMysqlTest");
    }

    public void testConnectionOpenTest() throws Throwable {
        deployAndTerminate("ConnectionOpenTest");
    }

    public void testIsMysqlLive() throws Throwable {
        deployAndTerminate("IsMysqlLiveTest");
    }

    public void testMysqlStart() throws Throwable {
        deployAndTerminate("MysqlStartTest");
    }
    
    public void testShutdown() throws Throwable {
        deployAndTerminate("ShutdownTest");
    }

    public void testTableManipulation() throws Throwable {
        deployAndTerminate("TableManipulationTest");
    }

    /*
     *@skip: only works if you deploy mysql in grant-tables mode
     */
    public void NotestUserManipulation() throws Throwable {
        deployAndTerminate("UserManipulationTest");
    }

    private void deployAndTerminate(String template) throws Throwable {
        application = deployExpectingSuccess(
                BASE + template +".sf",
                template);
        TestBlock block = (TestBlock) application;
        expectSuccessfulTermination(block);
    }


}
