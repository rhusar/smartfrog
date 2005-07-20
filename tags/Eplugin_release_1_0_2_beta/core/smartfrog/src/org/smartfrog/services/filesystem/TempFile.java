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
package org.smartfrog.services.filesystem;

import java.rmi.RemoteException;

/**
 * This is an interface to a component that creates a temporary filename
 * @see java.io.File#createTempFile(java.lang.String, java.lang.String, java.io.File)
 * created 18-May-2004 11:36:25
 */

public interface TempFile extends FileUsingComponent {

    /**
     * any optional text
     */
    public static final String ATTR_TEXT = "text";
    /**
     * text encoding {@value}
    */
    public static final String ATTR_TEXT_ENCODING = "encoding";

    /**
     * temp filename prefix {@value}
    */
    public static final String ATTR_PREFIX="prefix";
    /**
     * temp filename suffix {@value}
    */
    public static final String ATTR_SUFFIX = "suffix";
    /**
     * temp file directory {@value}
    */
    public static final String ATTR_DIRECTORY = "dir";
    //this is the filename that is created
    //public static final String ATTR_FILENAME = "filename";

    /**
     * get the filename of this file
     *
     * @return
     */
    String getFilename() throws RemoteException ;

}
