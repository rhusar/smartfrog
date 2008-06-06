/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
import org.smartfrog.services.filesystem.FileUsingComponent;

import java.io.File;

/**
 * Test text files
 * created 30-Mar-2005 17:38:57
 */

public class TextFileTest extends SmartFrogTestBase {

    public TextFileTest(String name) {
        super(name);
    }

    private static final String FILES = "org/smartfrog/test/system/filesystem/";

    /**
     * test a temp file with some stuff.
     * @throws Throwable on failure
     */
    public void testBasic() throws Throwable {
        application = deployExpectingSuccess(FILES +
                "textFileBasicTest.sf", "textFileBasicTest");
        File file=null;
        try {
            String filename = resolveStringAttribute(application,
                    FileUsingComponent.ATTR_ABSOLUTE_PATH);
            file = new File(filename);
            //verify the state of the file
            assertTrue(file.exists());
            assertTrue(file.length()>10);
            assertLivenessSuccess(application);
            assertStringAttributeExists(application,FileUsingComponent.ATTR_URI);
        } finally {
            //cleanup
            if(file!=null) {
                file.delete();
                assertFalse(file.exists());
            }
        }
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testEncoded() throws Throwable {
        application = deployExpectingSuccess(FILES +
            "textFileEncodingTest.sf", "textFileEncodingTest");
        String filename = resolveStringAttribute(application,
                FileUsingComponent.ATTR_ABSOLUTE_PATH);
        File file = null;
        file = new File(filename);
        //UTF encoded files are that much bigger
        assertEquals(12,file.length());
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testTextFileDirTest() throws Throwable {
        application = deployExpectingSuccess(FILES +
                "textFileDirTest.sf", "textFileDirTest");
        File file = null;
        try {
            String filename = resolveStringAttribute(application,
                    FileUsingComponent.ATTR_ABSOLUTE_PATH);
            file = new File(filename);
            //verify the state of the file
            assertTrue(file.exists());
            assertTrue(file.length() > 10);
            String PARENT_DIR_NAME = "textFileDirTestSubdir";
            assertTrue(filename, file.getParentFile().getName().contains(PARENT_DIR_NAME));
            String expected = File.separator+PARENT_DIR_NAME;
            assertTrue(filename+"does not contain "+expected, filename.contains(expected));
        } finally {
            //cleanup
            if (file != null) {
                file.delete();
                assertFalse(file.exists());
            }
        }
    }

}
