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
package org.smartfrog.services.junit.test;

import junit.framework.TestCase;
import org.smartfrog.services.junit.TestInfo;
import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.listeners.OneHostXMLListener;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Test the {@link OneHostXMLListener} class, that is not itself a smartfrog
 * component (and so is that much easier to test) created Nov 22, 2004 2:45:11
 * PM
 */

public class OneHostXMLListenerTest extends TestCase {


    File tempdir;

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        tempdir = new File(System.getProperty("java.io.tmpdir"), "junit");
    }

    public void testSimple() throws Exception {
        File file = new File(tempdir, "testSimple.xml");
        TestListener listener = createListener(file, "simple");
        TestInfo ti = new TestInfo(this);
        ti.markStartTime();
        listener.startTest(ti);
        ti.markEndTime();
        listener.endTest(ti);
        listener.endSuite();
        validate(file);
    }


    public void testError() throws Exception {
        File file = new File(tempdir, "testError.xml");
        TestListener listener = createListener(file, "simple");
        TestInfo ti = new TestInfo(this);
        listener.startTest(ti);
        Throwable t = new RuntimeException("oops", new Throwable("ne&>sted"));
        ti.addFaultInfo(t);
        listener.addError(ti);
        ti.markEndTime();
        listener.endTest(ti);
        listener.endSuite();

        validate(file);
    }

    public OneHostXMLListener createListener(File file, String suite)
            throws IOException {
        if (file.exists()) {
            file.delete();
        }
        Date startTime = new Date(System.currentTimeMillis());
        OneHostXMLListener listener = new OneHostXMLListener("localhost",
                file,
                suite,
                startTime,
                null);
        return listener;
    }


    public Document validate(File file) throws Exception {
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
