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
import org.smartfrog.services.junit.listeners.ConsoleListenerComponent;

import java.io.PrintStream;
import java.rmi.RemoteException;

/**
 * created Nov 22, 2004 4:16:27 PM
 */

public class ConsoleListenerTest extends TestCase {


    private ConsoleListenerComponent createListener(PrintStream out)
            throws RemoteException {
        ConsoleListenerComponent listener = new ConsoleListenerComponent();
        if (out != null) {
            listener.setOutputStream(out);
        }
        return listener;
    }

    public void testSuccess() throws Exception {
        ConsoleListenerComponent factory = createListener(null);
        TestListener listener = factory.listen(null, "localhost", "test", 0);
        TestInfo ti = new TestInfo(this);
        ti.markStartTime();
        listener.startTest(ti);
        ti.markEndTime();
        listener.endTest(ti);
        listener.endSuite();
    }

    public void testError() throws Exception {
        ConsoleListenerComponent factory = createListener(null);
        TestListener listener = factory.listen(null, "localhost", "test", 0);
        TestInfo ti = new TestInfo(this);
        listener.startTest(ti);
        Throwable t = new RuntimeException("oops", new Throwable("ne&>sted"));
        ti.addFaultInfo(t);
        listener.addError(ti);
        ti.markEndTime();
        listener.endTest(ti);
        listener.endSuite();
    }
}
