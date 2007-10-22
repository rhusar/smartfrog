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


/**
 * created 27-Sep-2006 10:41:58
 */


public interface EventSubscription {


    /**
     * Something happened to this job
     * @param event the event of interest
     * @return true if it was processed
     */
    boolean event(Event event);

    /**
     * Probe the event for still being valid
     * @return true if the event is still valid; false if it has expired and should be deleted
     */
    boolean probe();

    /**
     * Set the manager
     * @param manager manager
     */
    void setManager(EventSubscriberManager manager);

    /**
     * Tell the component to do any cleanup, then to unset itself from the manager
     */
    void unsubscribe();
}
