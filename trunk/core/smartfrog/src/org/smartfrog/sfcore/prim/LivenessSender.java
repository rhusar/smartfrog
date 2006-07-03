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

package org.smartfrog.sfcore.prim;

import org.smartfrog.sfcore.common.Timer;


/**
 * Implements sending liveness messages to a liveness targets.
 *
 */
public class LivenessSender extends Timer {
    /** Thread group for all liveness threads. */
    protected static ThreadGroup livenessGroup = new ThreadGroup("SFLiveness");

    /** Target for the liveness messages. */
    protected Liveness target;

    /**
     * Constructor.
     *
     * @param target target for heart beats
     * @param delay wait between heartbeats (in millis)
     * @param name String name
     */
    public LivenessSender(Liveness target, long delay, String name) {
        super(delay);
        this.name = "LivenessSender";
        if ((name!=null)&&(!(name.trim().equals("")))){
            this.name = this.name+"_"+name;
        }

        this.target = target;
    }

    /**
     * Timer behaviour. Send liveness message to target. Target should handle
     * any exceptions in the sfPing method. They are ignored here.
     *
     */
    protected void timerTick() {
        try {
            target.sfPing(this);
        } catch (Exception pingex) {
            // ignore
        }
    }

    /**
     * Override Timer behavior to make the created thread a daemon thread and
     * part of the liveness thread group.
     *
     * @param run interface to run on
     * @param nameTh String name
     * @return  Thread
     */
    protected Thread createThread(Runnable run, String nameTh) {
        if (nameTh !=null)
            this.name = nameTh+"."+name;
        Thread t = new Thread(livenessGroup, run,name);
        t.setDaemon(true);

        return t;
    }
}
