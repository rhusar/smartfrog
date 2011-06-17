/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.filesystem.append;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 * Created 24-Apr-2008 17:05:16
 *
 */

public class AppendLinesTest extends DeployingTestBase {

    private static final String PACKAGE="/org/smartfrog/test/system/filesystem/append/";

    public AppendLinesTest(String name) {
        super(name);
    }

    public void testAppend() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testAppend");
    }

    public void testDuplicateAppend() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testDuplicateAppend");
    }

    public void testNewAppend() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testNewAppend");
    }
    public void testRepeatedAppend() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testRepeatedAppend");
    }

    public void testRepeatedAppendAllowedOverlap() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testRepeatedAppendAllowedOverlap");
    }

}
