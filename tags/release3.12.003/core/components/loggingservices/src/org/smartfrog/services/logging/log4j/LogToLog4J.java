/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.logging.log4j;

/**
 */
public interface LogToLog4J {
    /**
     * String name for optional attribute
     * {@value}.
     */
    String ATTR_CONFIGURATOR_FILE = "configuratorFile";

    /**
     * Name of a resource to use
     * {@value}
     */
    String ATTR_RESOURCE = "resource";

    /**
     * String name for optional attribute "setLog4JLoggerLevel".
     * {@value}
     */
    String ATTR_SET_INI_LOG4J_LOGGER_LEVEL = "setIniLog4JLoggerLevel";
    /**
     * String name for optional attribute
     * {@value}
     */
    String ATTR_IGNORE_SET_LOG_LEVEL = "ignoreSetLogLevel";

}
