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
package org.smartfrog.services.assertions.history;

import org.smartfrog.services.assertions.SmartFrogAssertionException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** created 09-Jul-2007 12:11:03 */

public class HistoryImpl extends PrimImpl implements History {

    private List/*<Event>*/ events=new ArrayList/*<Event>*/();

    public HistoryImpl() throws RemoteException {
    }

    /**
     * Add an event to the history
     * @param event a new event to add
     * @return the index value of the event
     */
    public synchronized int addEvent(HistoryEvent event) throws SmartFrogAssertionException {
        if(event.message==null) {
            throw new SmartFrogAssertionException("No null messages are allowed");
        }
        events.add(event);
        int index = events.size() - 1;
        event.index=index;
        return index;
    }

    /**
     * Log a message, creating a new event from it
     * @param message
     * @return the index value of the event
     */
    public int log(String message) throws SmartFrogAssertionException {
        HistoryEvent event=new HistoryEvent(System.currentTimeMillis(), message);
        return addEvent(event);
    }


    /**
     * Clear the event history
     */
    public synchronized void clear() {
        events.clear();
    }

    public int size() {
        return events.size();
    }

    /**
     * Assert that the event list is of a specific size
     * @param expected the expected size
     * @throws SmartFrogAssertionException
     */
    public void assertSizeEquals(int expected) throws SmartFrogAssertionException {
        int s=size();
        if(s!=expected) {
            throw new SmartFrogAssertionException("Expected a history of size "+expected+" -actual size="+s
                    + "\nin\n" + toString(),
                this);
        }
    }

    /**
     * Assert that the event list is at least as big as the minimum.
     *
     * @param minimum the expected size
     * @throws SmartFrogAssertionException
     */
    public void assertSizeAtLeast(int minimum) throws SmartFrogAssertionException {
        int s = size();
        if (s <= minimum) {
            throw new SmartFrogAssertionException("Expected a history of minimum size " + minimum + " -actual size=" + s
                    +"\nin\n"+toString(),
                    this);
        }
    }

    /**
     * get the element at an offset
     * @param offset the offset
     * @return the element there or
     * @throws IndexOutOfBoundsException if the offset is out of range 
     */
    public HistoryEvent elementAt(int offset) {
        return (HistoryEvent) events.get(offset);
    }

    /**
     * Search through the list (synchronized to block changes during the scan) to
     * locate a message containing the specific string. Full or partial matches
     * are supported
     * @param text text to look for
     * @param partialMatch flag to set to true for substring matching
     * @return the first event that matched, or null for no match
     */
    public synchronized HistoryEvent lookup(String text,boolean partialMatch) {
        Iterator it=events.listIterator();
        boolean match=false;
        while (it.hasNext()) {
            HistoryEvent event = (HistoryEvent) it.next();
            String message=event.message;
            if(partialMatch) {
                match= message.indexOf(text)>=0;
            } else {
                match=message.equals(text);
            }
            if(match) {
                return event;
            }
        }
        return null;
    }

    /**
     * Assert that an event can be found
     * @param text text to look for
     * @param partialMatch flag to set to true for substring matching
     * @param errorText error message. if null, one is constructed.
     * @return the event found (which is always non-null)
     * @throws SmartFrogAssertionException if the condition is not met
     */
    public HistoryEvent assertEventFound(String text, boolean partialMatch,String errorText) throws SmartFrogAssertionException {
        HistoryEvent event=lookup(text,partialMatch);
        if(event==null) {
            String message=errorText;
            if(message==null) {
                message="Did not find any event "+(partialMatch?"containing":"matching")+" "+text
                        +"\nin\n"+toString();
            }
            throw new SmartFrogAssertionException(message,this);
        }
        return event;
    }

    /**
     * Find two events; assert that they are ordered by their index values
     * @param text1 partial matching text for the first event
     * @param text2 partial matching text for the second event
     * @throws SmartFrogAssertionException if the condition is not met
     */
    public void assertEventsOrdered(String text1,String text2) throws SmartFrogAssertionException {
        HistoryEvent event1 = assertEventFound(text1,true,null);
        HistoryEvent event2 = assertEventFound(text2, true, null);
        if(event1.index>event2.index) {
            throw new SmartFrogAssertionException(
                    "Event "+event1+" came after "+event2+"\nin\n"+toString(),
                    this);
        }
    }

    /**
     * Dumps the entire history; used in assertion messages
     */

    public synchronized String toString() {
        StringBuffer buffer=new StringBuffer(super.toString());
        buffer.append('\n');
        buffer.append("size=");
        buffer.append(size());
        buffer.append('\n');
        Iterator it = events.listIterator();
        while (it.hasNext()) {
            HistoryEvent event = (HistoryEvent) it.next();
            buffer.append(event.toString());
            buffer.append('\n');
        }
        return buffer.toString();
    }

}
