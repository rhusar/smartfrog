/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.core;

import org.apache.hadoop.util.LifecycleService;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 26-Aug-2009 16:40:03
 */


/**
 * This is the full service status
 * 
 *  * <li>Override {@link #innerPing(ServiceStatus)} with any health checks that a service
 * can perform to check that it is still "alive". These should be short lasting
 * and non-side effecting. Simple checks for valid data structures and live
 * worker threads are good examples. When the service thinks that something
 * has failed, throw an IOException with a meaningful error message!
 * </li>

 */
public final class ServicePingStatus implements Serializable {
    /**
     * enumerated state
     */
    private LifecycleService.ServiceState state;

    /**
     * name of the service
     */
    private String name;

    /**
     * when did the state change?
     */
    private Date lastStateChange;

    /**
     * a possibly null array of exceptions that caused a system failure
     */
    public ArrayList<Throwable> throwables = new ArrayList<Throwable>(0);

    /**
     * Create an empty service status instance
     */
    public ServicePingStatus() {
    }

    /**
     * Create a service status instance with the base values set
     *
     * @param name            service name
     * @param state           current state
     * @param lastStateChange when did the state last change?
     */
    public ServicePingStatus(String name, LifecycleService.ServiceState state,
                         Date lastStateChange) {
        this.state = state;
        this.name = name;
        this.lastStateChange = lastStateChange;
    }

    /**
     * Create a service status instance from the given service
     *
     * @param service service to read from
     */
    public ServicePingStatus(LifecycleService service) {
        name = service.getServiceName();
        updateState(service);
    }

  /**
     * Add a new throwable to the list. This is a no-op if it is null. To be safely sent over a network connection, the
     * Throwable (and any chained causes) must be fully serializable.
     *
     * @param thrown the throwable. Can be null; will not be cloned.
     */
    public void addThrowable(Throwable thrown) {
        if (thrown != null) {
            throwables.add(thrown);
        }
    }

    /**
     * Get the list of throwables. This may be null.
     *
     * @return A list of throwables or null
     */
    public List<Throwable> getThrowables() {
        return throwables;
    }

    /**
     * Get the current state
     *
     * @return the state
     */
    public LifecycleService.ServiceState getState() {
        return state;
    }

    /**
     * set the state
     *
     * @param state new state
     */
    public void setState(LifecycleService.ServiceState state) {
        this.state = state;
    }

    /**
     * Get the name of the service
     *
     * @return the service name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the service
     *
     * @param name the service name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the date of the last state change
     *
     * @return when the service state last changed
     */
    public Date getLastStateChange() {
        return lastStateChange;
    }

    /**
     * Set the last state change
     *
     * @param lastStateChange the timestamp of the last state change
     */
    public void setLastStateChange(Date lastStateChange) {
        this.lastStateChange = lastStateChange;
    }

    /**
     * Update the service state
     *
     * @param service the service to update from
     */
    public void updateState(LifecycleService service) {
        synchronized (service) {
            setState(service.getServiceState());
            setLastStateChange(service.getLastStateChange());
        }
    }

    /**
     * The string operator includes the messages of every throwable in the list of failures
     *
     * @return the list of throwables
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append(" in state ").append(getState());
        for (Throwable t : throwables) {
            builder.append("\n ").append(t.toString());
        }
        return builder.toString();
    }
}
