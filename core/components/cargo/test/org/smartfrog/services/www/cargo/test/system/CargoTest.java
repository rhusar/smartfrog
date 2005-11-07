package org.smartfrog.services.www.cargo.test.system;

/**
 * Created by IntelliJ IDEA.
 * User: Xav
 * Date: Nov 7, 2005
 * Time: 4:51:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class CargoTest extends CargoTestBase {
    public CargoTest(String name) {
        super(name);
    }

    public void testIncomplete() throws Exception {
        deployExpectingException(FILE_BASE+"testIncomplete.sf",
                "testIncomplete",
                EXCEPTION_DEPLOYMENT,
                "",
                EXCEPTION_RESOLUTION,
                "error in schema: non-optional attribute 'configurationClass' is missing");
    }

    public void testTomcat() throws Throwable {
        setApplication(deployExpectingSuccess(FILE_BASE+"testTomcat.sf",
        "testTomcat"
        ));
    }
}
