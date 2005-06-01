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


package org.smartfrog.test.system.jetty;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;

import java.net.*;
import java.io.*;

/**
 * JUnit test class for test cases related to "jetty" component
 */
public class JettyTest
    extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/jetty/";

    public JettyTest(String s) {
        super(s);
    }

    public void testCaseTCN52() throws Exception {
        deployExpectingException(FILES+"tcn52.sf",
                                 "tcn52",
                                 "SmartFrogLifecycleException",
                                 "sfStart",
                                 "SmartFrogDeploymentException",
                                 "Illegal ClassType");
    }

    public void testCaseTCN53() throws Exception {
        deployExpectingException(FILES+"tcn53.sf",
                                 "tcn53",
                                 "SmartFrogLifecycleException",
                                 "sfStart",
                                 "SmartFrogDeploymentException",
                                 "java.net.UnknownHostException: no-hostname");
    }

    public void testCaseTCN54() throws Exception {
        deployExpectingException(FILES+"tcn54.sf",
                                 "tcn54",
                                 "SmartFrogDeploymentException",
                                 "unnamed component",
                                 "SmartFrogResolutionException",
                                 "java.lang.StackOverflowError");
    }

    public void testCaseTCN55() throws Exception {
        deployExpectingException(FILES+"tcn55.sf",
                                 "tcn55",
                                 "SmartFrogDeploymentException",
                                 "unnamed component",
                                 "SmartFrogResolutionException",
                                 "Unresolved Reference, data: [jettyhome");
    }

    public void testCaseTCN56() throws Exception {
        deployExpectingException(FILES+"tcn56.sf",
                                 "tcn56",
                                 "SmartFrogDeploymentException",
                                 "unnamed component",
                                 "SmartFrogResolutionException",
            "error in schema: non-optional attribute 'listenerPort' is missing");
    }

    public void testCaseTCN57() throws Exception {
        deployExpectingException(FILES+"tcn57.sf",
                                 "tcn57",
                                 "SmartFrogDeploymentException",
                                 "unnamed component",
                                 "SmartFrogResolutionException",
                                 "error in schema: wrong class found for attribute 'server', expected: java.lang.String");
    }

    public void testCaseTCP19() throws Throwable {
        Prim application = deployExpectingSuccess(FILES+"tcp19.sf", "tcp19");
        assertNotNull(application);
        int port = 0;
        String hostname = application.sfResolve("serverHost", (String)null, true);
        port = application.sfResolve("port", port, true);
        URL url = new URL("http", hostname, port, "/jetty/index.html");
        URLConnection urlConnection = url.openConnection();

        BufferedReader in = new BufferedReader(
            new InputStreamReader(
            urlConnection.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine())!=null)
            assertNotNull(inputLine);
        in.close();
    }

    public void testCaseTCP20() throws Throwable {
        Prim application = deployExpectingSuccess(FILES+"tcp20.sf", "tcp20");
        assertNotNull(application);
        Prim server1 = (Prim)application.sfResolveHere("server1");
        Prim server2 = (Prim)application.sfResolveHere("server2");
        String hostname1 = server1.sfResolve("serverHost", (String)null, true);
        Prim listener1 = (Prim)server1.sfResolveHere("list1");
        int port1 = listener1.sfResolve("listenerPort", 0, true);
        String hostname2 = server2.sfResolve("serverHost", (String)null, true);
        Prim listener2 = (Prim)server2.sfResolveHere("list2");
        int port2 = listener2.sfResolve("listenerPort", 0, true);
        URL url1 = new URL("http", hostname1, port1, "/jetty/index.html");
        URLConnection urlConnection1 = url1.openConnection();
        URL url2 = new URL("http", hostname2, port2, "/jetty/index.html");
        URLConnection urlConnection2 = url2.openConnection();

        BufferedReader in1 = new BufferedReader(
            new InputStreamReader(
            urlConnection1.getInputStream()));
        BufferedReader in2 = new BufferedReader(
            new InputStreamReader(
            urlConnection2.getInputStream()));
        String inputLine1;
        String inputLine2 = null;

        while ((inputLine1 = in1.readLine())!=null&&(inputLine2 = in2.readLine())!=null)
            assertEquals(inputLine1, inputLine2);
        in1.close();
        in2.close();
    }

    public void testCaseTCP21() throws Throwable {
        Prim application = deployExpectingSuccess(FILES+"tcp21.sf", "tcp21");
        assertNotNull(application);
//Prim server = (Prim)application.sfResolveId("server");
        Prim server = (Prim)application.sfResolve("server");
        String jettyhome = server.sfResolve("jettyhome", (String)null, true);
        String filename = jettyhome.concat("\\webapps\\template\\index.html");
        File file = new File(filename);
        File jettyfile = new File(jettyhome);

        assertTrue(file.exists());
        assertTrue(jettyfile.exists());
        assertFalse(file.isDirectory());
        assertTrue(jettyfile.isDirectory());
    }

    public void testCaseTCP22() throws Throwable {
        Prim application = deployExpectingSuccess(FILES+"tcp22.sf", "tcp22");
        assertNotNull(application);
//Prim server = (Prim)application.sfResolveId("adminServer");
        Prim server = (Prim)application.sfResolve("adminServer");
        String hostname = server.sfResolve("httpserverHost", (String)null, true);
        int port = server.sfResolve("listenerPort", 0, true);
        URL url = new URL("http", hostname, port, "/");
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        String expectedmessage = "Unauthorized";
        String actualmessage = urlConnection.getResponseMessage();
        int expectedcode = 401;
        int actualcode = urlConnection.getResponseCode();
        assertEquals(expectedmessage, actualmessage);
        assertEquals(expectedcode, actualcode);
    }
}
