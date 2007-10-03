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

package org.smartfrog.services.www.jetty.test.system.full;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.test.TestHelper;

public abstract class JettyTestBase extends DeployingTestBase {
    public	static final String FILES = "org/smartfrog/services/www/jetty/test/system/full/";
    public static final String JETTY_HOME = "jetty.home";
    public static final String TEST_JETTY_HOME = "test." + JETTY_HOME;
    public static final String TEST_JASPER_FOUND = "test.jasper.found";

    public static final String ROOT_DOC = "/";//""/jetty/index.html";

    private boolean hasJasper;

    
	public JettyTestBase(String name) {
		super(name);
	}

    /**
     * looks up jetty home and probes for jasper
     */
    protected void setUp() throws Exception {
        super.setUp();
        String runtimeJettyHome = TestHelper.getTestProperty(TEST_JETTY_HOME,"");
        System.setProperty(JETTY_HOME, runtimeJettyHome);
        hasJasper= TestHelper.getTestProperty(TEST_JASPER_FOUND,null)!=null;
    }

    public boolean isHasJasper() {
        return hasJasper;
    }
}
