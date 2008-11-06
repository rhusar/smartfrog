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
package org.smartfrog.services.hadoop.components.cluster;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.conditional.Condition;
import org.apache.hadoop.util.Service;

import java.rmi.RemoteException;

/**
 *
 * Created 11-Aug-2008 15:48:25
 *
 */

public class IsHadoopServiceLive extends PrimImpl implements Condition {

    public static final String ATTR_SERVICE = "service";
    public static final String ATTR_SERVICE_STATE = "serviceState";
    public static final String ATTR_SERVICE_DESCRIPTION = "serviceDescription";

    private HadoopService service;

    public IsHadoopServiceLive() throws RemoteException {
    }

    /**
     * Get the service
     * @return the service or null
     */
    public HadoopService getService() {
        return service;
    }

    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        service = (HadoopService) sfResolve(ATTR_SERVICE, (Prim) null, true);
    }

    /**
     * set the service to null
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        service = null;
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    //@Override
    public boolean evaluate() throws RemoteException, SmartFrogException {
        Service.ServiceState state = service.getServiceState();
        sfReplaceAttribute(ATTR_SERVICE_STATE, state.toString());
        sfReplaceAttribute(ATTR_SERVICE_DESCRIPTION, service.getDescription());
        return service.isServiceLive();
    }
}
