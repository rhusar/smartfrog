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

/**
 * Created 14-Apr-2008 16:53:17
 */


public interface RpmErrors {
    String ERROR_NO_SUCH_FILE = "No such file ";
    String ERROR_NOT_AN_RPM_FILE = "Not an RPM file ";
    String ERROR_UNABLE_TO_INSTALL = "Unable to Install RPM package ";
    String ERROR_UNABLE_TO_UNINSTALL = "Unable to Uninstall RPM package ";
    String ERROR_UNABLE_TO_UPGRADE = "Unable to Upgrade RPM package ";
}
