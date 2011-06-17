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
package org.smartfrog.test.system.reference.constant;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 * Created 22-Nov-2007 14:56:55
 *
 */

public class ConstantReferenceTest extends DeployingTestBase {
    private static final String FILES = "org/smartfrog/test/system/reference/constant";
    public static final String PUBLIC_STATIC_STRING="PUBLIC_STATIC_STRING";

    //this is used in tests; do not delete.
    private static final String PRIVATE_STATIC_STRING = "PRIVATE_STATIC_STRING";
    public final String PUBLIC_STRING = "PRIVATE_STRING";

    public ConstantReferenceTest(String name) {
        super(name);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testResolveConstant() throws Throwable {
        expectSuccessfulTestRun(FILES, "testResolveConstant");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testBadClass() throws Throwable {
        expectSuccessfulTestRun(FILES,"testBadClass");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testBadField() throws Throwable {
        expectSuccessfulTestRun(FILES, "testBadField");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testNotStatic() throws Throwable {
        expectSuccessfulTestRun(FILES, "testNotStatic");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testIllegalAccess() throws Throwable {
        expectSuccessfulTestRun(FILES, "testIllegalAccess");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testTooShort() throws Throwable {
        expectSuccessfulTestRun(FILES, "testTooShort");
    }



}
