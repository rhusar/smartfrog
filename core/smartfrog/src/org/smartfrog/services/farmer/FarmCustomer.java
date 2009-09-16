/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.farmer;

import java.rmi.Remote;

/**
 * This component can ask a farmer for instances.
 *
 * Although written for testing, it could be extended to be more useful
 */

public interface FarmCustomer extends Remote {
    String ATTR_ROLE = "role";
    String ATTR_DELETE_ON_TERMINATE = "deleteOnTerminate";
    String ATTR_FARMER = "farmer";
    String ATTR_MIN = "min";
    String ATTR_MAX = "max";
}
