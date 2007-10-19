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
package org.smartfrog.services.cddlm.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a thread synchronisation queue; anything can push stuff to the queue;
 * any number of workers can block. When something is pushed, one worker will be
 * awoken to process the data created Sep 9, 2004 4:17:54 PM
 */

public class ActionQueue {

    /**
     * our queue
     */
    private List queue = new ArrayList();

    /**
     * add a new action to the queue, notify one single object waiting wakeup
     *
     * @param action
     */
    public synchronized void push(Action action) {
        queue.add(action);
        notifyAll();
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * wait for data
     *
     * @return an action
     * @throws InterruptedException
     */
    public synchronized Action pull() throws InterruptedException {
        return pull(0);
    }

    /**
     * wait for data
     *
     * @param timeout timeout in MS, can be zero for indefinite
     * @return an action
     * @throws InterruptedException
     */
    public synchronized Action pull(long timeout) throws InterruptedException {
        //block
        if (isEmpty()) {
            wait(timeout);
        }
        if (isEmpty()) {
            throw new InterruptedException("No data after timeout");
        }
        //remove the head of the list
        return internalPull();
    }

    /**
     * private function to pull data from a
     *
     * @return
     */
    private synchronized Action internalPull() {
        assert !queue.isEmpty();
        Action action = (Action) queue.remove(0);
        return action;
    }

}
