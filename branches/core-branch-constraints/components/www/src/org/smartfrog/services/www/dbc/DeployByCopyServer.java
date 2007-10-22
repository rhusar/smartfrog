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
package org.smartfrog.services.www.dbc;

import org.smartfrog.services.www.JavaWebApplicationServer;

/**
 * Base interface for any app server that supports deploy-by-copy deployment.
 * <p/>
 * Child components can implement startup and shutdown behaviour, so functional
 * implementations can be made without writing new java classes.
 * created 19-Jun-2006 15:53:32
 */


public interface DeployByCopyServer extends JavaWebApplicationServer {

    /**
     * Directory to deploy to
     * {@value}
     */
    public static final String ATTR_DEPLOY_DIR = "destDir";

    /**
     * Clean up directory: boolean.
     * This is currently unused.
     * {@value}
     */
    public static final String ATTR_CLEAN_DIRECTORY = "cleanDirOnStartup";


    /**
     * Component description of to start during startup. This component
     * is terminated during shutdown, after the shutdown component is deployed
     * {@value}
     */
    public static final String ATTR_START_COMPONENT = "startup";

    /**
     * Component description of to start during shutdown.
     * It is actually created earlier on, after startup succeeds, so that it is ready for
     * use when termination time comes around.
     * {@value}
     */
    public static final String ATTR_SHUTDOWN_COMPONENT = "shutdown";

    /**
     * Flag to control whether or not a copy is synchronous.
     * {@value}
     */
    public static final String ATTR_SYNCHRONOUS_COPY = "synchronousCopy";


}
