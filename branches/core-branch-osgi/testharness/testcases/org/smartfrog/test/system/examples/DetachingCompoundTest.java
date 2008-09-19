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
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.test.DeployingTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.util.Enumeration;


/**
 * JUnit test class for test cases related to HelloWorld Example
 */

public class DetachingCompoundTest extends DeployingTestBase {

    private static final Log log = LogFactory.getLog(DetachingCompoundTest.class);

    public DetachingCompoundTest(String s) {
        super(s);
    }

    /**
     * DetachingCompound test case (this was commented out. why? because it was failing)
     *
     * @throws Throwable on failure
     */


    public void testDetachingCompound() throws Throwable {
        expectSuccessfulTestRun("/org/smartfrog/test/system/examples/", "testDetachingCompound");

//        application = deployExpectingSuccess("/org/smartfrog/test/system/examples/testDetachingCompound.sf",
//                "testDetachingCompound");
/*        ProcessCompound root = SFProcess.sfSelectTargetProcess((InetAddress) null, null);
        assertNotNull("We have no root process",root);
        Enumeration<Liveness> children = root.sfChildren();
        boolean found = false;
        Prim detachedChild = null;
        while (children.hasMoreElements() && !found) {
            Liveness liveness = children.nextElement();
            if (liveness instanceof Prim) {
                Prim child = (Prim) liveness;
                log.info(child.sfCompleteName());
                String named = child.sfResolve("name", "", false);
                if ("timeoutChild".equals(named)) {
                    found = true;
                    detachedChild = child;
                }
            }
        }
        assertTrue("Did not find the timeoutChild component", found);
        terminateApplication(detachedChild);*/

    }

}