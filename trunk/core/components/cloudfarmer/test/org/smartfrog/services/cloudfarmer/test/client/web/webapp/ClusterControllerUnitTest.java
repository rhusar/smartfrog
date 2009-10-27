/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.test.client.web.webapp;

import junit.framework.TestCase;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.DynamicSmartFrogClusterController;

/**
 * Created 27-Oct-2009 13:22:28
 */

public class ClusterControllerUnitTest extends TestCase {
    public ClusterControllerUnitTest(String name) {
        super(name);
    }

    String convert(String in) {
        return DynamicSmartFrogClusterController.convertPath(in);
    }
    
    void assertConverts(String expected, String in) {
        String converted = convert(in);
        assertEquals(expected, converted);
    }
    
    public void testEmptyString() throws Throwable {
        assertConverts(DynamicSmartFrogClusterController.FARMER_PATH, "" );
    }

    public void testSpit() throws Throwable {
        assertConverts("a:b","a/b");
    }

    public void testSpit3() throws Throwable {
        assertConverts("a:b:c", "a/b/c");
    }

    public void testColonsUntouched() throws Throwable {
        assertConverts("a:b:c", "a:b:c");
    }

    public void testLeadingTrailingColons() throws Throwable {
        assertConverts("a:b:c", "::a:b:c::");
    }


    public void testLeadingSlash() throws Throwable {
        assertConverts("a:b", "/a/b");
    }

    public void testTrailingSlash() throws Throwable {
        assertConverts("a:b", "/a/b/");
    }

    public void testSlashOnly() throws Throwable {
        assertConverts(DynamicSmartFrogClusterController.FARMER_PATH, "/");
    }

    public void testSlashesOnly() throws Throwable {
        assertConverts(DynamicSmartFrogClusterController.FARMER_PATH, "////");
    }

    public void testColonsOnly() throws Throwable {
        assertConverts(DynamicSmartFrogClusterController.FARMER_PATH, "::::");
    }
}
