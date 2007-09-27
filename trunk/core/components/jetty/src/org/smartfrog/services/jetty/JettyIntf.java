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
package org.smartfrog.services.jetty;


import org.smartfrog.services.www.JavaWebApplicationServer;

/**
 * created 17-Jun-2004 11:24:28
 */


public interface JettyIntf extends JavaWebApplicationServer {

    /**
     * jetty home attribute
     * {@value}
     */
    public static final String ATTR_JETTY_HOME = "jettyhome";


    /**
     * cached jetty server
     * {@value}
     */
    public static final String ATTR_JETTY_SERVER = "Jetty Server";

    /**
     * logging flag.
     * {@value}
     */

    public static final String ATTR_ENABLE_LOGGING="enableLogging";

    /**
     * log dir.
     * {@value}
     */

    public static final String ATTR_LOGDIR="logDir";

    /**
     * log pattern.
     * {@value}
     */

    public static final String ATTR_LOGPATTERN="logPattern";

    /**
     * timezone for log data
     * {@value}
     */

    public static final String ATTR_LOG_TZ = "logTimezone";

    /**
     * max# of threads in the pool.
     * {@value}
     */
    public static final String ATTR_MAXTHREADS="maxThreads";

    /**
     * min# of threads in the pool.
     * {@value}
     */
    public static final String ATTR_MINTHREADS = "minThreads";

    /**
     * max idle time for a thread before it is closed
     * {@value}
     */

    public static final String ATTR_MAXIDLETIME = "maxIdleTime";


}
