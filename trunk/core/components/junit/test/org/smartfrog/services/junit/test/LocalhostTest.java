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

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.test.LocalJVMTestBase;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.junit.TestRunner;
import org.smartfrog.services.junit.listeners.BufferingListener;

import java.rmi.RemoteException;

/**
 * Test deploying against a localhost
 * Date: 06-Jul-2004
 * Time: 21:54:25
 */
public class LocalhostTest extends TestRunnerTestBase {

    public LocalhostTest(String name) {
        super(name);
    }


    public void testSuccess() throws Throwable {
        String url;
        Prim deploy = null;
        url="/files/success.sf";
        final String appName = "localhostTest";
        try {
            deploy = deployExpectingSuccess(url , appName);
            TestRunner runner=(TestRunner) deploy;
            assertTrue(runner!=null);
            BufferingListener listener=null;
            listener=(BufferingListener) deploy.sfResolve("listener",listener,true);
            spinTillFinished(listener, 1);

            assertTrue("tests run",listener.getStartCount()==1);
            assertTrue("session started", listener.getSessionStartCount() == 1);
            assertTrue("session ended",
                    listener.getSessionEndCount() == 1);
            assertTrue("all tests passed", listener.testsWereSuccessful());

        } finally {
            terminateApplication(deploy);
        }

    }

    private void spinTillFinished(BufferingListener listener, final int testsExpected) throws InterruptedException,
            RemoteException {
        do {
            Thread.sleep(250);
        } while ( listener.getEndCount() != testsExpected );
    }
}
