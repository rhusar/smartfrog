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
package org.smartfrog.services.comm.slp.agents;

import java.util.TimerTask;

/** This class implements a TimerTask which is activated when a SA needs to refresh its registrations with the DAs. */
class SARefreshTimerTask extends TimerTask {
    /** The thread handling the registration */
    private SARegistrationThread regThread;

    /**
     * Creates a new timer task.
     *
     * @param regThread The thread to notify
     */
    SARefreshTimerTask(SARegistrationThread regThread) {
        this.regThread = regThread;
    }

    /**
     * When the timer task runs, it will execute this method. The method tells the registration thread that it is time
     * to refresh the service advertisements.
     */
    public void run() {
        regThread.itIsTimeToRefresh = true;
        regThread.tellThreadToWork();
    }
}
