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

package org.smartfrog.tools.ant;

/**
 * Start the SmartFrog management console
 * @ant.task category="SmartFrog" name="sf-managementconsole" 
 * */
public class StartManagementConsole extends StartDaemon {

    /**
     * Override point: declare the name of the entry point of this task.
     *
     * @return name of the class providing a static void Main(String args[]) method
     * @see SmartFrogJVMProperties#MANAGEMENT_ENTRY_POINT
     */
    protected String getEntrypoint() {
        return SmartFrogJVMProperties.MANAGEMENT_ENTRY_POINT;
    }

    /**
     * override point
     *
     * @return default timeout, return 1 number less than 0 for no timeout
     */
    protected long getDefaultTimeout() {
        return NO_DEFAULT_TIMEOUT;
    }
}
