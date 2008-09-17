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
package org.smartfrog.test.unit.files;

import junit.framework.TestCase;
import org.smartfrog.services.filesystem.files.FilenamePatternFilter;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

import java.io.File;
import java.io.IOException;

/**
 * Unit tests for the filename filter Created 04-Feb-2008 14:32:27
 */

public class FilenamePatternFilterTest extends TestCase {
    private File tempdir;
    private File t1;
    private File t2;
    private File t3;
    private File t4;
    private File t5;
    private static final String cipattern = "t\\d+.txt";

    /**
     * Constructs a test case with the given name.
     *
     * @param name test name
     */
    public FilenamePatternFilterTest(String name) {
        super(name);
    }


    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        tempdir = File.createTempFile("unittest", "tmp");
        tempdir.delete();
        tempdir.mkdir();
        t1 = tmpfile("t1.txt");
        t2 = tmpfile("t2.txt");
        t3 = tmpfile("T3.txt");
        t4 = tmpfile("t4.tmp");
        t5 = tmpfile(".t5.hidden");
    }

    /**
     * Tears down the fixture, for example, close a network connection. This method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        t5.delete();
        t4.delete();
        t3.delete();
        t2.delete();
        t1.delete();
        tempdir.delete();
    }

    private File tmpfile(String name) throws IOException {
        File f = new File(tempdir, name);
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();
        return f;
    }

    public void testStarDotTxt() throws Throwable {
        assertMatches(3, "\\w*.txt", true, false);
    }

    public void testQueryDotQueryTxt() throws Throwable {
        assertMatches(4, "\\w{2}.\\w{3}", true, false);
    }

    public void testCaseSensitive() throws Throwable {
        assertMatches(2, cipattern, true, false);
    }

    public void testCaseInsensitive() throws Throwable {
        assertMatches(3, cipattern, false, false);
    }

    public void testHidden() throws Throwable {
        assertMatches(1, ".\\w+.hidden", false, true);
    }


    private void assertMatches(int count, String mask, boolean caseSensitive, boolean hidden)
            throws SmartFrogDeploymentException {
        FilenamePatternFilter filter = new FilenamePatternFilter(mask, hidden, caseSensitive);
        File[] files = tempdir.listFiles(filter);
        int filesize = files.length;
        if (filesize != count) {
            String message = "expected " + count + " files but matched " + filesize + " files with the filter" + filter
                    + "\n";
            for (File f : files) {
                message += f.toString() + "\n";
            }
            fail(message);
        }


    }
}
