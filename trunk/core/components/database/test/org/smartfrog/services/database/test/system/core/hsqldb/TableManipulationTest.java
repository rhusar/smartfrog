package org.smartfrog.services.database.test.system.core.hsqldb;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.TestBlock;

/**
 */
public class TableManipulationTest extends DeployingTestBase {

    public TableManipulationTest(String name) {
        super(name);
    }


    protected static final String FILES = "org/smartfrog/test/system/workflow/sequence/";

    public void testSequence() throws Throwable {
        application = deployExpectingSuccess(
                "/org/smartfrog/services/database/test/system/core/hsqldb/TableManipulationTest.sf",
                "TableManipulationTest");
        TestBlock block = (TestBlock) application;
        expectSuccessfulTermination(block);
    }

}
