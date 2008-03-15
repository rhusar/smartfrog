/* (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.database.test.system.core.mysql;

import org.smartfrog.test.DeployingTestBase;
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
