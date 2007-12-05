/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.deploy;

import org.smartfrog.test.SmartFrogTestBase;

/**
 * JUnit test class for test cases related to life cycle method "sfDeploy"
 */
public class DeployTCN1Test extends SmartFrogTestBase {

    private static final String FILES="org/smartfrog/test/system/deploy/";

    public DeployTCN1Test(String s) {
        super(s);
    }

    public void testCaseTCN1() throws Exception {
        deployExpectingException(FILES+"tcn1.sf",
                "tcn1",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Reference not found");
    }


    public void testCaseTCN1b() throws Exception {
        deployExpectingException(FILES + "tcn1b.sf",
                "tcn1b",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Reference not found");
    }

    public void testCaseTCN1c() throws Exception {
        deployExpectingException(FILES + "tcn1c.sf",
                "tcn1c",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Reference not found");
    }
}
