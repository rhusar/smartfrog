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

import org.smartfrog.examples.arithnet.Input;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;

/**
 * JUnit test class for test cases for "arithnet" example
 */
public class ArithnetTest
        extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/examples/arithnet/";

    public ArithnetTest(String s) {
        super(s);
    }

    public void testCaseTCP27() throws Throwable {
        application = deployExpectingSuccess(FILES + "example1.sf", "example1");
        assertNotNull(application);
        Prim example = (Prim) application.sfResolveHere("example");
        Prim generator = (Prim) example.sfResolveHere("generator");
        int interval = generator.sfResolve("interval", 0, true);
        int min = generator.sfResolve("min", 0, true);
        int max = generator.sfResolve("max", 0, true);
        assertTrue(interval > 0);
        assertTrue(max > min);
    }

    public void testCaseTCP28() throws Throwable {
        application = deployExpectingSuccess(FILES + "example2.sf", "example2");
        assertNotNull(application);
        Prim example = (Prim) application.sfResolveHere("example");
        Prim printerRemote = (Prim) example.sfResolveHere("printerRemote");
        String host = printerRemote.sfResolve("sfProcessHost", (String) null, true);
        assertEquals(host, "localhost");
        Prim dup = (Prim) example.sfResolveHere("dup");
        Prim outputs = (Prim) dup.sfResolveHere("outputs");
        Prim copy1 = (Prim) outputs.sfResolveHere("copy1");
        Prim copy2 = (Prim) outputs.sfResolveHere("copy2");
        Input to1 = (Input) copy1.sfResolve("to");
        Input to2 = (Input) copy2.sfResolve("to");
        assertNotNull(to1);
        assertNotNull(to2);
    }

    public void testCaseTCP29() throws Throwable {
        application = deployExpectingSuccess(FILES + "example3.sf", "example3");
        assertNotNull(application);
        Prim example = (Prim) application.sfResolveHere("example");
        Prim hostA = (Prim) example.sfResolveHere("hostA");
        Prim six = (Prim) hostA.sfResolveHere("six");
        int constant1 = six.sfResolve("constant", 0, true);
        Prim hostB = (Prim) example.sfResolveHere("hostB");
        Prim five = (Prim) hostB.sfResolveHere("five");
        int constant2 = five.sfResolve("constant", 0, true);
        assertEquals(constant1, 6);
        assertEquals(constant2, 5);
    }

    public void testCaseTCP30() throws Throwable {
        application = deployExpectingSuccess(FILES + "example4.sf", "example4");
        assertNotNull(application);
        Prim example = (Prim) application.sfResolveHere("example");
        String classtype = example.sfResolve("sfClass", (String) null, true);
        assertEquals(classtype, "org.smartfrog.sfcore.workflow.combinators.Sequence");
        Prim case1 = (Prim) example.sfResolveHere("case1");
        String hostG_case1 = case1.sfResolve("hostG", (String) null, true);
        String hostP_case1 = case1.sfResolve("hostP", (String) null, true);
        assertEquals(hostG_case1, "localhost");
        assertEquals(hostP_case1, "localhost");
    }
}
