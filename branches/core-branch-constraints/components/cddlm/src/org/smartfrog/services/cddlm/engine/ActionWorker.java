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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * created Sep 9, 2004 5:01:23 PM
 */

public class ActionWorker extends Thread {

    private ActionQueue queue;

    private long timeout;

    private boolean terminated = false;
    private boolean busy = false;
    private boolean started = false;

    static final Log log = LogFactory.getLog(ActionWorker.class);

    /**
     * create a worker (not currently active
     *
     * @param queue
     * @param timeout
     */
    public ActionWorker(ActionQueue queue, long timeout) {
        this.queue = queue;
        this.timeout = timeout;
    }

    /**
     * run
     */
    public void run() {
        started = true;
        while (!terminated) {
            Action action;
            try {
                action = queue.pull(timeout);
                //look for our special exit action
                if (action instanceof EndWorkerAction) {
                    //and mark ourselves as ready to die
                    terminated = true;
                } else {
                    busy = true;
                    action.execute();
                }
            } catch (SmartFrogException e) {
                processException(e);
            } catch (RemoteException e) {
                processException(e);
            } catch (InterruptedException e) {
                //time to die
                terminated = true;
            }
            //mark as un busy. do it here so that however we exit, we are not busy
            busy = false;
            //end while loop when terminated
        }

    }

    private void processException(Exception ex) {
        log.error("Received when invoking endpoint",ex);
    }

    /**
     * test state
     *
     * @return true if we are terminated
     */
    public boolean isTerminated() {
        return terminated;
    }

    /**
     * are we busy
     *
     * @return
     */
    public boolean isBusy() {
        return busy;
    }

    public boolean isStarted() {
        return started;
    }
}
