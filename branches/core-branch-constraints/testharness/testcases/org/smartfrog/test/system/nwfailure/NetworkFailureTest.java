/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.nwfailure;

import org.smartfrog.test.SmartFrogTestBase;

/**
 * Test network failures.
 * Initially only bad network configurations are done.
 *         created 01-Apr-2004 13:10:30
 */

public class NetworkFailureTest extends SmartFrogTestBase {
    private static final String FILES = "org/smartfrog/test/system/nwfailure/";

    public NetworkFailureTest(String name) {
        super(name);
    }


    public void testUnknownHostTCN50() throws Exception {

        deployExpectingException(FILES + "tcn50.sf",
                "tcn50",
                "SmartFrogDeploymentException",
                "Unknown host: no-such-hostname",
                "java.net.UnknownHostException",
                "no-such-hostname");
    }

    /**
     * This test can fail for different reasons on Java 6 from Java 5, or
     * maybe this is triggered by differences in proxy setup.
     * <pre>
     * java.rmi.ConnectIOException: Exception creating connection to: 192.6.19.80;
     * nested exception is: java.net.NoRouteToHostException: connect timed out: 192.6.19.80
     * </pre>
     * @throws Exception
     */
    public void testConnectionRefusedTCN51() throws Exception {

        Throwable thrown = deployExpectingException(FILES + "tcn51.sf",
                "tcn51",
                "SmartFrogDeploymentException",
                null,
                null,
                null);
        Throwable nested=thrown.getCause();
        assertNotNull("No nested cause",nested);
        String message = nested.getMessage();
        assertNotNull("No nested message", message);
        String faulttype=nested.getClass().getName();
        if("java.rmi.ConnectException".equals(faulttype)) {
            assertContains(message, "Connection refused");
        } else {
            assertEquals("java.rmi.ConnectIOException",faulttype);
        }
    }

    public void testConnectionRefusedTCN51b() throws Exception {

        deployExpectingException(FILES + "tcn51b.sf",
                "tcn51b",
                "SmartFrogDeploymentException",
                "Connection refused to host: 216.239.59.99",
                "java.rmi.ConnectException",
                "Connection refused");
    }
}
