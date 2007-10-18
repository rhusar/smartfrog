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

package org.smartfrog.sfcore.logging;

//import org.smartfrog.sfcore.common.SmartFrogException;


/**
 * A simple logging interface abstracting logging APIs based in Apache Jakarta
 * logging.
 *
 */
public interface LogLevel {

    /** "IGNORE" level logging < 1 */
    public static final int LOG_LEVEL_IGNORE  = 0;
    /** "Trace" level logging = 1 */
    public static final int LOG_LEVEL_TRACE  = 1;
    /** "Debug" level logging = 2*/
    public static final int LOG_LEVEL_DEBUG  = 2;
    /** "Info" level logging = 3*/
    public static final int LOG_LEVEL_INFO   = 3;
    /** "Warn" level logging = 4 */
    public static final int LOG_LEVEL_WARN   = 4;
    /** "Error" level logging = 5 */
    public static final int LOG_LEVEL_ERROR  = 5;
    /** "Fatal" level logging = 6 */
    public static final int LOG_LEVEL_FATAL  = 6;

    /** Enable all logging levels  > 6 */
    public static final int LOG_LEVEL_ALL    = (LOG_LEVEL_IGNORE);

    /** Enable no logging levels */
    public static final int LOG_LEVEL_OFF    = (LOG_LEVEL_FATAL + 1);


    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel);

    /**
     * <p> Get logging level. </p>
     * @return int log level
     */
    public int getLevel();

    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     * @return boolean if given log currently enabled
     */
    public boolean isLevelEnabled(int logLevel);



}


