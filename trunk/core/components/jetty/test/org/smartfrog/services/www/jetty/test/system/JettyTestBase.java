/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */

package org.smartfrog.services.www.jetty.test.system;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.test.TestHelper;
import org.smartfrog.sfcore.prim.Prim;

import java.util.Properties;

public abstract class JettyTestBase extends DeployingTestBase {
    public static final String FULL_FILES = "org/smartfrog/services/www/jetty/test/system/full/";
    public static final String JETTY_HOME = "jetty.home";
    public static final String TEST_JETTY_HOME = "test." + JETTY_HOME;
    public static final String TEST_JASPER_FOUND = "test.jasper.found";

    public static final String ROOT_DOC = "/";//""/jetty/index.html";

    private boolean hasJasper;
    /** location for files. {@value} */
    public static final String SYSTEM_FILES = "/org/smartfrog/services/www/jetty/test/system/";
    private static final String HTTP_PROXY_HOST = "http.proxyHost";
    private String proxyHost;
    private static final String HTTP_PROXY_PORT = "http.proxyPort";
    private String proxyPort;


    public JettyTestBase(String name) {
        super(name);
    }

    /**
     * looks up jetty home and probes for jasper -turns off proxy settings for this test, so no proxy gets in the way of
     * the results (which can only contaminate the tests)
     */
    protected void setUp() throws Exception {
        super.setUp();
        String runtimeJettyHome = TestHelper.getTestProperty(TEST_JETTY_HOME, "");
        System.setProperty(JETTY_HOME, runtimeJettyHome);
        hasJasper = TestHelper.getTestProperty(TEST_JASPER_FOUND, null) != null;
        proxyHost = System.getProperty(HTTP_PROXY_HOST, null);
        proxyPort = System.getProperty(HTTP_PROXY_PORT, null);
        Properties sysprops= System.getProperties();
        if (proxyHost != null) {
            System.setProperty(HTTP_PROXY_HOST, "");
        }
        if (proxyPort != null) {
            System.setProperty(HTTP_PROXY_PORT, "");
        }
    }


    /**
     * Restore proxy settings
     *
     * @throws Exception if things go wrong
     */
    protected void tearDown() throws Exception {
        if (proxyHost != null) {
            System.setProperty(HTTP_PROXY_HOST, proxyHost);
        }
        if (proxyPort != null) {
            System.setProperty(HTTP_PROXY_PORT, proxyPort);
        }
        super.tearDown();
    }

    /**
     * Test for jasper (JSP) support
     *
     * @return true if present
     */
    public boolean isHasJasper() {
        return hasJasper;
    }

    /**
     * Deploy a web application, and search for a component called ping, which is then pinged repeatedly
     *
     * @param resource resource to deploy
     * @param name     application name
     * @throws Throwable if things go wrong
     */
    public void deployWebApp(String resource, String name) throws
            Throwable {
        setApplication(deployExpectingSuccess(resource, name));
        Prim liveness = (Prim) getApplication().sfResolve("ping");
        for (int i = 0; i < 100; i++) {
            liveness.sfPing(null);
        }
    }
}
