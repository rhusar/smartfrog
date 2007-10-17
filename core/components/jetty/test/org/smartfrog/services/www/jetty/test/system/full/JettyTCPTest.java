/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.www.jetty.test.system.full;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.www.jetty.test.system.JettyTestBase;
import org.smartfrog.services.jetty.JettyIntf;
import org.smartfrog.sfcore.prim.Prim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * JUnit test class for some of the test cases related to the "jetty" component.
 *
 */
public class JettyTCPTest
        extends JettyTestBase {


    public JettyTCPTest(String s) {
        super(s);
    }


    public void testCaseTCPJettyCore() throws Throwable {
        application = deployExpectingSuccess(FULL_FILES + "tcp_jetty_core.sf",
                "tcp_jetty_core");
        assertNotNull(application);
    }

    public void testCaseTCN52() throws Exception {
        deployExpectingException(FULL_FILES + "tcn52.sf",
                "tcn52",
                EXCEPTION_LIFECYCLE,
                "sfStart",
                EXCEPTION_DEPLOYMENT,
                JettyIntf.ATTR_JETTY_SERVER);
    }


    public void testCaseTCN53() throws Exception {
        deployExpectingException(FULL_FILES + "tcn53.sf",
                "tcn53",
                EXCEPTION_LIFECYCLE,
                "sfStart",
                EXCEPTION_DEPLOYMENT,
                "java.net.UnknownHostException: no-hostname");
    }

    public void NotestCaseTCN54() throws Exception {
        deployExpectingException(FULL_FILES + "tcn54.sf",
                "tcn54",
                EXCEPTION_DEPLOYMENT,
                "unnamed component",
                EXCEPTION_RESOLUTION,
                "java.lang.StackOverflowError");
    }

    public void testCaseTCN55() throws Exception {
        deployExpectingException(FULL_FILES + "tcn55.sf",
                "tcn55",
                EXCEPTION_DEPLOYMENT,
                "unnamed component",
                EXCEPTION_RESOLUTION,
                "jettyhome");
    }

    public void testCaseTCN56() throws Exception {
        deployExpectingException(FULL_FILES + "tcn56.sf",
                "tcn56",
                EXCEPTION_LIFECYCLE,
                null,
                EXCEPTION_DEPLOYMENT,
                "Port value out of range");
    }

    public void testCaseTCN57() throws Exception {
        deployExpectingException(FULL_FILES + "tcn57.sf",
                "tcn57",
                EXCEPTION_LIFECYCLE,
                "unnamed component");
    }




    public void testCaseTCP21() throws Throwable {
        application = deployExpectingSuccess(FULL_FILES + "tcp21.sf", "tcp21");
        assertNotNull(application);
        Prim server = (Prim) application.sfResolve("server");
        String jettyhome = server.sfResolve("jettyhome", (String) null, true);
        String filename = jettyhome.concat(
                File.separator + "demo"
                        + File.separator + "webapps"
                        + File.separator + "root"
                        + File.separator + "index.html");
        File file = new File(filename);
        File jettyfile = new File(jettyhome);

        assertTrue("Not found" + file, file.exists());
        assertTrue("Not found" + jettyfile, jettyfile.exists());
        assertFalse("Shound not be a directory " + file, file.isDirectory());
        assertTrue("Should be a directory " + jettyfile,
                jettyfile.isDirectory());
    }

}
