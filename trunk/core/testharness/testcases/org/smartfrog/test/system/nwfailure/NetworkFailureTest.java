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
                "SmartFrogException",
                "Unknown host: no-such-hostname",
                "java.net.UnknownHostException",
                "no-such-hostname");
    }

    public void testConnectionRefusedTCN51() throws Exception {

        deployExpectingException(FILES + "tcn51.sf",
                "tcn51",
                "SmartFrogException",
                "Connection refused",
                "java.rmi.ConnectException",
                "Connection refused");

    }

    public void testConnectionRefusedTCN51b() throws Exception {

        deployExpectingException(FILES + "tcn51b.sf",
                "tcn51b",
                "SmartFrogException",
                "Connection refused to host: 216.239.59.99",
                "java.rmi.ConnectException",
                "Connection refused");

    }
}
