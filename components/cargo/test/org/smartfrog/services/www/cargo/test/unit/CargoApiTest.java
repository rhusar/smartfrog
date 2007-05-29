/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.cargo.test.unit;

import junit.framework.TestCase;
import org.codehaus.cargo.container.jboss.JBossStandaloneLocalConfiguration;
import org.codehaus.cargo.container.jboss.JBoss3xInstalledLocalContainer;
import org.codehaus.cargo.container.jboss.JBoss4xInstalledLocalContainer;
import org.codehaus.cargo.container.jetty.Jetty4xEmbeddedStandaloneLocalConfiguration;
import org.codehaus.cargo.container.jetty.Jetty5xEmbeddedStandaloneLocalConfiguration;
import org.codehaus.cargo.container.jetty.Jetty6xEmbeddedStandaloneLocalConfiguration;
import org.codehaus.cargo.container.jetty.Jetty5xEmbeddedLocalContainer;
import org.codehaus.cargo.container.jetty.Jetty4xEmbeddedLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;

import java.io.File;

/**
 * This is here mainly as a regression test of the API, to catch any changes in the
 * stuff that we normally reflect in to
 * created 16-Jun-2006 17:06:27
 */

public class CargoApiTest extends TestCase {
    private String dir;

    private static boolean instantiate = false;

    protected void setUp() throws Exception {
        super.setUp();
        dir = new File(".").getAbsolutePath();
    }

    public void testJBoss3() throws Exception {
        LocalConfiguration local = new JBossStandaloneLocalConfiguration(dir);
        if (instantiate) {
            JBoss3xInstalledLocalContainer container = new JBoss3xInstalledLocalContainer(local);
        }
    }

    public void testJBoss4() throws Exception {
        LocalConfiguration local = new JBossStandaloneLocalConfiguration(dir);
        if (instantiate) {
            JBoss4xInstalledLocalContainer container = new JBoss4xInstalledLocalContainer(local);
        }
    }

    public void testTomcat() throws Exception {
        LocalConfiguration local = new JBossStandaloneLocalConfiguration(dir);
        if (instantiate) {
            JBoss3xInstalledLocalContainer container = new JBoss3xInstalledLocalContainer(local);
        }

    }

    public void testJetty5Configuration() throws Exception {
        LocalConfiguration local;
        local = new Jetty5xEmbeddedStandaloneLocalConfiguration(dir);
        if (instantiate) {
            Jetty5xEmbeddedLocalContainer container = new Jetty5xEmbeddedLocalContainer(local);
        }
    }

    public void testJetty6Configuration() throws Exception {
        LocalConfiguration local;
        local = new Jetty6xEmbeddedStandaloneLocalConfiguration(dir);
        if (instantiate) {
            Jetty5xEmbeddedLocalContainer container = new Jetty5xEmbeddedLocalContainer(local);
        }
    }

    public void testJetty4Configuration() throws Exception {
        LocalConfiguration local = new Jetty4xEmbeddedStandaloneLocalConfiguration(dir);
        if (instantiate) {
            Jetty4xEmbeddedLocalContainer container = new Jetty4xEmbeddedLocalContainer(local);
        }
    }

}
