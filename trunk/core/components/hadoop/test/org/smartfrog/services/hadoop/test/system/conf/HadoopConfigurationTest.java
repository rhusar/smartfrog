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
package org.smartfrog.services.hadoop.test.system.conf;

import org.smartfrog.test.DeployingTestBase;

/**
 * Created 14-Jan-2009 15:49:56
 */

public class HadoopConfigurationTest extends DeployingTestBase {
    public static final String PACKAGE = "/org/smartfrog/services/hadoop/test/system/conf/";

    public HadoopConfigurationTest(String name) {
        super(name);
    }

    public void testDefaultConfValues() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testDefaultConfValues");
    }

    public void testOverride() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testOverride");
    }

    public void testEmptyConf() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testEmptyConf");
    }
    public void testRequiredAttributes() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testRequiredAttributes");
    }

    public void testManagedConfTest() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testManagedConfTest");
    }
}
