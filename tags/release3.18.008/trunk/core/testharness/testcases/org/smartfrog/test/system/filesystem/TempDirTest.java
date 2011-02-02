/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;

/**
 * created 24-Apr-2006 18:12:16
 */

public class TempDirTest extends SmartFrogTestBase {

    public TempDirTest(String name) {
        super(name);
    }

    private static final String FILES = "org/smartfrog/test/system/filesystem/";

    /**
     * test that we are working
     *
     * @throws Throwable
     */
    public void testTempDirWorking() throws Throwable {
        application = deployExpectingSuccess(FILES + "tempDirTestWorking.sf", "tempDirTestWorking");
        File dir=null;
        File child=null;
        try {
            Prim temp1=application.sfResolve("temp1",(Prim)null,true);
            String absolutePath = temp1.sfResolve("absolutePath", (String) null, true);
            dir = new File(absolutePath);
            assertTrue("Not found: "+dir,dir.exists());
            assertTrue("Not a directory "+dir,dir.exists());
            child = new File(dir,"child");
            child.setLastModified(0);
        } finally {
            terminateApplication();
        }
        assertFalse("child Not deleted " + child, child.exists());
        assertFalse("Dir Not deleted " + dir,dir.exists());
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testTempDirBadPrefix() throws Throwable {
        deployExpectingException(FILES + "tempDirBadPrefix.sf",
                "tempDirBadPrefix",
                "SmartFrogLifecycleException",null,
                "SmartFrogException",
                "prefix=o");
    }

    /**
     * test that we are working
     *
     * @throws Throwable on failure
     */
    public void testTempDirEmptySuffix() throws Throwable {
        application = deployExpectingSuccess(FILES + "tempDirTestEmptySuffix.sf", "tempDirTestEmptySuffix");
        File dir = null;
        try {
            Prim temp1 = application.sfResolve("temp1", (Prim) null, true);
            String absolutePath = temp1.sfResolve("absolutePath", (String) null, true);
            dir = new File(absolutePath);
            assertTrue("Not found: " + dir, dir.exists());
            assertTrue("Not a directory " + dir, dir.exists());
        } finally {
            terminateApplication();
        }
        assertFalse("Not deleted " + dir, dir.exists());
    }


    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCreateFileTempDir() throws Throwable {
        File tempFile = FileSystem.createTempFile("abcd", ".e", null);
        assertTrue(tempFile.exists());
        tempFile.delete();
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCreateFileInDir() throws Throwable {
        File tempFile = FileSystem.createTempFile("abcd", ".e", System.getProperty("java.io.tmpdir"));
        assertTrue(tempFile.exists());
        tempFile.delete();
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCreateTempDir() throws Throwable {
        File tempFile = FileSystem.createTempDir("abcd", ".e", null);
        assertTrue(tempFile.isDirectory());
        tempFile.delete();
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testCreateTempDirInDir() throws Throwable {
        File tempFile = FileSystem.createTempDir("abcd", ".e", System.getProperty("java.io.tmpdir"));
        assertTrue(tempFile.isDirectory());
        tempFile.delete();
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testBadPrefix() throws Throwable {
        try {
            File tempFile = FileSystem.createTempFile("", ".txt", null);
            //should not be reached, but just in case
            tempFile.delete();
        } catch (SmartFrogException e) {
            //expected
        }
    }
}
