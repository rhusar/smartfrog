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


package org.smartfrog.services.hadoop.test.system.local.datanode;

import org.smartfrog.services.hadoop.test.system.local.namenode.HadoopTestBase;

/**
 *
 */
public class FileSystemOperationsTest extends HadoopTestBase {
    public static final String PACKAGE="/org/smartfrog/services/hadoop/test/system/local/datanode/";

    public FileSystemOperationsTest(String name) {
        super(name);
    }

    /**
     * Sets up the fixture,by extracting the hostname and classes dir
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        checkFileSystem();
    }

    public void testClusterShutDownCleanly() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testClusterShutDownCleanly");
        enableFailOnPortCheck();
    }
    
    public void testFileSystemOperations() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testFileSystemOperations");
        enableFailOnPortCheck();
    }

    public void testClusterShutDownCleanly2() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testClusterShutDownCleanly");
        enableFailOnPortCheck();
    }

    public void testFileSystemCopyFileInAndOut() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testFileSystemCopyFileInAndOut");
        enableFailOnPortCheck();
    }

}