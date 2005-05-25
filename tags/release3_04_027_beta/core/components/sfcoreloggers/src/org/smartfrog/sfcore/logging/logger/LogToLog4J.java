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

package org.smartfrog.sfcore.logging.logger;

/**
 */
public interface LogToLog4J {
    /** String name for optional attribute "configuratorFile". */
    final static String ATR_CONFIGURATOR_FILE = "configuratorFile";

// Log4J 1.3 removes Configure and Wath
//    /** String name for optional attribute "configureAndWatch". */
//    final static String ATR_CONFIGURE_AND_WATCH = "configureAndWatch";
//    /** String name for optional attribute "configureAndWatchDelay". */;
//    final static String ATR_CONFIGURE_AND_WATCH_DELAY = "configureAndWatchDelay";

    /** String name for optional attribute "setLog4JLoggerLevel". */
    final static String ATR_SET_INI_LOG4J_LOGGER_LEVEL = "setIniLog4JLoggerLevel";
    /** String name for optional attribute "setLog4JLoggerLevel". */
    final static String ATR_INGNORE_SET_LOG_LEVEL = "ignoreSetLogLevel";
}
