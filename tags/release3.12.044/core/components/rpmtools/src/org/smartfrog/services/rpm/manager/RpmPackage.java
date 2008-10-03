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
package org.smartfrog.services.rpm.manager;

import java.rmi.Remote;
import java.util.Vector;

/**
 * Created 25-Apr-2008 14:53:06
 */


public interface RpmPackage extends Remote {


    /**
     * RPM name
     */
    public String ATTR_NAME="name";

    /**
     * RPM Version string
     */
    public String ATTR_VERSION="version";

    /**
     * Any description for error messages
     */
    public String ATTR_DESCRIPTION="description";

    /**
     * Filename of the RPM
     */
    public String ATTR_RPMFILE = "rpmFile";

    /**
     * Delete the file during termination?
     */
    public String ATTR_DELETEONTERMINATION="deleteOnTermination";

    /**
     * A list of managed files
     */
    public String ATTR_FILES ="files";

}
