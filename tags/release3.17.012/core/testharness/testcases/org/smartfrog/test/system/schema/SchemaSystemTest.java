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


package org.smartfrog.test.system.schema;

import org.smartfrog.test.SmartFrogTestBase;


/**
 * JUnit test class for test cases related to scehmas.
 */
public class SchemaSystemTest extends SmartFrogTestBase {

    private static final String FILES="org/smartfrog/test/system/schema/";

    public SchemaSystemTest(String s) {
        super(s);
    }


    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCaseTCN43() throws Throwable {
        deployExpectingException(FILES+"tcn43.sf",
                "tcn43",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_LINKRESOLUTION,
		"in: HERE sfConfig:bar:myFoo attribute: schema cause: SmartFrogAssertionResolutionException:: error in schema: wrong class found for attribute 'z (integer attribute to test types and presence)', expected: java.lang.Integer, found: java.lang.String");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCaseTCN44() throws Throwable {
        deployExpectingException(FILES+"tcn44.sf",
                "tcn44",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_LINKRESOLUTION,
                "in: HERE sfConfig:bar:myFoo attribute: schema cause: SmartFrogAssertionResolutionException:: error in schema: non-reference value found for lazy attribute 'a'");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCaseTCN45() throws Throwable {
        deployExpectingException(FILES+"tcn45.sf",
                "tcn45",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_LINKRESOLUTION,
                " non-optional attribute 'y (string attribute to test types and presence)' is missing");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCaseTCN46() throws Throwable {
        deployExpectingException(FILES+"tcn46.sf",
                "tcn46",
                EXCEPTION_DEPLOYMENT,
                null,
                EXCEPTION_LINKRESOLUTION,
                "in: HERE sfConfig:bar:myFoo attribute: schema cause: SmartFrogAssertionResolutionException:: error in schema: wrong class found for attribute 'x (optional integer attribute)', expected: java.lang.Integer, found: java.lang.String");
    }
}
