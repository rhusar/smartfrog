/** (C) Copyright 2004-2007 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test.system.reference;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.SmartFrogTestBase;

import java.net.InetAddress;
import java.util.Vector;


/**
 * JUnit class for negative test cases using attribute resolution functionality provided by SmartFrog framework.
 */
public class ReferencePositiveTest extends SmartFrogTestBase {

    public static final String FILES = "org/smartfrog/test/system/reference/";

    public ReferencePositiveTest(String s) {
        super(s);
    }

    public void testCaseTCP23() throws Throwable {
        Vector expected = new Vector();
        Vector actual = null;
        expected.add("Macgrath");
        expected.add("Hayden");
        expected.add("Ponting");
        application = deployExpectingSuccess(FILES + "tcp23.sf", "tcp23");
        assertNotNull(application);
        actual = application.sfResolve("administrators", actual, true);
        assertNotNull("Did not find the value", actual);
        assertEquals(expected, actual);
    }

    public void testCaseTCP24() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp24.sf", "tcp24");
        assertNotNull(application);
        Prim component1 = null;
        int actual = 0;
        int expected = 5;
        component1 = application.sfResolve("component1", component1, true);
        assertNotNull(component1);
        actual = component1.sfResolve("limit", actual, true);
        assertEquals(expected, actual);
    }

    public void testCaseTCP25() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp25.sf", "tcp25");
        assertNotNull(application);
        InetAddress address = null;
        address = application.sfResolve("address", address, true);
        assertNotNull(address);
    }

    public void testCaseTCP26() throws Throwable {
        application = deployExpectingSuccess(FILES + "tcp26.sf", "tcp26");
        assertNotNull(application);
        Prim spawn = null;
        String expected = "limit 3";
        ComponentDescription cd = null;
        spawn = application.sfResolve("spawn", spawn, true);
        assertNotNull(spawn);
        cd = spawn.sfResolve("sfOffspringDescription", cd, true);
        String actual = cd.toString();
        assertNotNull(cd);
        assertContains(actual, expected);
    }


}