/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.system.filesystem;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;

/**
 * created 21-Jun-2004 17:25:24
 */

public class MkdirTest extends SmartFrogTestBase {

    private static final String FILES = TouchFileTest.FILES;

    public MkdirTest(String name) {
        super(name);
    }

    /**
     * test case
     *
     * @throws Throwable on failure
     */

    public void testWorking() throws Throwable {
        application = deployExpectingSuccess(FILES + "mkdirTestWorking.sf", "mkdirFileTestWorking");
        File file = null;
        try {
            String filename = application.sfResolve("newdir",
                    (String) null,
                    true);
            file = new File(filename);
            //now verify we clean up
            assertIsDirectory(file, filename);
        } finally {
            //cleanup
            if (file != null && file.exists()) {
                String childFiles = listChildFiles(file, "\n");
                if (childFiles.length() > 0) {
                    childFiles = "\nChild files: \n" + childFiles;
                }
                FileSystem.recursiveDelete(file);
                assertFalse("Should be able to clean up after test. Directory remaining: " + file + childFiles,
                        file.exists());
            }
        }
    }

    private String listChildFiles(File dir, String separator) {
        if (dir == null) {
            return "";
        }
        if (!dir.isDirectory()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (File file : dir.listFiles()) {
            result.append(file);
            result.append(separator);
        }
        return result.toString();
    }

    private void assertIsDirectory(File file, String filename) {
        assertTrue("Directory should exist: " + filename, file.exists());
        assertTrue("Should be a directory: " + filename, file.isDirectory());
    }

    public void testCleanOnStartup() throws Throwable {
        String dirname = System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator") + "directory-to-be-cleaned";
        File dir = new File(dirname);

        try {
            if(dir.exists()) {
                FileSystem.recursiveDelete(dir);
            }
            assertFalse("Temp directory should not exist yet", dir.exists());
            assertTrue("Should be able to create a new temporary directory: " + dirname,
                    dir.mkdir());
            File dirInsideDir = new File(dir, "testDir");
            assertTrue("Should be able to create a new directory: " + dirInsideDir,
                    dirInsideDir.mkdir());
            File fileInsideDir = new File(dir, "testFile.txt");
            assertTrue("Should be able to create a new file inside directory: " + fileInsideDir,
                    fileInsideDir.createNewFile());

            application = deployExpectingSuccess
                    (FILES + "mkdirCleanOnStartup.sf", "mkdirCleanOnStartupTest");

            assertIsDirectory(dir, dirname);
            String childFiles = listChildFiles(dir, "\n");
            assertEquals("The directory " + dir + " should be cleaned up and not contain anything, but contains "
                    + childFiles,
                    0, dir.listFiles().length);
        } finally {
            FileSystem.recursiveDelete(dir);
            assertFalse("Should be able to clean up after test. Directory remaining: " + dir,
                    dir.exists());
        }
    }
}
