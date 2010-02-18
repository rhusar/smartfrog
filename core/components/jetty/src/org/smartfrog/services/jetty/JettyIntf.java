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
    String ATTR_JETTY_HOME = "jettyhome";


    /**
     * cached jetty server
     * {@value}
     */
    String ATTR_JETTY_SERVER = "Jetty Server";

    /**
     * logging flag.
     * {@value}
     */

    String ATTR_ENABLE_LOGGING="enableLogging";

    /**
     * log dir.
     * {@value}
     */

    String ATTR_LOGDIR="logDir";


    /**
     * log pattern.
     * {@value}
     */

    String ATTR_LOGIGNOREPATHS = "logIgnorePaths";
    /**
     * log pattern.
     * {@value}
     */

    String ATTR_LOGPATTERN="logPattern";

    /**
     * timezone for log data.
     * {@value}
     */

    String ATTR_LOG_TZ = "logTimezone";

    /**
     * should log data be appended?
     *
     * {@value}
     */

    String ATTR_LOG_APPEND = "logAppend";

    /**
     * days to keep for log data.
     *  {@value}
     */

    String ATTR_LOG_KEEP_DAYS = "logKeepDays";

    /**
     * is the log extended?
     *  {@value}
     */

    String ATTR_LOG_EXTENDED = "logExtended";

    /**
     * max# of threads in the pool.
     * {@value}
     */
    String ATTR_MAXTHREADS="maxThreads";

    /**
     * min# of threads in the pool.
     * {@value}
     */
    String ATTR_MINTHREADS = "minThreads";

    /**
     * max idle time for a thread before it is closed
     * {@value}
     */

    String ATTR_MAXIDLETIME = "maxIdleTime";


    /**
     * {@value}
     */
    String ATTR_STOP_AT_SHUTDOWN = "stopAtShutdown";

    /**
     * {@value}
     */
    String ATTR_SEND_SERVER_VERSION = "sendServerVersion";

    /**
     * {@value}
     */
    String ATTR_SEND_DATE_HEADER = "sendDateHeader";
}
