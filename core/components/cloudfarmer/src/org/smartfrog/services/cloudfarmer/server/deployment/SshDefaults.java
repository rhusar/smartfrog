/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.server.deployment;

/**
 * Created 06-May-2010 10:29:43
 */


public interface SshDefaults {
    int SLEEP_TIME = 15;
    /**
     * Time to sleep waiting for sfStart : {@value}
     */
    int STARTUP_SLEEP_TIME = 15;
    /**
     * #of times to proble for the start command {@value}
     */
    int STARTUP_LOCATE_ATTEMPTS = 6;
    /**
     * #of times to proble for the ping command {@value}
     */
    int STARTUP_PING_ATTEMPTS = 4;
    /**
     * Time to sleep waiting for sfPing : {@value}
     */
    int STARTUP_PING_SLEEP_TIME = 30;
    String ERROR_NO_EXECUTABLE = " Error: no executable ";
    int SLEEP_TIME_FOR_HOSTNAME_RESOLUTION = 100;
}
