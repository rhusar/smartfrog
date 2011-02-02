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
package org.smartfrog.test.unit.java;

import org.smartfrog.services.os.java.LocalCachePolicy;
import org.smartfrog.services.os.java.FlattenLocalFilesPolicy;

/**
 */
public class FlattenLocalFilesPolicyTest extends AbstractLocalPolicyTestBase {

    public FlattenLocalFilesPolicyTest() {
    }

    LocalCachePolicy createPolicy() throws Exception {
        return new FlattenLocalFilesPolicy();
    }

    public void testExpectedPath() throws Exception {
        String path = createLoggingPath();
        String expected = COMMONS_LOGGING_JAR;
        assertEquals(expected,path);
    }

}
