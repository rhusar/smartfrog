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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;

/**
 * JUnit test class for test cases related to Spawn Example
 */
public class SpawnExampleTest
        extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/examples/spawn/";

    public SpawnExampleTest(String s) {
        super(s);
    }

    public void testCaseSE01() throws Throwable {

        application = deployExpectingSuccess(FILES + "example.sf", "tcSE01");
        assertNotNull(application);

        String actualSfClass = (String) application.sfResolveHere("sfClass");
        assertEquals("org.smartfrog.sfcore.compound.CompoundImpl", actualSfClass);

        Prim nest = (Prim) application.sfResolveHere("nest");
        String actualNestSfClass = (String) nest.sfResolveHere("sfClass");
        assertEquals("org.smartfrog.sfcore.compound.CompoundImpl", actualNestSfClass);

        // check the 5 counters
        Prim littleCuckoo0 = (Prim) nest.sfResolveHere("littleCuckoo0");
        assertNotNull(littleCuckoo0);
        Prim littleCuckoo1 = (Prim) nest.sfResolveHere("littleCuckoo1");
        assertNotNull(littleCuckoo1);
        Prim littleCuckoo2 = (Prim) nest.sfResolveHere("littleCuckoo2");
        assertNotNull(littleCuckoo2);
        Prim littleCuckoo3 = (Prim) nest.sfResolveHere("littleCuckoo3");
        assertNotNull(littleCuckoo3);
        Prim littleCuckoo4 = (Prim) nest.sfResolveHere("littleCuckoo4");
        assertNotNull(littleCuckoo4);


        Prim spawn = (Prim) application.sfResolveHere("spawn");
        String actualspawnSfClass = (String) spawn.sfResolveHere("sfClass");
        assertEquals("org.smartfrog.examples.spawn.Spawn", actualspawnSfClass);
        String actualsfOffspringName = (String) spawn.sfResolveHere("sfOffspringName");
        assertEquals("myBaby", actualsfOffspringName);
        //check the 5 counters
        Prim myBaby0 = (Prim) spawn.sfResolveHere("myBaby0");
        assertNotNull(myBaby0);
        Prim myBaby1 = (Prim) spawn.sfResolveHere("myBaby1");
        assertNotNull(myBaby1);
        Prim myBaby2 = (Prim) spawn.sfResolveHere("myBaby2");
        assertNotNull(myBaby2);
        Prim myBaby3 = (Prim) spawn.sfResolveHere("myBaby3");
        assertNotNull(myBaby3);
        Prim myBaby4 = (Prim) spawn.sfResolveHere("myBaby4");
        assertNotNull(myBaby4);


        Prim cuckoo = (Prim) application.sfResolveHere("cuckoo");
        String actualcuckooSfClass = (String) cuckoo.sfResolveHere("sfClass");
        assertEquals("org.smartfrog.examples.spawn.Spawn", actualcuckooSfClass);
        String actualCuckooOffspringName = (String) cuckoo.sfResolveHere("sfOffspringName");
        assertEquals("littleCuckoo", actualCuckooOffspringName);
        ComponentDescription cd = null;
        cd = cuckoo.sfResolve("sfOffspringDescription", cd, true);
        String actual = cd.toString();
        assertNotNull(cd);
        String expected = "limit 3";
        assertContains(actual, expected);


    }

}

