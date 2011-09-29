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

public class Subnet169NotConnectableTest extends SmartFrogTestBase {
    private static final String FILES = "org/smartfrog/test/system/nwfailure/";
    private static final String CONNECTION_REFUSED = "Connection refused";
    private static final String CONNECT_IOEXCEPTION = "java.rmi.ConnectIOException";
    private static final String EXCEPTION_CREATING_CONNECTION_TO = "Exception creating connection to";
    private static final String SMARTFROG_DEPLOYMENT_EXCEPTION = "SmartFrogDeploymentException";
    private static final String JAVA_RMI_CONNECT_EXCEPTION = "java.rmi.ConnectException";

    public Subnet169NotConnectableTest(String name) {
        super(name);
    }


    /**
     * test case
     * @throws Throwable on failure
     */

    public void testConnectionRefusedTCN51b() throws Throwable {

        Throwable thrown = deployExpectingException(FILES + "tcn51b.sf",
                "tcn51b",
                SMARTFROG_DEPLOYMENT_EXCEPTION,
                null,
                null,
                null);
        String message=thrown.getMessage();
        assertNotNull("No nested message", message);
        assertTrue("Did not find " + EXCEPTION_CREATING_CONNECTION_TO + " or " + CONNECTION_REFUSED
                + " in " + message,
                message.contains(EXCEPTION_CREATING_CONNECTION_TO)
                        || message.contains(CONNECTION_REFUSED));

        Throwable nested = thrown.getCause();
        assertNotNull("No nested cause", nested);
        message = nested.getMessage();
        assertNotNull("No nested message", message);
        String faulttype = nested.getClass().getName();
        if (JAVA_RMI_CONNECT_EXCEPTION.equals(faulttype)) {
            assertContains(message, CONNECTION_REFUSED);
        } else {
            assertEquals(CONNECT_IOEXCEPTION, faulttype);
        }
    }
}
