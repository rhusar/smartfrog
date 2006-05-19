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
package org.smartfrog.services.junit.log;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.junit.LogListener;
import org.smartfrog.services.junit.data.LogEntry;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * created 18-May-2006 15:37:08
 */

public class TestListenerLogImpl extends AbstractTestLog implements TestListenerLog {


    List/*<LogListener>*/ listeners=new ArrayList(1);
    public static final String ERROR_DUPLICATE_ADD = "Duplicate listener registration";

    public TestListenerLogImpl() throws RemoteException {
    }

    /**
     * log an event by passing it to each entry in turn.
     * A remote exception in one propagates to all
     *
     * @param entry
     */
    public void log(LogEntry entry) throws RemoteException {
        Iterator elements = listeners.iterator();
        while (elements.hasNext()) {
            LogListener listener = (LogListener) elements.next();
            listener.log(entry);
        }
    }

    /**
     * Add a listener to log events
     *
     * @param listener
     */
    public void addLogListener(LogListener listener) throws SmartFrogException {
        if(listeners.indexOf(listener)>=0) {
            throw new SmartFrogException(ERROR_DUPLICATE_ADD);
        }
        listeners.add(listener);
    }

    /**
     * Remove a log listener. Harmless if the log is not active
     *
     * @param listener
     */
    public void removeLogListener(LogListener listener)  {
        listeners.remove(listener);
    }

    /**
     * Remove all listeners
     */
    public void clearListeners() {
        listeners.clear();
    }

}
