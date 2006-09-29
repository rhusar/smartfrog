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
package org.smartfrog.services.deployapi.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;

/**
 * created 27-Sep-2006 11:34:52
 */

public class EventSubscriberManager implements Iterable<EventSubscription>, EventSubscription {

    private List<EventSubscription> subscriptions=new ArrayList<EventSubscription>();

    private ExecutorService executor;

    public EventSubscriberManager(ExecutorService executor) {
        this.executor = executor;
    }


    public void shutdown() {
        executor.shutdown();
    }
    public void add(EventSubscription sub) {
        subscriptions.add(sub);
    }

    public void remove(EventSubscription sub) {
        subscriptions.remove(sub);
    }

    public Iterator<EventSubscription> iterator() {
        return subscriptions.listIterator();
    }

    /**
     * Do a clean up of old/expired/disconnected entries
     * @return number of purged elements
     */
    public synchronized int purge() {
        int purged=0;
        ListIterator<EventSubscription> it=subscriptions.listIterator();
        while (it.hasNext()) {
            EventSubscription sub = it.next();
            if(!sub.probe()) {
                it.remove();
                purged++;
            }
        }
        return purged;
    }

    /**
     * Something happened. Queue up events in the given executor.
     *
     * @param event the event of interest
     */
    public void event(Event event)  {
        for(EventSubscription sub:this) {
            executor.submit(new NotifierRunnable(sub, event));
        }
    }



    /**
     * Probe the event for still being valid
     *
     * @return false always
     */
    public boolean probe() {
        return false;
    }


}
