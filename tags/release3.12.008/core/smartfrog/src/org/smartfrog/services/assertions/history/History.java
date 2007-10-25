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

import java.rmi.Remote;
import java.rmi.RemoteException;

/** created 09-Jul-2007 12:10:42 */

public interface History extends Remote {
    /**
     * Add an event to the history
     *
     * @param event a new event to add
     * @return the index value of the event
     * @throws SmartFrogAssertionException for invalid events
     * @throws RemoteException for RMI-related problems
     */
    int addEvent(HistoryEvent event) throws RemoteException, SmartFrogAssertionException;

    /**
     * Log a message, creating a new event from it
     *
     * @param message message to log
     * @return the index value of the event
     * @throws SmartFrogAssertionException for assertion failures
     * @throws RemoteException for RMI-related problems
     */
    int log(String message) throws RemoteException, SmartFrogAssertionException;

    /**
     * Clear the event history
     * @throws RemoteException for RMI-related problems
     */
    void clear() throws RemoteException;

    /**
     * get the size of the history 
     * @return the size
     * @throws RemoteException for RMI-related problems
     */
    int size() throws RemoteException;

    /**
     * Assert that the event list is of a specific size
     *
     * @param expected the expected size
     * @throws SmartFrogAssertionException for assertion failures
     * @throws RemoteException for RMI-related problems
     */
    void assertSizeEquals(int expected) throws RemoteException, SmartFrogAssertionException;

    /**
     * Assert that the event list is at least as big as the minimum.
     *
     * @param minimum the expected size
     * @throws SmartFrogAssertionException for assertion failures
     * @throws RemoteException for RMI-related problems
     */
    void assertSizeAtLeast(int minimum) throws RemoteException, SmartFrogAssertionException;

    /**
     * get the element at an offset
     *
     * @param offset the offset
     * @return the element there or
     * @throws IndexOutOfBoundsException if the offset is out of range
     * @throws RemoteException for RMI-related problems
     */
    HistoryEvent elementAt(int offset) throws RemoteException;

    /**
     * Look up the events.
     * Search through the list (synchronized to block changes during the scan) to
     * locate a message containing the  specific string. Full or partial matches are supported
     *
     * @param text         text to look for
     * @param partialMatch flag to set to true for substring matching
     * @return the first event that matched, or null for no match
     * @throws RemoteException for RMI-related problems
     */
    HistoryEvent lookup(String text, boolean partialMatch) throws RemoteException;

    /**
     * Assert that an event can be found
     *
     * @param text         text to look for
     * @param partialMatch flag to set to true for substring matching
     * @param errorText    error message. if null, one is constructed.
     * @return the event found (which is always non-null)
     * @throws SmartFrogAssertionException if the condition is not met
     * @throws RemoteException for RMI-related problems
     */
    HistoryEvent assertEventFound(String text, boolean partialMatch, String errorText)
            throws RemoteException, SmartFrogAssertionException;

    /**
     * Find two events; assert that they are ordered by their index values
     *
     * @param text1 partial matching text for the first event
     * @param text2 partial matching text for the second event
     * @throws SmartFrogAssertionException if the condition is not met
     * @throws RemoteException for RMI-related problems
     */
    void assertEventsOrdered(String text1, String text2) throws RemoteException, SmartFrogAssertionException;
}
