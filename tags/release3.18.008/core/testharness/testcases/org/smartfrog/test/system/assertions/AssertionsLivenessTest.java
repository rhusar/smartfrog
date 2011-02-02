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


package org.smartfrog.test.system.assertions;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.assertions.SmartFrogAssertionException;

/**
 * Date: 30-Apr-2004
 * Time: 22:03:23
 */
public class AssertionsLivenessTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/assertions/";

    public AssertionsLivenessTest(String name) {
        super(name);
    }



    /**
     //Todo: turn on once we have a way of expecting liveness faults.
     * @throws Throwable on failure
     */
    public void testFalseIsLazyTrue() throws Throwable {
        deployExpectingAssertionFailure(FILES + "testFalseIsLazyTrue.sf", "testFalseIsLazyTrue");
    }


}