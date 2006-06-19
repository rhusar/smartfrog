package org.smartfrog.services.www.cargo.test.system.tomcat;

import org.smartfrog.services.www.cargo.test.system.CargoTestBase;

/**
 */
public class TomcatTest extends CargoTestBase {

    public TomcatTest(String name) {
        super(name);
    }

    public void testTomcat() throws Throwable {
        deployAppServer(FILE_BASE + "tomcat/testTomcat.sf",
                "testTomcat"
        );
    }
}
