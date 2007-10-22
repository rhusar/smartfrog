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
package org.smartfrog.test.unit.sfcore;

import junit.framework.TestCase;
import org.smartfrog.sfcore.utils.PlatformHelper;

/**
 * created 27-May-2004 15:41:17
 */

public class PlatformHelperTest extends TestCase {

    private static final String unixPath="/home/someone/file.txt";
    private static final String unixInDos = "\\home\\someone\\file.txt";
    private static final String dosPath="c:\\home\\someone\\file.txt";
    private static final String dosInUnix = "c:/home/someone/file.txt";
    private static final String mutantPath="c:/home/someone\\file.txt";
    private PlatformHelper local=PlatformHelper.getLocalPlatform();
    private PlatformHelper unix=PlatformHelper.getUnixPlatform();
    private PlatformHelper dos=PlatformHelper.getDosPlatform();


    public PlatformHelperTest(String name) {
        super(name);
    }

    public void testLocal() {
        String userDir=System.getProperty("user.dir");
        assertUnchanged(local,userDir);
        assertUnchanged(local, ".");
    }

    private void assertUnchanged(PlatformHelper helper,String userDir) {
        String converted=helper.convertFilename(userDir);
        assertEquals(userDir,converted);
    }

    public void testUnixPath() {
        assertUnchanged(unix,unixPath);
    }
    public void testDosPath() {
        assertUnchanged(dos, dosPath);
    }

    public void testDos2Unix() {
        String converted = unix.convertFilename(dosPath);
        assertEquals(dosInUnix,converted);
    }

    public void testUnix2Dos() {
        String converted = dos.convertFilename(unixPath);
        assertEquals(unixInDos, converted);
    }

    public void testMutant1() {
        assertEquals(dosPath,dos.convertFilename(mutantPath));
    }

    public void testMutant2() {
        assertEquals(dosInUnix, unix.convertFilename(mutantPath));
    }

}
