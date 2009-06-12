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
package org.smartfrog.test.system.filesystem.files;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 * Created 21-Apr-2008 16:35:04
 *
 */

public class FilesCompoundTest extends DeployingTestBase {
    public static final String FILES="/org/smartfrog/test/system/filesystem/files/";
    public FilesCompoundTest(String name) {
        super(name);
    }

    public void testFilesCompound() throws Throwable {
        expectSuccessfulTestRun(FILES,"testFilesCompound");
    }

    public void testFilesCompoundBadFilecount() throws Throwable {
        expectSuccessfulTestRun(FILES, "testFilesCompoundBadFilecount");
    }

    public void NotestRecursive() throws Throwable {
        expectSuccessfulTestRun(FILES, "testRecursive");
    }

    public void testFiles() throws Throwable {
        expectSuccessfulTestRun(FILES, "testFiles");
    }
    
    public void testAttributeBinding() throws Throwable {
        expectSuccessfulTestRun(FILES, "testAttributeBinding");
    }

    public void testFilesDuplicatesDropped() throws Throwable {
        expectSuccessfulTestRun(FILES, "testFilesDuplicatesDropped");
    }

    public void testRuntimeAddedAttributes() throws Throwable {
        expectSuccessfulTestRun(FILES, "testRuntimeAddedAttributes");
    }
}
