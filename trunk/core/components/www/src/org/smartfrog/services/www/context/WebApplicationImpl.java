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
package org.smartfrog.services.www.context;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * A WAR File
 */
public class WebApplicationImpl extends ApplicationServerContextImpl implements JavaWebApplication {

    public WebApplicationImpl() throws RemoteException {
    }

    /**
     * subclasses must implement this to deploy their component.
     * It is called during sfDeploy, after we have bound to a server
     *
     * @return the context
     * @throws RemoteException  In case of network/rmi error
     * @throws SmartFrogException error while deploying
     */
    protected ApplicationServerContext deployThisComponent() throws RemoteException, SmartFrogException {

        return getServer().deployWebApplication(this);
    }



    /**
     * Check the target file exists
     *
     * @throws SmartFrogException error while validating
     * @throws RemoteException In case of network/rmi error
     */
    @Override
    protected void validateDuringStartup()
            throws SmartFrogException, RemoteException {
        String filename = FileUsingComponentImpl.bind(this, true, null);
        FileSystem.requireFileToExist(filename,false,0);
    }
}
