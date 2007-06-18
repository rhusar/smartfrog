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
package org.smartfrog.services.deployapi.notifications.muws;

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * created 10-Oct-2006 15:36:11
 */

public class MuwsEventReceiver implements Iterable<ReceivedEvent> {


    private NotifyServerImpl owner;
    private List<ReceivedEvent> events;
    private String id;
    private String url;
    private AtomicInteger count = new AtomicInteger(0);


    public MuwsEventReceiver(NotifyServerImpl owner) {
        this.owner = owner;
        events = new LinkedList<ReceivedEvent>();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the count of received events
     * @return
     */
    public int getCount() {
        return count.get();
    }


    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public ListIterator<ReceivedEvent> iterator() {
        return events.listIterator();
    }

    /**
     * Handler for received muws events. A new event is added and notify() is called to wake
     * up any waiting object.
     * @param messageContext the message context
     * @param event the received event
     */
    public synchronized void muwsEventReceived(MessageContext messageContext, SoapElement event) {
        count.incrementAndGet();
        events.add(new ReceivedEvent(messageContext, event));
        notify();
    }

    /**
     * cleare the buffer
     */
    public synchronized void clear() {
        events.clear();
    }

    public synchronized int size() {
        return events.size();
    }

    /**
     * destroy an instance by unregistering it from its container
     */
    public void destroy() {
        owner.remove(this);
    }


    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    /**
     * Wait for an incoming event. If there already is one in the buffer, return that, removing
     * it from the list.
     *
     * @param milliseconds time to wait.
     * @return the event or null for timeout
     */
    public synchronized ReceivedEvent waitForEvent(long milliseconds) {
        if(size()==0) {
            try {
                wait(milliseconds);
                if(size()==0) {
                    return null;
                }
            } catch (InterruptedException e) {
                return null;
            }
        }
        return events.remove(0);
    }


    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result
     * @return a string representation of the object.
     */
    public String toString() {
        return "Receiver at "+url+" with "+ events.size()+" event(s)";
    }
}
