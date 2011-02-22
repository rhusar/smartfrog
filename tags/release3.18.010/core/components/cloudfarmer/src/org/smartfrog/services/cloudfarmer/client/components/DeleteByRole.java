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
package org.smartfrog.services.cloudfarmer.client.components;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 20-Nov-2009 11:34:09
 */

public class DeleteByRole extends AbstractFarmWorkflowClient implements FarmCustomer {

    public DeleteByRole() throws RemoteException {
    }

    protected String getSuccessText() {
        return "Cluster started";
    }

    protected void startupAction() throws IOException, SmartFrogException {
        String role = sfResolve(ATTR_ROLE, (String) null, true);
        if (!role.isEmpty()) {
            getFarmer().deleteAllInRole(role);
        } else {
            getFarmer().deleteAll();
        }
    }

}