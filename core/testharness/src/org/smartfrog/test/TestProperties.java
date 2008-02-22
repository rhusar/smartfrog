/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test;

/** Created 12-Jul-2007 16:13:41 */


public interface TestProperties {

    /**
     * Default port for RMI callbacks: {@value}
     */
    int RMI_CALLBACKS_PORT = 3802;

    /**
     * Name of the property that defines the port for RMI callbacks {@value}
     */
    String PROPERTY_TEST_RMI_CALLBACKS_PORT = "test.rmi.callbacks.port";

    int TIMEOUT = 30000;
    /**
     * Default startup timeout: {@value}
     */
    int STARTUP_TIMEOUT = TIMEOUT;
    /**
     * Default execute timeout: {@value}
     */
    int EXECUTE_TIMEOUT = TIMEOUT;
    /**
     * configuration point: {@value}
     */
    String TEST_TIMEOUT_STARTUP = "test.timeout.startup";
    /**
     * configuration point: {@value}
     */
    String TEST_TIMEOUT_EXECUTE = "test.timeout.execute";
}
