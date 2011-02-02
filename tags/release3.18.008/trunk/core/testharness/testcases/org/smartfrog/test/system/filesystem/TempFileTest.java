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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.filesystem.TempFileImpl;
import org.smartfrog.services.filesystem.TempFile;

import java.io.File;

/**
 * created 18-May-2004 13:28:43
 */

public class TempFileTest extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/filesystem/";

    public TempFileTest(String name) {
        super(name);
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testEmpty() throws Throwable {
        deployExpectingException(FILES + "tempFileTestEmpty.sf",
                "tempFileTestEmpty",
                        EXCEPTION_LIFECYCLE, null,
                        EXCEPTION_SMARTFROG,
                    TempFileImpl.ERROR_PREFIX_EMPTY);
    }

    /**
     * test that we are working
     * @throws Throwable on failure
     */
    public void testWorking() throws Throwable {
        application=deployExpectingSuccess(FILES + "tempFileTestWorking.sf", "tempFileTestWorking");
        File file;
        try {
            String filename = application.sfResolve(TempFile.ATTR_FILENAME, (String) null, true);
            String suffix = application.sfResolve(TempFile.ATTR_SUFFIX, (String) null, true);
            String prefix = application.sfResolve(TempFile.ATTR_PREFIX, (String) null, true);
            file = new File(filename);
            assertTrue("found " + suffix + " in " + filename, filename.endsWith(suffix));
            assertTrue("found "+prefix+" in "+filename,file.getName().indexOf(prefix)==0);
            //now verify we clean up
            assertTrue(file.exists());
            Prim temp1 = application.sfResolve("temp1", (Prim) null, true);
            String absolutePath = temp1.sfResolve("absolutePath", (String) null, true);
        } finally {
            terminateApplication();
        }
        assertFalse(file.exists());
    }

}
