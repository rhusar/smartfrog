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


package org.smartfrog.services.junit.test;

import org.smartfrog.services.junit.TestRunner;
import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.test.TestHelper;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.rmi.RemoteException;

/**
 * this is a parent class for our tests, so that we can share logic about
 * running tests, blocking till they finish, etc. Date: 07-Jul-2004 Time:
 * 20:04:27
 */
public class TestRunnerTestBase extends SmartFrogTestBase {
    public static final String CODEBASE_PROPERTY = "org.smartfrog.codebase";
    public static final String TIMEOUT_PROPERTY = "timeout";
    public static final int TIMEOUT_DEFAULT = 10;

    public TestRunnerTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        assertSystemPropertySet(CODEBASE_PROPERTY);
    }

    protected boolean spinTillFinished(TestRunner runner,
            int timeoutSeconds) throws InterruptedException,
            RemoteException {

        do {
            Thread.sleep(1000);
            timeoutSeconds--;
        } while (!runner.isFinished() && timeoutSeconds >= 0);
        return runner.isFinished();
    }

    protected int getTimeout() {
        int seconds = TIMEOUT_DEFAULT;
        String timeout = TestHelper.getTestProperty(TIMEOUT_PROPERTY, null);
        if (timeout != null) {
            seconds = Integer.valueOf(timeout).intValue();
        }
        return seconds;
    }

    public Document validateXmlLog(File file) throws Exception {
        assertTrue(file.exists());
        DocumentBuilder builder;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        factory.setValidating(true);
        builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        return document;
    }
}
