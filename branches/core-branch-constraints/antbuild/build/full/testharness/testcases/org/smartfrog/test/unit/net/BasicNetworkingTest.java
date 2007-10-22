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
package org.smartfrog.test.unit.net;

import junit.framework.TestCase;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.smartfrog.sfcore.processcompound.*;
import org.smartfrog.sfcore.common.*;

/**
 * created Jul 12, 2004 3:55:32 PM
 */

public class BasicNetworkingTest extends TestCase {

    Logger log;

    public BasicNetworkingTest(String name) {
        super(name);
        log=Logger.getLogger(name);
    }

    public void testHasHostname() throws UnknownHostException {
        InetAddress addr=InetAddress.getLocalHost();
    }

    /**
     * may fail if DNS is badly set up or absent
     * @throws SmartFrogException
     */
    public void testProcessSfDeployedHost() throws SmartFrogException {
        InetAddress addr;
        addr= SFProcess.sfDeployedHost();
        logAddr("SFProcess.sfDeployedHost", addr);
    }

    /**
     * may fail if DNS is badly set up or absent
     * @throws UnknownHostException
     */
    public void testLocalhost() throws UnknownHostException {
        InetAddress addr;
        addr= InetAddress.getByName("localhost");
        logAddr("localhost", addr);
    }

    public void testLoopback() throws UnknownHostException {
        InetAddress addr;
        addr = InetAddress.getByName(null);
        logAddr("loopback", addr);
        assertTrue(addr.isLoopbackAddress());
    }

    public void test127dot0dot0dot1() throws UnknownHostException {
        InetAddress addr;
        addr = InetAddress.getByName("127.0.0.1");
        logAddr("127.0.0.1", addr);
        assertTrue(addr.isLoopbackAddress());
    }


    /**
     * this only valid in an IPv6 context
     * @throws UnknownHostException
     */
    public void testIPv6() throws UnknownHostException {
        InetAddress addr;
        addr = InetAddress.getByName("::1");
        logAddr("IPv6",addr);
        assertTrue(addr.isLoopbackAddress());
    }

    public void testReverseDNSWorking() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String hostname=addr.getHostName();
        logHostname(hostname);
    }

    public void testReverseDNSFullyWorking() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String hostname = addr.getCanonicalHostName();
        logHostname(hostname);
    }

    private void logHostname(String hostname) {
        log.info("hostname =" + hostname);
    }

    private void logAddr(String type, InetAddress addr) {
        log.info(type + ": " + addr.toString());
    }
}
