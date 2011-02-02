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

package org.smartfrog.services.persistence.storage;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

/**
 * StorageDeploymentAccessException is used to indicate that the Storage adapter was unable 
 * to access with the storage implementation during deployment. this does is not a subtype of
 * StroageException as it has to subtype SmartfrogDeploymentException so that it can 
 * be passed through SmartFrog deployment methods. Otherwise StorageAccessException could have 
 * been used in its place.
 */
public class StorageDeploymentAccessException extends SmartFrogDeploymentException {
    
    public StorageDeploymentAccessException(String string, Throwable cause) {
        super(string, cause);
    }

    public StorageDeploymentAccessException(String string) {
        super(string);
    }

    public StorageDeploymentAccessException(Throwable cause) {
        super(cause);
    }
    

}
