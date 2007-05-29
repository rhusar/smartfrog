/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test;

import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.assertions.TestBlock;

import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;

/**
 * Add an application that is always destroyed on teardown
 * created 13-Oct-2006 16:28:33
 */

public abstract class DeployingTestBase extends SmartFrogTestBase {
    private static final int SPIN_INTERVAL = 10;
    private static final int TIMEOUT = 10000;


    protected DeployingTestBase(String name) {
        super(name);
    }


    protected TerminationRecord expectSuccessfulTermination(TestBlock testBlock) throws Throwable {
        return expectTermination(testBlock, true);
    }

    protected TerminationRecord expectAbnormalTermination(TestBlock testBlock) throws Throwable {
        return expectTermination(testBlock, false);
    }

    protected TerminationRecord expectTermination(TestBlock testBlock,boolean normal) throws Throwable {
        TerminationRecord status = spinUntilFinished(testBlock);
        assertTrue("unexpected exit status" + status, normal==status.isNormal());
        return status;
    }

    /**
     * Delay until a test has finished, sleeping (and yielding the CPU) until
     * that point is reached. There is no timeout.
     * @param testBlock component to spin on
     * @param timeout how long to wait (in millis)
     * @return the termination record of the component
     * @throws Throwable if something went wrong
     */
    protected TerminationRecord spinUntilFinished(TestBlock testBlock, long timeout) throws Throwable {
        try {
            long endtime = System.currentTimeMillis() + timeout;
            while (!testBlock.isFinished() && System.currentTimeMillis() < endtime) {
                Thread.sleep(SPIN_INTERVAL);
            }
            assertTrue("timeout ("+timeout+"ms) waiting for application to finish", testBlock.isFinished());
            return testBlock.getStatus();
        } catch (NoSuchObjectException e) {
            //some kind of remoting problem may happen during termination.
            logThrowable("Object has been deleted", e);
            throw e;
        } catch (RemoteException e) {
            //some kind of remoting problem may happen during termination.
            logThrowable("RMI exceptions during spin-waits may be network race conditions", e);
            throw e;
        }

    }

    /**
     * Delay until a test has finished, sleeping (and yielding the CPU) until
     * that point is reached. There is no timeout.
     * @param testBlock component to spin on 
     * @return the termination record of the component
     * @throws Throwable
     */
    protected TerminationRecord spinUntilFinished(TestBlock testBlock) throws Throwable {
        return spinUntilFinished(testBlock,TIMEOUT);
    }

    /**
     * Assert that a termination record contains the expected values.
     * If either the throwableClass or throwableText attributes are non-null, then the record
     * must contain a fault
     * @param record termination record
     * @param descriptionText text to look for in the description (optional; can be null)
     * @param throwableClass fragment of the class name/package of the exception. (optional; can be null)
     * @param throwableText text to look for in the fault text. (optional; can be null)
     */
    public void assertRecordContains(TerminationRecord record,
                                        String descriptionText,
                                        String throwableClass,
                                        String throwableText) {
        if(descriptionText!=null) {
            assertContains(record.description,descriptionText);
        }
        if(throwableClass !=null || throwableText !=null) {
            if(record.cause!=null) {
                assertFaultCauseAndTextContains(record.cause,
                        throwableClass, throwableText, null);
            } else {
                fail("Expected Termination record "+record+" to contain "
                +" a throwable "+(throwableClass!=null?throwableClass:"")
                + (throwableText!=null?(" with text"+throwableText):""));
            }
        }
    }
}
