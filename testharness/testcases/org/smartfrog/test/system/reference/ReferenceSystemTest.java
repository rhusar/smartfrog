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
import java.net.InetAddress;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;


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
                "SmartFrogResolutionException",
                "Possible cause: cyclic reference.");
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

    public void testCaseTCN59() throws Exception {
        deployExpectingException(FILES+"tcn59.sf",
                "tcn59",
                "SmartFrogDeploymentException",
                "unnamed component",
                "SmartFrogResolutionException",
                "error in schema: wrong class found for attribute 'limit', expected: java.lang.Integer, found: java.lang.String");
    }

    public void testCaseTCN60() throws Exception {
        deployExpectingException(FILES+"tcn60.sf",
                "tcn60",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Unresolved Reference: HERE limit");
    }

    public void testCaseTCN61() throws Exception {
        deployExpectingException(FILES+"tcn61.sf",
                "tcn61",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Error: sfResolved int '5' > '0'(maxValue)");
    }

    public void testCaseTCN62() throws Exception {
        deployExpectingException(FILES+"tcn62.sf",
                "tcn62",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Unresolved Reference: HERE integer1");
    }

    public void testCaseTCN63() throws Exception {
        deployExpectingException(FILES+"tcn63.sf",
                "tcn63",
                "SmartFrogLifecycleException",
                "sfDeploy",
                "SmartFrogResolutionException",
                "Unresolved Reference: HERE name1");//@Todo check for "Reference not found";
    }

    public void testCaseTCP23() throws Throwable {
        Vector expected = new Vector();
	    Vector actual = null;
        expected.add("Macgrath");
        expected.add("Hayden");
        expected.add("Ponting");
        application = deployExpectingSuccess(FILES + "tcp23.sf", "tcp23");
        assertNotNull(application);
        actual = application.sfResolve("administrators",actual,true);
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

    public void testCaseTCN96() throws Exception {
        deployExpectingException(FILES+"tcn96.sf",
                "tcn96",
                "SmartFrogDeploymentException",
                "Failed to resolve 'link link'.",
                "SmartFrogResolutionException",
                "Possible cause: cyclic reference.");
    }

    public void testCaseTCN97() throws Exception {
        deployExpectingException(FILES+"tcn97.sf",
                "tcn97",
                "SmartFrogDeploymentException",
                "Failed to resolve 'link PARENT:sfConfig:link'.",
                "SmartFrogResolutionException",
                "Possible cause: cyclic reference.");
    }

	/**
    public void testCaseTCN98() throws Exception {
        deployExpectingException(FILES+"tcn98.sf",
                "tcn98",
                "SmartFrogDeploymentException",
                "[unprintable cyclic value]",
                "SmartFrogResolutionException",
                "Possible cause: cyclic reference.");
    }
	

    public void testCaseTCN99() throws Exception {
        deployExpectingException(FILES+"tcn99.sf",
                "tcn99",
                "SmartFrogDeploymentException",
		"",
		"SmartFrogResolutionException",
                "Possible cause: cyclic reference.");
    }
	**/
}
