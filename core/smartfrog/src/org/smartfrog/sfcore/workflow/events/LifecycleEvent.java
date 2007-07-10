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
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.Serializable;

/**
 * Base class for lifecycle events that components may send
 * created 10-Jul-2007 16:56:59
 * */

public abstract class LifecycleEvent implements Serializable {

    private long timestamp;

    private Prim component;

    private String componentName;

    protected LifecycleEvent() {
    }


    protected LifecycleEvent( Prim component) {
        this.timestamp = System.currentTimeMillis();
        this.component = component;
        if(componentName!=null) {
            componentName=new ComponentHelper(component).completeNameSafe().toString();
        }
    }



    /**
     * Is the component alive?
     * Default is yes. probably.
     * @return true iff the component is alive
     */
    public boolean isAlive() {
        return true;
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
        this.component = null;
    }

    /**
     * Get the name of the component
     * @return the name, which will be an empty string if the component name could not be resolved.
     */
    public String getComponentName() {
        return componentName;
    }
}
