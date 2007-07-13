package org.smartfrog.services.database.test.system.core.mysql;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;

/** Mysql tests */
public class MysqlTest extends DeployingTestBase {
    private static final String BASE = "/org/smartfrog/services/database/test/system/core/mysql/";

    private boolean mysqlPresent;
    public static final String MYSQL_PRESENT = "test.mysql.present";

    public MysqlTest(String name) {
        super(name);
    }


    protected void setUp() throws Exception {
        super.setUp();
        mysqlPresent=Boolean.getBoolean(MYSQL_PRESENT);
    }


    public void testCheckNoMysql() throws Throwable {
        deployAndTerminate("CheckNoMysqlTest");
    }

    public void testConnectionOpenTest() throws Throwable {
        deployAndTerminateMysql("ConnectionOpenTest");
    }

    public void testIsMysqlLive() throws Throwable {
        deployAndTerminateMysql("IsMysqlLiveTest");
    }

    public void testMysqlStart() throws Throwable {
        deployAndTerminateMysql("MysqlStartTest");
    }

    public void testShutdown() throws Throwable {
        deployAndTerminateMysql("ShutdownTest");
    }

    public void testTableManipulation() throws Throwable {
        deployAndTerminateMysql("TableManipulationTest");
    }

    public void testIssueWarnings() throws Throwable {
        deployAndTerminateMysql("IssueWarningsTest");
    }

    /**
     * Test that this raises an exception that we can marshall
     *
     * @throws Throwable
     */
    public void testMissingDatabase() throws Throwable {
        if(mysqlPresent) {
            TestBlock block = deploy("MissingDatabaseTest");
            expectAbnormalTermination(block);
        }
    }

    /*
    *@skip: only works if you deploy mysql in grant-tables mode
    */
    public void NotestUserManipulation() throws Throwable {
        deployAndTerminateMysql("UserManipulationTest");
    }

    private void deployAndTerminate(String template) throws Throwable {
        TestBlock block = deploy(template);
        expectSuccessfulTermination(block);
    }

    private void deployAndTerminateMysql(String template) throws Throwable {
        if (mysqlPresent) {
            deployAndTerminate(template);
        } else {
            getLog().info("Skipping test "+template+" as mysql is not found");
        }
    }

    private TestBlock deploy(String template) throws Throwable {
        application = deployExpectingSuccess(
                BASE + template + ".sf",
                template);
        TestBlock block = (TestBlock) application;
        return block;
    }


}
