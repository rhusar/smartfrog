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


package org.smartfrog.test.system.examples;

import org.smartfrog.test.DeployingTestBase;


/**
 * JUnit test class for test cases related to Function Example 
 *  */

public class FunctionExampleTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/examples/functions/";

    public FunctionExampleTest(String s) {
        super(s);
    }

    public void testCaseFE01() throws Throwable {
        application = deployExpectingSuccess(FILES + "function.sf", "tcFE01");
        assertNotNull(application);
        String actual = (String) (application.sfResolve("message"));
        System.out.println(actual);
        String expected = "hello - here is a constructed message\n" + "value is " + "99" + "\n" + "goodbye\n" +
                "[[elementA, elementB]" + ", " + "Message from outerVector" + ", " + "[value is " + ", " + "99" + "]]";
        assertContains(actual, expected);

    }

}

