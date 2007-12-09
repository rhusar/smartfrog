/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.junit.test.unit;

import org.smartfrog.services.junit.test.system.TestRunnerTestBase;
import org.smartfrog.services.xunit.listeners.xml.OneHostXMLListener;
import org.smartfrog.services.xunit.serial.TestInfo;

import java.io.File;
import java.io.IOException;
import java.util.Date;


/**
 * Test the {@link OneHostXMLListener} class, that is not itself a smartfrog
 * component (and so is that much easier to test) created Nov 22, 2004 2:45:11
 * PM
 */

public class OneHostXMLListenerTest extends TestRunnerTestBase {

    public OneHostXMLListenerTest(String name) {
        super(name);
    }

    private File tempdir;

    protected TestInfo createTestInfo() {
        TestInfo testInfo = new TestInfo(null);
        testInfo.setName(getClass().getName());
        testInfo.setText(getName());
        return testInfo;
    }
    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        tempdir = new File(System.getProperty("java.io.tmpdir"), "junit");
    }

    public void testSimple() throws Exception {
        File file = new File(tempdir, "testSimple.xml");
        OneHostXMLListener listener = createListener(file, "simple");
        assertTrue("listener is not open", listener.isOpen());
        TestInfo ti = createTestInfo();
        ti.markStartTime();
        listener.startTest(ti);
        assertTrue("listener is not open", listener.isOpen());
        ti.markEndTime();
        listener.endTest(ti);
        assertTrue("listener is not open", listener.isOpen());
        listener.endSuite();
        assertFalse("listener is not closed", listener.isOpen());
        validateXmlLog(file);
    }


    public void testError() throws Exception {
        File file = new File(tempdir, "testError.xml");
        OneHostXMLListener listener = createListener(file, "simple");
        assertTrue("listener is not open", listener.isOpen());
        assertTrue("listener is not happy", listener.isHappy());
        TestInfo testInfo = createTestInfo();
        listener.startTest(testInfo);
        assertTrue("listener is not open", listener.isOpen());
        Throwable t = new RuntimeException("oops", new Throwable("ne&>sted"));
        testInfo.addFaultInfo(t);
        listener.addError(testInfo);
        assertTrue("listener is not open", listener.isOpen());
        testInfo.markEndTime();
        listener.endTest(testInfo);
        listener.endSuite();
        assertFalse("listener is not closed", listener.isOpen());

        validateXmlLog(file);
    }



    public OneHostXMLListener createListener(File file, String suite)
            throws IOException {
        if (file.exists()) {
            file.delete();
        }
        Date startTime = new Date(System.currentTimeMillis());
        OneHostXMLListener listener = new OneHostXMLListener("localhost",
                null, suite, file,
                startTime,
                null);
        listener.open();
        assertTrue("listener is not open", listener.isOpen());
        return listener;
    }

}
