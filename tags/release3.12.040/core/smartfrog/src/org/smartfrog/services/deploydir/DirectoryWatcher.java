/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deploydir;

import java.rmi.Remote;

/**
 * Created 07-Mar-2008 16:26:34
 */


public interface DirectoryWatcher extends Remote {

    /**
     * Directory to watch
     */
    String ATTR_DIRECTORY = "dir";

    /**
     * Scan interval; 0 means don't rescan
     */
    String ATTR_INTERVAL = "interval";

}
