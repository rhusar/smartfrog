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
package org.smartfrog.sfcore.workflow.events;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.reference.Reference;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.rmi.Remote;

/**
 * Base class for lifecycle events that components may send
 * created 10-Jul-2007 16:56:59
 * */

public abstract class LifecycleEvent implements Serializable {

    private long timestamp;

    private Prim component;

    private String componentName;

    private TerminationRecord status;

    private HashMap<String, Object> data = new HashMap<String, Object>();

    private static final String UNKNOWN_COMPONENT = "(unknown)";

    public TerminationRecord getStatus() {
        return status;
    }


    /**
     * Simple constructor
     */
    protected LifecycleEvent() {
    }


    /**
     * Set the event to a component
     * @param component component the event came from
     */
    protected LifecycleEvent( Prim component) {
        this(component,null);
    }


    /**
     * Set the component and the optional termination record
     * @param component component
     * @param status termination record, can be null.
     */
    protected LifecycleEvent(Prim component, TerminationRecord status) {
        timestamp = System.currentTimeMillis();
        this.component = component;
        if (component!=null && componentName != null) {
            Reference reference = new ComponentHelper(component).completeNameOrNull();
            componentName = reference != null ? reference.toString() : UNKNOWN_COMPONENT;
        } else {
            componentName = UNKNOWN_COMPONENT;
        }
        this.status = status;
    }

    /**
     * Get the name of the event; used in the toString operator
     * @return the name of this event
     */
    public String getEventName() {
        return getClass().getName();
    }

    /**
     * Is the component alive?
     * Default is yes. probably.
     * @return true iff the component is alive
     */
    public boolean isAlive() {
        return true;
    }

    /**
     * Get the cause from any termination record. Null if there is no termination record, or if there is no cause
     *
     * @return the reason for the tests failing, or null
     */
    public Throwable getCause() {
        return status == null ? null : status.getCause();
    }

    public long getTimestamp() {
        return timestamp;
    }


    public Prim getComponent() {
        return component;
    }


    /**
     * forget about this component; subclasses can do this after initialisation
     */
    protected void resetComponent() {
        component = null;
    }

    /**
     * Get the name of the component
     * @return the name, which will be an empty string if the component name could not be resolved.
     */
    public String getComponentName() {
        return componentName;
    }


    /**
     * String operator includes the buffer, the component name, optional termination record
     * @return a readable outcome
     */
    public String toString() {
        StringBuilder buf=new StringBuilder();
        buf.append(getComponentName());
        buf.append(" -").append(getEventName());
        buf.append(" at ");
        buf.append(new Date(getTimestamp()).toString());
        buf.append(" alive: ");
        buf.append(Boolean.valueOf(isAlive()));
        if(getStatus()!=null) {
            buf.append("\nstatus: \n");
            buf.append(getStatus());
        }
        return buf.toString();
    }


    /**
     * Add a remote reference. This will be sent over to the far end.
     * Do not add references that will go away, such as those to
     * a component that is being terminatd.
     *
     * @param key string key
     * @param endpoint endpoint to refer to
     */
    public void put(String key, Remote endpoint) {
        data.put(key, endpoint);
    }

    /**
     * Add serializable data. This will be sent over to the listener
     *
     * @param key string key
     * @param value value
     */
    public void put(String key, Serializable value) {
        data.put(key, value);
    }

    /**
     * Look up a value; return it or the default
     *
     * @param key key to look up
     * @param defval the value to return if there is no entry
     * @return the lookup result or the supplied default value
     */
    public Object get(String key, Object defval) {
        Object result = data.get(key);
        return result != null ? result : defval;
    }

    /**
     * lookup a specific key; return the value or null
     *
     * @param key key to look up
     * @return the value or null for no match
     */
    public Object get(String key) {
        return data.get(key);
    }

    /**
     * Protected access to the hashmap.
     *
     * @return the map.
     */
    protected HashMap<String, Object> getData() {
        return data;
    }

}
