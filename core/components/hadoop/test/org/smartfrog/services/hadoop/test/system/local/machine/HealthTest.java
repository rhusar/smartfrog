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


package org.smartfrog.services.hadoop.test.system.local.machine;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class HealthTest extends DeployingTestBase {
    public static final String PACKAGE="/org/smartfrog/services/hadoop/test/system/local/machine/";

    public HealthTest(String name) {
        super(name);
    }

    public void testCheckDatanodeJspClass() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testCheckDatanodeJspClass");
    }

    public void testCheckNamenodeJspClass() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testCheckNamenodeJspClass");
    }

    public void testCheckJobtrackerJspClass() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testCheckJobtrackerJspClass");
    }

    public void testCheckJasperClasses() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testCheckJasperClasses");
    }

    public void testHadoopJspClasses() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testHadoopJspClasses");
    }

    public void testJvmHealth() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testJvmHealth");
    }
}