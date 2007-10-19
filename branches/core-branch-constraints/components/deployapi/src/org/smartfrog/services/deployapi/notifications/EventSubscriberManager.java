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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;

/**
 * something to handle all subscriptions. Unlike {@link SubscriptionServiceStore}, this
 * component uses hard references, so retains the links.
 * created 27-Sep-2006 11:34:52
 */

public class EventSubscriberManager extends AbstractEventSubscription 
        implements Iterable<EventSubscription>, EventSubscription {

    private static final Log log= LogFactory.getLog(EventSubscriberManager.class);

    private List<EventSubscription> subscriptions=new ArrayList<EventSubscription>();

    private ExecutorService executor;
    private String role;

    public EventSubscriberManager(String role, ExecutorService executor) {
        this.role=role;
        this.executor = executor;
    }

    public void shutdown() {
        executor.shutdown();
    }
    public void add(EventSubscription sub) {
        if(log.isInfoEnabled()) {log.info("adding a subscription "+sub);}
        subscriptions.add(sub);
        sub.setManager(this);
    }

    public void remove(EventSubscription sub) {
        if (log.isInfoEnabled()) {
            log.info("removing the subscription " + sub);
        }
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
    public boolean event(Event event)  {
        for(EventSubscription sub:this) {
            executor.submit(new NotifierRunnable(sub, event));
        }
        return true;
    }



    /**
     * Probe the event for still being valid
     *
     * @return false always
     */
    public boolean probe() {
        return false;
    }


    /**
     * Returns a string representation of the object. I
     * @return a string representation of the object.
     */
    public String toString() {
        return "EventSubscriberManager for "+role+" of size "+subscriptions.size();
    }
}
