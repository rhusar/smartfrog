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

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;

/**
 * JUnit test class for test cases for "workflow" example
 */
public class WorkFlowTest
    extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/examples/workflow/";

    public WorkFlowTest(String s) {
        super(s);
    }

    public void testCaseTCP27() throws Throwable {

        application = deployExpectingSuccess(FILES+"system.sf", "system");
        String applicationName = "TCP27.system.tempname";
        String diag ="";
        Prim h1 = null;
        try {
          assertNotNull(application);
          applicationName = application.sfCompleteName().toString();

          diag = (((Prim)application).sfDiagnosticsReport()).toString();
          h1 = (Prim)application.sfResolveHere("h1");
        } catch (Exception ex) {
            throw new SmartFrogResolutionException ("Failed TCP27. Could not find H1 in "+applicationName +"\n "+diag,ex);
        }
        ComponentDescription cd = null;
        cd = h1.sfResolve("nodeAction", cd, true);
        String actual = cd.toString();
        String expected1 = "message \"copied file from http://codeserver/webServerCode to file /tmp/default\";";
        String expected2 = "message \"copied file from http://codeserver/appServerCode to file /tmp/default\";";
        String expected3 = "message \"file /tmp/default has been removed\";";
        assertNotNull(cd);
        assertContains(actual,expected1);
        assertContains(actual,expected2);
        assertContains(actual,expected3);
        
    }

}
