/**
 * (C) Copyright 2005 Hewlett-Packard Development Company, LP This library is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA For
 * more information: www.smartfrog.org
 */

package org.smartfrog.test.unit.java;

import org.smartfrog.services.os.java.Maven1Policy;
import org.smartfrog.services.os.java.RemoteCachePolicy;

/**
 * Test Maven1 policy logic
 *
 */
public class Maven1RemotePolicyTest extends AbstractRemotePolicyTestBase {

    RemoteCachePolicy createPolicy() throws Exception {
        return  new Maven1Policy();
    }

    public void testExpectedPath() throws Exception {
        String path = policy.createRemotePath(logging);
        String expected = MAVEN1_PATH;
        assertEquals(expected, path);
    }
}
