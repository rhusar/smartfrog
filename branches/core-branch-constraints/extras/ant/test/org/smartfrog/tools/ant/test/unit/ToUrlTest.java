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
package org.smartfrog.tools.ant.test.unit;

import org.smartfrog.tools.ant.ToUrlTask;
import org.smartfrog.tools.ant.test.TaskTestBase;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

/**
 *         created 07-Apr-2004 16:28:40
 */

public class ToUrlTest extends TaskTestBase {

    public ToUrlTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "to-url.xml";
    }

    public void testEmpty() {
        expectBuildExceptionContaining("testEmpty","missing property","property");
    }

    public void testNoProperty() {
        expectBuildExceptionContaining("testNoProperty", "missing property", "property");
    }

    public void testNoFile() {
        expectBuildExceptionContaining("testNoFile", "missing file", "file");
    }
    public void testValidation() {
        expectBuildExceptionContaining("testValidation", ToUrlTask.ERROR_MISSING_FILE, "file");
    }

    public void testWorks() {
        executeTarget("testWorks");
        assertPropertyContains("testWorks","file:");
        assertPropertyContains("testWorks", "/foo");
    }

    public void testIllegalChars() {
        executeTarget("testIllegalChars");
        assertPropertyContains("testIllegalChars", "file:");
        assertPropertyContains("testIllegalChars", "fo%20o%25");
    }

    /**
     * test that we can round trip by opening a url that exists
     * @throws IOException
     */
    public void testRoundTrip() throws IOException {
        executeTarget("testRoundTrip");
        assertPropertyContains("testRoundTrip", "file:");
        String property=getProperty("testRoundTrip");
        URL url=new URL(property);
        InputStream instream=url.openStream();
        instream.close();
    }

    public void testIllegalCombinations() {
        executeTarget("testIllegalCombinations");
        assertPropertyContains("testIllegalCombinations", "/foo");
        assertPropertyContains("testIllegalCombinations", ".xml");
    }

    public void testFileset() {
        executeTarget("testFileset");
        assertPropertyContains("testFileset", ".xml ");
        String result = getProperty("testFileset");
        assertPropertyEndsWith("testFileset", ".xml");
    }

    public void testFilesetSeparator() {
        executeTarget("testFilesetSeparator");
        assertPropertyContains("testFilesetSeparator", ".xml\",\"");
        assertPropertyEndsWith("testFilesetSeparator", ".xml");
    }

    public void testPath() {
        executeTarget("testPath");
        assertPropertyContains("testPath", "to-url.xml");
    }

    /**
     * assert that a property ends with a value
     * @param property property name
     * @param ending ending
     */
    private void assertPropertyEndsWith(String property, String ending) {
        String result = getProperty(property);
        String substring = result.substring(result.length() - ending.length());
        assertEquals(ending, substring);
    }
}
