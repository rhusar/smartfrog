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
import org.smartfrog.tools.ant.LocalHost;
import org.smartfrog.tools.ant.test.TaskTestBase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * created 07-Apr-2004 16:28:40
 */

public class ListResourcesTest extends TaskTestBase {

    public ListResourcesTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "listresources.xml";
    }

    public void testEmpty() {
        executeTarget("testEmpty");
    }

    public void testNoProperty() {
        executeTarget("testNoProperty");
    }

    public void testProperty() {
        executeTarget("testProperty");
        assertPropertyContains("testProperty", ".");
    }

    public void testDestFile() {
        executeTarget("testDestFile");
    }
    public void testCSV() {
        executeTarget("testCSV");
        assertPropertyContains("testCSV", "\"listresources\",");
    }
    public void testDirSplitter() {
        executeTarget("testDirSplitter");
        assertPropertyContains("testDirSplitter", "|");
    }
}