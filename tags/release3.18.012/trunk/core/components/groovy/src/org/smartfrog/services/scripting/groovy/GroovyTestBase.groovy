/** (C) Copyright 2011 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.scripting.groovy

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.smartfrog.services.assertions.CommonExceptionNames
import org.smartfrog.services.assertions.events.TestCompletedEvent
import org.smartfrog.test.PortChecker
import org.smartfrog.test.SmartFrogTestManager
import org.smartfrog.sfcore.prim.Prim

/**
 * This extends the GroovyTestCase class with support for SmartFrog functional tests
 * -exception names
 * -port checking
 * -delegated test manager
 */
abstract class GroovyTestBase extends GroovyTestCase implements CommonExceptionNames {

    SmartFrogTestManager manager;
    PortChecker portChecker

    @Override protected void setUp() {
        super.setUp()
        Log log = LogFactory.getLog(this.class)
        portChecker = new PortChecker()
        manager = new SmartFrogTestManager(log, this.name)
        manager.setup()
    }

    @Override protected void tearDown() {
        super.tearDown()
        manager.teardown()
    }

    protected void setApplication(Prim application) {
        manager.setApplication(application)
    }


    protected Prim getApplication(Prim application) {
        return manager.getApplication()
    }

    /**
     * Do a test run, assert that it passed and did not skip. The application and eventSink are both saved in member
     * variables, ready for cleanup in teardown
     *
     * @param packageName package containing the deployment
     * @param filename filename (with no .sf extension)
     * @return the test completion event
     */
    public TestCompletedEvent expectSuccessfulTestRun(String packageName, String filename) {
        return manager.expectSuccessfulTestRun(packageName, filename)
    }

    /**
     * Do a test run, assert that it passed or that it skipped. Skipped tests are warned about; there's no way to do
     * anything else with them in JUnit3
     *
     * @param packageName package containing the deployment
     * @param filename filename (with no .sf extension)
     * @return the test completion event
     * @throws Throwable if things go wrong
     */
    public TestCompletedEvent expectSuccessfulTestRunOrSkip(String packageName, String filename) {
        return manager.expectSuccessfulTestRunOrSkip(packageName, filename)
    }


}
