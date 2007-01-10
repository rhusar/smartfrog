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
        application = deployExpectingSuccess(
                BASE +"TableManipulationTest.sf",
                "TableManipulationTest");
        TestBlock block = (TestBlock) application;
        expectSuccessfulTermination(block);
    }


    public void testConnectionOpenTest() throws Throwable {
        application = deployExpectingSuccess(
                BASE +"ConnectionOpenTest.sf",
                "ConnectionOpenTest");
        TestBlock block = (TestBlock) application;
        expectSuccessfulTermination(block);
    }
}
