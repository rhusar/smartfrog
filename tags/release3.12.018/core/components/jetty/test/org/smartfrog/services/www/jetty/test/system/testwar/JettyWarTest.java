package org.smartfrog.services.www.jetty.test.system.testwar;

import org.smartfrog.services.www.jetty.test.system.JettyTestBase;

/**
 *
 */
public class JettyWarTest extends JettyTestBase {


    private static final String FILES ="/org/smartfrog/services/www/jetty/test/system/testwar/";

    public JettyWarTest(String name) {
        super(name);
    }


    public void testWarDeployed() throws Throwable {
        expectSuccessfulTestRun(FILES, "testWarDeployed");
    }


    public void testContextPathFixup() throws Throwable {
        expectSuccessfulTestRun(FILES, "testContextPathFixup");
    }

    public void testWarAtRoot() throws Throwable {
        expectSuccessfulTestRun(FILES,"testWarAtRoot");
    }

    public void testErrorPage() throws Throwable {
        expectSuccessfulTestRun(FILES, "testErrorPage");
    }


    public void testTcp27testFilesystemWar() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "tcp27testFilesystemWar");
    }
}
