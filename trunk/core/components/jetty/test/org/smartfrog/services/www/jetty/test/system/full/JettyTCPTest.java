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
 * JUnit test class for test cases related to "jetty" component This test suite
 * needs jetty to be fully installed somewhere, and the jetty home location
 * passed down to the program. This can be done by <ol> <li>setting the system
 * property jetty.home</li> <li>setting the system property
 * runtime.jetty.home</li> </ol>
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

    public void NotestCaseTCN52() throws Exception {
        deployExpectingException(FULL_FILES + "tcn52.sf",
                "tcn52",
                EXCEPTION_LIFECYCLE,
                "sfStart",
                EXCEPTION_DEPLOYMENT,
                "Illegal ClassType");
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
                EXCEPTION_DEPLOYMENT,
                "unnamed component",
                EXCEPTION_RESOLUTION,
                "error in schema: non-optional attribute 'listenerPort' is missing");
    }

    public void testCaseTCN57() throws Exception {
        deployExpectingException(FULL_FILES + "tcn57.sf",
                "tcn57",
                EXCEPTION_LIFECYCLE,
                "unnamed component");

/*
                                 EXCEPTION_RESOLUTION,
                                 "error in schema: wrong class found for attribute 'server', expected: java.lang.String");
*/
    }

    public void NotestCaseTCP19() throws Throwable {
        application = deployExpectingSuccess(FULL_FILES + "tcp19.sf", "tcp19");
        int port = 0;
        String host = application.sfResolve("serverHost",
                (String) null,
                true);
        port = application.sfResolve("port", port, true);
        URL url = new URL("http", host, port, ROOT_DOC);
        HttpURLConnection connection = null;
        int errorcode = 0;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            errorcode = connection.getResponseCode();
        } catch (FileNotFoundException e) {
            //if this is a 404 error, we have succeeded.
        	if(connection!=null) {
        		errorcode = connection.getResponseCode();
        	}
        } catch (ConnectException e  ) {
        	fail("Connection refused to "+url.toString());
        }
        assertEquals("Expected a 404 response from " + url + " but got " + errorcode,
                HttpURLConnection.HTTP_NOT_FOUND, errorcode);
    }

    public void NotestCaseTCP20() throws Throwable {
        application = deployExpectingSuccess(FULL_FILES + "tcp20.sf", "tcp20");
        Prim server1 = (Prim) application.sfResolveHere("server1");
        Prim server2 = (Prim) application.sfResolveHere("server2");
        String hostname1 = server1.sfResolve("serverHost", (String) null, true);
        Prim listener1 = (Prim) server1.sfResolveHere("listener");
        int port1 = listener1.sfResolve("listenerPort", 0, true);
        String hostname2 = server2.sfResolve("serverHost", (String) null, true);
        Prim listener2 = (Prim) server2.sfResolveHere("listener");
        int port2 = listener2.sfResolve("listenerPort", 0, true);
        URL url1 = new URL("http", hostname1, port1, ROOT_DOC);
        URLConnection urlConnection1 = url1.openConnection();
        URL url2 = new URL("http", hostname2, port2, ROOT_DOC);
        URLConnection urlConnection2 = url2.openConnection();

        BufferedReader in1 = null;
        BufferedReader in2 = null;

        try {
            in1 = new BufferedReader(
                    new InputStreamReader(
                            urlConnection1.getInputStream()));
            in2 = new BufferedReader(
                    new InputStreamReader(
                            urlConnection2.getInputStream()));
            String inputLine1;
            String inputLine2 = null;

            while ((inputLine1 = in1.readLine()) != null && (inputLine2 = in2.readLine()) != null)
            {
                assertEquals(inputLine1, inputLine2);
            }
        } finally {
            FileSystem.close(in1);
            FileSystem.close(in2);
        }
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

    public void testCaseTCP22() throws Throwable {
        application = deployExpectingSuccess(FULL_FILES + "tcp22.sf", "tcp22");
/*
        assertNotNull(application);
        Prim server = (Prim) application.sfResolve("adminServer");
        String host = server.sfResolve("httpserverHost",
                (String) null,
                true);
        int port = server.sfResolve("listenerPort", 0, true);
        URL url = new URL("http", host, port, "/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String expectedmessage = "Unauthorized";
        String actualmessage = urlConnection.getResponseMessage();
        int expectedcode = 401;
        int actualcode = urlConnection.getResponseCode();
        assertEquals(expectedmessage, actualmessage);
        assertEquals(expectedcode, actualcode);
*/
    }
}
