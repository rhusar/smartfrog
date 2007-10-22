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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.services.filesystem.FileIntf;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;

/** created 05-Apr-2005 16:41:52 */

public class FileTest extends SmartFrogTestBase {

    public FileTest(String name) {
        super(name);
    }

    private static final String FILES = TouchFileTest.FILES;

    /**
     * test that we are working
     *
     * @throws Throwable
     */
    public void testHomedir() throws Throwable {
        application = deployExpectingSuccess(FILES +
                "testHomedir.sf", "testHomedir");
        File file;
        String filename = application.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH,
                (String) null,
                true);
        file = new File(filename);
        assertTrue(file.exists());
        assertAttributeEquals(application, FileIntf.ATTR_EXISTS, true);
        assertAttributeEquals(application,
                FileIntf.ATTR_IS_DIRECTORY,
                true);
        assertAttributeExists(application, FileIntf.ATTR_IS_HIDDEN);
        assertAttributeExists(application, FileIntf.ATTR_LENGTH);
        assertAttributeExists(application, FileIntf.ATTR_TIMESTAMP);
    }

    /**
     * test a missing file with a parent of another file
     *
     * @throws Throwable
     */
    public void testMissingFile() throws Throwable {
        application = deployExpectingSuccess(FILES +
                "testMissingFile.sf", "testMissingFile");
        File file;
        String filename = application.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH,
                (String) null,
                true);
        file = new File(filename);
        assertFalse(file.exists());
        assertAttributeEquals(application, FileIntf.ATTR_EXISTS, false);
        assertAttributeEquals(application,
                FileIntf.ATTR_IS_DIRECTORY,
                false);
        assertAttributeExists(application, FileIntf.ATTR_IS_HIDDEN);
        assertAttributeExists(application, FileIntf.ATTR_LENGTH);
        assertAttributeExists(application, FileIntf.ATTR_TIMESTAMP);
        assertEquals(0, application.sfResolve(FileIntf.ATTR_LENGTH, 0L, true));
        assertEquals(-1, application.sfResolve(FileIntf.ATTR_TIMESTAMP, 0L, true));
    }


    /**
     * test a missing file with a parent of another file
     *
     * @throws Throwable
     */
    public void testUndeployedFile() throws Throwable {
        deployExpectingException(FILES +
                "testUndeployedFile.sf", "testUndeployedFile",
                EXCEPTION_LIFECYCLE,
                null,
                EXCEPTION_RESOLUTION,
                FileSystem.ERROR_UNDEPLOYED_CD);
    }
}
