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
import org.smartfrog.services.junit.TestListenerFactory;
import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.TestInfo;
import org.smartfrog.services.junit.listeners.BufferingListener;
import org.smartfrog.services.junit.listeners.BufferingListenerComponent;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Test that a buffering listerner
 * created Nov 22, 2004 1:49:37 PM
 */

public class BufferingListenerTest extends TestCase {


    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
    }

    public void testSuccess() throws Exception {
        BufferingListener buffer = createFactory();
        TestListener listener=buffer.listen("localhost","testo",System.currentTimeMillis());
        TestInfo ti=new TestInfo(this);
        ti.markStartTime();
        listener.startTest(ti);
        ti.markEndTime();
        listener.endTest(ti);
        listener.endSuite();
        assertEquals(1, buffer.getSessionStartCount());
        assertEquals(1, buffer.getSessionEndCount());
        assertEquals(1,buffer.getStartCount());
        assertEquals(1,buffer.getEndCount());
        TestInfo ti2=buffer.getEndInfo(0);
        assertEquals(ti.getClassname(),ti2.getClassname());
        assertFalse(ti2.getStartTime()==0);
        assertFalse(ti2.getEndTime() == 0);
        assertTrue(buffer.testsWereSuccessful());
    }

    public void testFailure() throws Exception {
        BufferingListener buffer = createFactory();
        TestListener listener = buffer.listen("localhost",
                "testo",
                System.currentTimeMillis());
        TestInfo ti = new TestInfo(this);
        ti.markStartTime();
        listener.startTest(ti);
        Throwable t=new RuntimeException("oops",new Throwable("nested"));
        ti.addFaultInfo(t);
        listener.addError(ti);
        ti.markEndTime();
        listener.endTest(ti);
        listener.endSuite();
        assertEquals(1, buffer.getSessionStartCount());
        assertEquals(1, buffer.getSessionEndCount());
        assertEquals(1, buffer.getEndCount());
        assertEquals(1, buffer.getErrorCount());
        assertEquals(0, buffer.getFailureCount());
        TestInfo ti2 = buffer.getErrorInfo(0);
        assertEquals(ti.getClassname(), ti2.getClassname());
/*
        assertFalse(ti2.getStartTime() == 0);
        assertFalse(ti2.getEndTime() == 0);
*/
        assertFalse(buffer.testsWereSuccessful());
    }


    private BufferingListener createFactory() throws RemoteException {
        BufferingListener factory;
        factory = new BufferingListenerComponent();
        return factory;
    }
}
