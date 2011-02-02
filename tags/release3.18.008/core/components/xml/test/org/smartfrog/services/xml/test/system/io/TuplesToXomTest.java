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
package org.smartfrog.services.xml.test.system.io;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 * Created 10-Mar-2008 16:52:14
 *
 */

public class TuplesToXomTest extends DeployingTestBase {

    public static final String PACKAGE="/org/smartfrog/services/xml/test/system/io/";

    public TuplesToXomTest(String name) {
        super(name);
    }

    public void testCSVtoXom() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testCSVtoXom");

    }
    public void testNamespaceXom() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testNamespaceXom");
    }

    public void testTrimmedXom() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testTrimmedXom");
    }



}
