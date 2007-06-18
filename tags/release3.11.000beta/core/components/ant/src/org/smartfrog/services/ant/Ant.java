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

import java.rmi.Remote;

/**
 * Defines the attributes for counter component.
 */
public interface Ant extends Remote {


    /**
     * Smartfrog attribute: {@value}
     */
    String ATTR_TASK_NAME = "AntTask";
    /**
     * Smartfrog attribute: {@value}
     */
    String ATTR_ANT_ELEMENT = "AntElement";

    /**
     * Smartfrog attribute: {@value}
     */
    String ATTR_LOG_LEVEL = "logLevel";

    /**
     * log level: {@value}
     */
    String ATTR_LOG_LEVEL_DEBUG="debug";
    /**
     * log level: {@value}
     */
    String ATTR_LOG_LEVEL_VERBOSE = "verbose";
    /**
     * log level: {@value}
     */
    String ATTR_LOG_LEVEL_INFO = "info";
    /**
     * log level: {@value}
     */
    String ATTR_LOG_LEVEL_ERROR = "error";
    /**
     * log level: {@value}
     */
    String ATTR_LOG_LEVEL_WARN = "warn";

    /**
     * Smartfrog attribute: {@value}
     */
    String ATTR_PROPERTIES = "properties";

    /**
     * Smartfrog attribute: {@value}
     */
    String ATTR_BASEDIR = "basedir";

    /** Smartfrog attribute: run Ant task in separate thread. Default: false. Value {@value}. */
   final static String ATR_ASYNCH = "asynch";

    String ATTR_TASKS_RESOURCE = "tasksResource";

    String ATTR_TYPES_RESOURCE = "typesResource";

    /**
     * Prefix for env variables {@value}
     */
    String ENV_PREFIX = "env";

    String ATTR_RUNTIME = "runtime";


}
