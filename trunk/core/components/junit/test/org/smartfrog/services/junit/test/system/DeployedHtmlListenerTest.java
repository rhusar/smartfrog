/* (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.junit.test.system;

import org.smartfrog.services.xunit.base.TestRunner;
import org.smartfrog.services.xunit.listeners.html.HtmlTestListenerFactory;

import java.io.File;

/** created Nov 22, 2004 4:31:45 PM */

public class DeployedHtmlListenerTest extends TestRunnerTestBase {
    public static final String TEST_SUITE_COMPONENT_NAME = "tests";
    public static final String SUITENAME = "tests";

    public DeployedHtmlListenerTest(String name) {
        super(name);
    }

    public void testAll() throws Throwable {
        executeTestFile("html-all");
        TestRunner runner = getApplicationAsTestRunner();

        HtmlTestListenerFactory listenerFactory = null;
        listenerFactory =
                (HtmlTestListenerFactory) application.sfResolve(
                        TestRunner.ATTR_LISTENER,
                        listenerFactory,
                        true);
        assertNotNull(listenerFactory);
        String outputFilename = listenerFactory.lookupFilename("localhost", TEST_SUITE_COMPONENT_NAME);
        File xmlfile = new File(outputFilename);
        assertTrue("File " + outputFilename + " not found", xmlfile.exists());
        getLog().info("Output file: " + xmlfile);
        //validate the file
        validateXmlLog(xmlfile);
    }

}
