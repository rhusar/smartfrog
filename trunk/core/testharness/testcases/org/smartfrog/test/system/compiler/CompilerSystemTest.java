/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test.system.compiler;

import org.smartfrog.test.SmartfrogTestBase;



/**
 * JUnit test class for compiler/parser functional tests.
 */
public class CompilerSystemTest extends SmartfrogTestBase {

    private static final String FILES="org/smartfrog/test/system/compiler/";

    public CompilerSystemTest(String s) {
        super(s);
    }


    public void testCaseTCN5() throws Exception {
        deployExpectingException(FILES+"tcn5.sf",
                "tcn5",
                "org.smartfrog.sfcore.common.SmartFrogDeploymentException",
                "failed to deploy",
                "org.smartfrog.sfcore.common.SmartFrogResolutionException",
                "Reference not found");
    }

    public void testCaseTCN6() throws Exception {
        deployExpectingException(FILES + "tcn6.sf",
                "tcn6",
                "SmartFrogCompileResolutionException",
                "failed to deploy");
    }

    public void testCaseTCN7() throws Exception {
        deployExpectingException(FILES + "tcn7.sf",
                "tcn7",
                "SmartFrogLifecycleException",
                "failed to deploy 'p' component",
                "ClassNotFoundException",
                "org.smartfrog.test.system.compiler.PrinterImpl");
    }

    public void testCaseTCN8() throws Exception {
        deployExpectingException(FILES + "tcn8.sf",
                "tcn8",
                "SmartFrogCompileResolutionException",
                "failed to deploy");
    }



    public void testCaseTCN9() throws Exception {
        deployExpectingException(FILES + "tcn9.sf",
                "tcn9",
                "SmartFrogLifecycleException",
                "failed to deploy",
                "java.lang.ClassNotFoundException",
                "Cannot find org.smartfrog.test.system.compiler.PrinterImpl");
    }

    public void testCaseTCN10() throws Exception {
        deployExpectingException(FILES + "tcn10.sf",
                "tcn10",
                "SmartFrogParseException",
                "Encountered \"HOST\" ");
    }
}
