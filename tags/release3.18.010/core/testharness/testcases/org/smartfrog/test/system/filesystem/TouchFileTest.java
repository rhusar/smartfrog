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

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.services.filesystem.TouchFileImpl;
import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.services.filesystem.TouchFileIntf;

import java.io.File;

/**
 * created 18-May-2004 13:29:12
 */

public class TouchFileTest  extends DeployingTestBase {

    public static final String FILES = "org/smartfrog/test/system/filesystem/";

    public TouchFileTest(String name) {
        super(name);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testWorking() throws Throwable {
        application=deployExpectingSuccess(FILES + "testTouchWorking.sf", "testTouchWorking");
    }

    /**
     * set the time then verify that the timestamp was clocked back.
     * @throws Throwable on failure
     */
    public void testTouchSetTime() throws Throwable {
        application = deployExpectingSuccess(FILES +
                "testTouchSetTime.sf", "testTouchSetTime");
        String filename = application.sfResolve(TouchFileIntf.ATTR_FILENAME,
                (String) null,
                true);
        long age = application.sfResolve(TouchFileImpl.ATTR_AGE, (long) 0, true);
        File file = new File(filename);
        assertTrue(file.exists());
        assertEquals(age, file.lastModified());
        terminateApplication();
        assertFalse(file.exists());
    }


    /**
     * test that we are working
     *
     * @throws Throwable on failure
     */
    public void testTouchSubdirs() throws Throwable {
        application = deployExpectingSuccess(FILES +
                "testTouchSubdirs.sf", "testTouchSubdirs");
        File file;
        String filename = application.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH,
                (String) null,
                true);
        file = new File(filename);
        assertTrue("not found " + file, file.exists());
        assertTrue("not a file " + file, file.isFile());
        file.delete();
    }

}
