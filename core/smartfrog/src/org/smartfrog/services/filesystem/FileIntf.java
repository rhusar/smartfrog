/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.filesystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * created 27-May-2004 10:43:10
 */

public interface FileIntf extends UriIntf, FileUsingComponent, Remote {

    /**
     * {@value}
     */
    String ATTR_DIR = "dir";

    /**
     * {@value}
     */
    String ATTR_EXISTS = "exists";

    /**
     * {@value}
     */
    String ATTR_IS_DIRECTORY = "isDirectory";

    /**
     * {@value}
     */
    String ATTR_IS_FILE = "isFile";

    /**
     * {@value}
     */
    String ATTR_IS_HIDDEN = "isHidden";

    /**
     * {@value}
     */
    String ATTR_TIMESTAMP = "timestamp";

    /**
     * {@value}
     */
    String ATTR_LENGTH = "length";

    /**
     * {@value}
     */
    String ATTR_IS_EMPTY = "isEmpty";

    /**
     * {@value}
     */
    String ATTR_SHORTNAME = "shortname";

    /**
     * {@value}
     */
    String ATTR_MUST_EXIST = "mustExist";

    /**
     * {@value}
     */
    String ATTR_MUST_READ = "mustRead";

    /**
     * {@value}
     */
    String ATTR_MUST_WRITE = "mustWrite";

    /**
     * {@value}
     */
    String ATTR_MUST_BE_FILE = "mustBeFile";

    /**
     * {@value}
     */
    String ATTR_MUST_BE_DIR = "mustBeDir";

    /**
     * {@value}
     */
    String ATTR_TEST_ON_STARTUP = "testOnStartup";

    /**
     * {@value}
     */
    String ATTR_TEST_ON_LIVENESS = "testOnLiveness";

    /**
     * {@value}
     */
    String ATTR_DELETE_ON_EXIT = "deleteOnExit";

    /**
     * get the absolute path of this file
     *
     * @return String
     * @throws RemoteException In case of network/rmi error
     */
    public String getAbsolutePath() throws RemoteException;

}
