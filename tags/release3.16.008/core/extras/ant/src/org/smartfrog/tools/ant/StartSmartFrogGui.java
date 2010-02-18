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
 * Start the smartfrog editor/management GUI
 * @ant.task category="SmartFrog" name="sf-gui"
 */
public class StartSmartFrogGui extends StartDaemon {

    /**
     * Override point: declare the name of the entry point of this task.
     * @see SmartFrogJVMProperties#GUI_ENTRY_POINT
     * @return name of the GUI class providing a static void Main(String args[]) method
     */
    protected String getEntrypoint() {
        return SmartFrogJVMProperties.GUI_ENTRY_POINT;
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
