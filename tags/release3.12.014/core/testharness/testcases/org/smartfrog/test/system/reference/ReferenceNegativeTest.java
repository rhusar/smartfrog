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

import org.smartfrog.test.SmartFrogTestBase;


/**
 * JUnit class for negative test cases using attribute resolution functionality provided by SmartFrog framework.
 */
public class ReferenceNegativeTest extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/reference/";

    public ReferenceNegativeTest(String s) {
        super(s);
    }



    public void testCaseTCN39() throws Exception {
        deployExpectingException(FILES + "tcn39.sf",
                "tcn39",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                "Illegal ClassType");
    }

    public void testCaseTCN40() throws Exception {
        deployExpectingException(FILES + "tcn40.sf",
                "tcn40",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                "Illegal ClassType");
    }

    public void testCaseTCN41() throws Exception {
        deployExpectingException(FILES + "tcn41.sf",
                "tcn41",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                "Error: sfResolved int '10' < '12'(minValue)");
    }

    public void testCaseTCN42() throws Exception {
        deployExpectingException(FILES + "tcn42.sf",
                "tcn42",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                "Error: sfResolved int '15' > '9'(maxValue)");
    }

    public void testCaseTCN59() throws Exception {
        deployExpectingException(FILES + "tcn59.sf",
                "tcn59",
                EXCEPTION_DEPLOYMENT,
                "unnamed component",
                EXCEPTION_LINKRESOLUTION,
                "error in schema: wrong class found for attribute 'limit', expected: java.lang.Integer, found: java.lang.String");
    }

    public void testCaseTCN60() throws Exception {
        deployExpectingException(FILES + "tcn60.sf",
                "tcn60",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                "Unresolved Reference: HERE limit");
    }

    public void testCaseTCN61() throws Exception {
        deployExpectingException(FILES + "tcn61.sf",
                "tcn61",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                "Error: sfResolved int '5' > '0'(maxValue)");
    }

    public void testCaseTCN62() throws Exception {
        deployExpectingException(FILES + "tcn62.sf",
                "tcn62",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                "Unresolved Reference: HERE integer1");
    }

    public void testCaseTCN63() throws Exception {
        deployExpectingException(FILES + "tcn63.sf",
                "tcn63",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                "Unresolved Reference: HERE name1");//@Todo check for "Reference not found";
    }


}