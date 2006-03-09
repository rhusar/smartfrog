package org.smartfrog.services.www.cargo.test.system.tomcat;

import org.smartfrog.services.www.cargo.test.system.CargoTestBase;

/**
 */
public class TomcatTest extends CargoTestBase {

    public TomcatTest(String name) {
        super(name);
    }

    public void testJBoss4() throws Throwable {
        deployApp(FILE_BASE + "jboss/testTomcat.sf",
                "testTomcat"
        );
    }
}
