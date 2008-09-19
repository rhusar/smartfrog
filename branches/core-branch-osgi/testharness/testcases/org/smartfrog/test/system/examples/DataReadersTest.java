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


package org.smartfrog.test.system.examples;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;

/**
 * JUnit test class for test cases for "datareaders" example
 */
public class DataReadersTest
    extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/examples/datareaders/";

    public DataReadersTest(String s) {
        super(s);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCaseTCP31() throws Throwable {
        application = deployExpectingSuccess(FILES+"dataProvider.sf", "provider");
        assertNotNull(application);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCaseTCP32() throws Throwable {
        application = deployExpectingSuccess(FILES + "dataConsumer.sf", "consumer");
        assertNotNull(application);
        Prim event = (Prim) application.sfResolveHere("event");
        Prim event2 = (Prim) application.sfResolveHere("event2");
        Prim event3 = (Prim) application.sfResolveHere("event3");
        String message = event.sfResolve("message", (String) null, true);
        String message2 = event2.sfResolve("message", (String) null, true);
        String message3 = event3.sfResolve("message", (String) null, true);
        assertEquals("hello world! (data attribute)", message);
        assertEquals("PRIM hello world! (component attribute)", message2);
        assertEquals("PRIM2 hello World!", message3);
    }
}
