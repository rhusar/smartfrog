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
package org.smartfrog.services.junit;

import java.io.Serializable;

/**
 * This is the configuration to turn
 * created 17-May-2004 17:22:03
 * The clone policy creates a shallow clone, and retains the same listener
 */

public class RunnerConfiguration implements Serializable, Cloneable {

    /**
     * who listens to the tests?
     * This is potentially remote
     */
    private TestListener listener;

    /**
     * flag to identify whether the task should fail when it is time
     */
    private boolean keepGoing = true;

    /**
     * fork into a new process?
     */
    private boolean fork = false;

    public TestListener getListener() {
        return listener;
    }

    public void setListener(TestListener listener) {
        this.listener = listener;
    }

    public boolean getKeepGoing() {
        return keepGoing;
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public boolean getFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }


    /**
     * the shallow clone copies all the simple settings, but shares the test listener.
     * @return
     * @throws CloneNotSupportedException
     */
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
