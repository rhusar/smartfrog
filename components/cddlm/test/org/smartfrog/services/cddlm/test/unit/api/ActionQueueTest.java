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
package org.smartfrog.services.cddlm.test.unit.api;

import junit.framework.TestCase;
import org.smartfrog.services.cddlm.engine.Action;
import org.smartfrog.services.cddlm.engine.ActionQueue;
import org.smartfrog.services.cddlm.engine.ActionWorker;
import org.smartfrog.services.cddlm.engine.EndWorkerAction;

/**
 * created Sep 9, 2004 5:34:01 PM
 */

public class ActionQueueTest extends TestCase {
    public static final int LONG_TIMEOUT = 60000;

    private ActionQueue queue;
    private TestAction testAction;
    public static final int WORK_DELAY = 5000;


    protected void setUp() throws Exception {
        queue = new ActionQueue();
        testAction = new TestAction();
    }

    public void testQueue() throws Exception {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        queue.push(testAction);
        assertFalse(queue.isEmpty());
        assertEquals(1, queue.size());
        Action a2;
        a2 = queue.pull(1);
        assertEquals(testAction, a2);
        assertTrue(queue.isEmpty());
    }

    public void testTimeout() throws Exception {
        Action a2;
        try {
            a2 = queue.pull(10);
            fail("should have been interrupted");
        } catch (InterruptedException e) {

        }
    }

    public void testTestAction() throws Exception {
        assertEquals(0, testAction.getCounter());
        testAction.execute();
        assertEquals(1, testAction.getCounter());
        testAction.setCounter(0);
        assertEquals(0, testAction.getCounter());
    }

    public void testWorker() throws Exception {
        ActionWorker worker = new ActionWorker(queue, LONG_TIMEOUT);
        assertFalse(worker.isStarted());
        worker.start();
        assertFalse(worker.isTerminated());
        assertFalse(worker.isBusy());
        queue.push(testAction);
        testAction.block(WORK_DELAY);
        assertTrue(worker.isStarted());
        assertEquals(1, testAction.getCounter());
        EndWorkerAction endAction = new EndWorkerAction();
        queue.push(endAction);
        endAction.block(WORK_DELAY);
        assertTrue(worker.isTerminated());
    }

}
