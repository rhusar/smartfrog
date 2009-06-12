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


package org.smartfrog.test.system.services.scripting.js;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class JavascriptTest extends DeployingTestBase {

    public static final String PACKAGE = "/org/smartfrog/test/system/services/scripting/js/";

    public JavascriptTest(String name) {
        super(name);
    }

    public void testInlineLifecycle() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testInlineLifecycle");
    }

    public void testInlineLifecycleTerminating() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testInlineLifecycleTerminating");
    }

    public void testResourceTerminating() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testResourceTerminating");
    }

    public void testConditionTrue() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testConditionTrue");
    }

    public void testConditionFalse() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testConditionFalse");
    }

    public void testFailOnStartup() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testFailOnStartup");
    }

    public void testConditionScriptError() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testConditionScriptError");
    }
}
