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


package org.smartfrog.test.system.reference;

import java.util.Vector;
import java.util.Calendar;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;


/**
 * JUnit class for negative test cases using attribute resolution 
 * functionality provided by SmartFrog framework.
 */
public class ReferenceSystemTest extends SmartFrogTestBase {

    private static final String FILES="org/smartfrog/test/system/reference/";

    public ReferenceSystemTest(String s) {
        super(s);
    }


    public void testCaseTCN19() throws Exception {
        deployExpectingException(FILES+"tcn19.sf",
                "tcn19",
                "SmartFrogDeploymentException",
                "Failed to resolve 'attr ",
                "SmartFrogCompileResolutionException",
                "java.lang.StackOverflowError");
    }
    
    public void testCaseTCN39() throws Exception {
        deployExpectingException(FILES + "tcn39.sf",
                "tcn39",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Illegal ClassType");
    }
    
    public void testCaseTCN40() throws Exception {
        deployExpectingException(FILES + "tcn40.sf",
                "tcn40",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Illegal ClassType");
    }
    public void testCaseTCN41() throws Exception {
        deployExpectingException(FILES + "tcn41.sf",
                "tcn41",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Error: sfResolved int '10' < '12'(minValue)");
    }
    
    public void testCaseTCN42() throws Exception {
        deployExpectingException(FILES + "tcn42.sf",
                "tcn42",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Error: sfResolved int '15' > '9'(maxValue)");
    }
}
