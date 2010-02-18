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
package org.smartfrog.sfcore.workflow.conditional.conditions;

import org.smartfrog.sfcore.workflow.conditional.Condition;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/** created 27-Feb-2007 13:50:56 */

public class RunningForImpl extends PrimImpl implements Condition, RunningFor {

    private long startTime = -1;
    private long endTime = -1;

    public RunningForImpl() throws RemoteException {

    }


    /**
     * record the current time and work out when our runningfor test will begin to pass
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        startTime = System.currentTimeMillis();
        long days, hours, minutes, seconds, milliseconds;
        days = sfResolve(ATTR_DAYS, 0L, true);
        hours = sfResolve(ATTR_HOURS, 0L, true);
        minutes = sfResolve(ATTR_MINUTES, 0L, true);
        seconds = sfResolve(ATTR_SECONDS, 0L, true);
        milliseconds = sfResolve(ATTR_MILLISECONDS, 0L, true);
        long delay = days;
        delay = delay * 24 + hours;
        delay = delay * 60 + minutes;
        delay = delay * 60 + seconds;
        delay = delay * 1000 + milliseconds;
        endTime = startTime + delay;

        if (delay > 0) {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("RunningFor will pass after " + delay
                        + "milliseconds");
            } else {
                sfLog().debug("RunningFor will always pass; delay=" + delay);
            }
        }
    }


    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        long now = System.currentTimeMillis();
        boolean overrun = now >= endTime;
        return overrun;
    }
}
