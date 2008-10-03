/** (C) Copyright Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.ant;


/**
 * Defines the attributes for counter component.
 */
public interface Ant extends AntWorkflowComponent {

    /**
     * Smartfrog attribute: {@value}
     */
    String ATTR_TASK_NAME = "AntTask";
    /**
     * Smartfrog attribute: {@value}
     */
    String ATTR_ANT_ELEMENT = "AntElement";

    /**
     * Smartfrog attribute: run Ant task in separate thread. Default: false.
     * </p>
     * Value {@value}.
     * */
    String ATTR_ASYNCH = "asynch";

    /**
     * {@value}
     */
    String ATTR_TASKS_RESOURCE = "tasksResource";

    /**
     * {@value}
     */
    String ATTR_TYPES_RESOURCE = "typesResource";

    /**
     * Prefix for env variables {@value}
     */
    String ENV_PREFIX = "env";

    /**
     * This is the attribute we use for the Ant runtime
     * </p>
     * Value {@value}.
     */
    String ATTR_RUNTIME = "runtime";


}
