package org.smartfrog.services.database.test.system.core.mysql;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.events.TestCompletedEvent;

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

    public void testInstallMysqlTest() throws Throwable {
        deployAndTerminateMysql("InstallMysqlTest");
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
            TestCompletedEvent event = expectAbnormalTestRun(BASE, "MissingDatabaseTest", true, null);
            Throwable cause = event.getCause();
            //see SFOS-383
            if(cause!=null && cause.getMessage().indexOf("Table 'mysql.proc' doesn't exist")>=0) {
                throw cause;
            }
        }
    }

    /*
    *@skip: only works if you deploy mysql in grant-tables mode
    */
    public void NotestUserManipulation() throws Throwable {
        deployAndTerminateMysql("UserManipulationTest");
    }

    private TestCompletedEvent deployAndTerminate(String template) throws Throwable {
        return runTestsToCompletion(BASE, template);
    }

    private void deployAndTerminateMysql(String template) throws Throwable {
        if (mysqlPresent) {
            runTestsToCompletion(BASE,template);
        } else {
            getLog().info("Skipping test "+template+" as mysqld is not found on the path");
        }
    }

}
