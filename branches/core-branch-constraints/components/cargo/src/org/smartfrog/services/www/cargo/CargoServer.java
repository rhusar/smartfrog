/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.cargo;

import org.smartfrog.services.www.JavaWebApplicationServer;


/**
 */
public interface CargoServer extends JavaWebApplicationServer {

    /**
     * {@value}
     */
    String ATTR_CONFIGURATION_CLASS = "configurationClass";

    //String ATTR_HOSTNAME = "cargo.servlet.hostname";

    /**
     * {@value}
     */
    String ATTR_LOGGING = " logging";

    /**
     * name of the class that is the implementation
     * {@value}
     */
    String ATTR_CONTAINER_CLASS = "containerClass";

    /**
     * {@value}
     */
    String ATTR_HOME = "home";


    /**
     * This is the temp dir used for stuff, that must be empty on startup (or things break)
     * {@value}
     */
    String ATTR_CONFIG_DIR = "tempDir";


    /**
     * {@value}
     */
    String ATTR_EXTRA_CLASSPATH = "extraClasspath";

    /**
     * {@value}
     */
    String ATTR_PROPERTIES = "properties";
}
