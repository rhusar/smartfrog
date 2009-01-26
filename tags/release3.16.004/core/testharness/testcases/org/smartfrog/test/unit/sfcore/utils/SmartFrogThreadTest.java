/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.unit.sfcore.utils;

import junit.framework.TestCase;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Test that our thread code works; that exceptions are caught, etc.
 * created 13-Feb-2007 11:02:46
 * */

public class SmartFrogThreadTest extends TestCase {



    /**
     * Constructs a test case with the given name.
     * @param name test name
     */
    public SmartFrogThreadTest(String name) {
        super(name);
    }

    /**
     * Test that an exception is caught in the run
     * @throws Exception trouble
     */
    public void testExceptionCaught() throws Exception {
        SmartFrogThread thread=new SmartFrogThread(
                new ThrowingRunnable(
                        new RuntimeException("test")));
        //IDEs may complain that we call Thread.run instead of Thread.start(),
        //but this is deliberate...leave the code as is.
        thread.run();
        assertTrue(thread.isThrown());
        try {
            thread.rethrow();
            fail("expected rethrow");
        } catch (SmartFrogException e) {
            Throwable cause = e.getCause();
            assertNotNull(cause);
            assertTrue (cause instanceof RuntimeException);
        }
    }


    /**
     * Test that an exception is caught in the run
     * @throws Exception trouble
     */
    public void testNotifyingSelf() throws Exception {
        SmartFrogThread thread = new SmartFrogThread();
        thread.start();
        thread.waitForNotification(1000);
        assertTrue(thread.isFinished());
    }

    /**
     * Test that an exception is caught in the run
     * @throws Exception trouble
     */
    public void testNotifyingOther() throws Exception {
        Object waiter=new Object();
        SmartFrogThread thread = new SmartFrogThread(waiter);
        thread.start();
        synchronized(waiter) {
            waiter.wait(1000);
        }
        assertTrue(thread.isFinished());
    }

    public void testSleepingTooLong() throws Exception {
        SmartFrogThread thread = new SmartFrogThread(new SleepingRunnable(10000));
        thread.start();
        thread.waitForNotification(1000);
        assertFalse(thread.isFinished());
    }

    /**
     * A little runnable that throws an exception
     */
    private static class ThrowingRunnable implements Runnable {

        private RuntimeException rte;


        /**
         * A runnable designed to throw a RuntimeException
         * @param rte the exception to throw
         */
        ThrowingRunnable(RuntimeException rte) {
            this.rte = rte;
        }

        /**
         * throw any runtime exception we have been created with
         */
        public void run() {
            if(rte!=null) {
                throw rte;
            }
        }
    }

    /**
     * A little runnable that throws an exception
     */
    private static class SleepingRunnable implements Runnable {

        private long sleep;


        SleepingRunnable(long sleep) {
            this.sleep = sleep;
        }



        /**
         * throw any runtime exception we have been created with
         */
        public void run() {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
