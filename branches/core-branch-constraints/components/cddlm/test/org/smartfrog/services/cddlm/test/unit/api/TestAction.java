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

import org.smartfrog.services.cddlm.engine.BaseAction;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * For testing, we have an action that increments every time it is executed
 * created Sep 9, 2004 5:39:44 PM
 */

public class TestAction extends BaseAction {

    public int counter = 0;

    public synchronized int getCounter() {
        return counter;
    }

    public synchronized void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * execute increments a counter and waits on it
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void execute() throws SmartFrogException,
            RemoteException {
        super.execute();
        counter++;

    }

}
