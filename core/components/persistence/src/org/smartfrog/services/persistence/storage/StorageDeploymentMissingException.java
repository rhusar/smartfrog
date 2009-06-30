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
 * StorageDeploymentMissingException is used to indicate that the storage
 * for a component was not found during deployment. This implies the storage is working, but
 * there is no persisted data for a component that was expected to be there.
 */
public class StorageDeploymentMissingException extends SmartFrogDeploymentException {
    
    public StorageDeploymentMissingException(String string, Throwable cause) {
        super(string, cause);
    }

    public StorageDeploymentMissingException(String string) {
        super(string);
    }

    public StorageDeploymentMissingException(Throwable cause) {
        super(cause);
    }
    

}
