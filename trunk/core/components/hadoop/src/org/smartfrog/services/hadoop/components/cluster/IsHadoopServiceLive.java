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

import org.apache.hadoop.util.Service;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.workflow.conditional.Condition;
import org.smartfrog.sfcore.workflow.conditional.conditions.AbstractTargetedCondition;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 *
 * Created 11-Aug-2008 15:48:25
 *
 */

public class IsHadoopServiceLive extends AbstractTargetedCondition implements Condition {

    public static final String ATTR_SERVICE = "service";
    public static final String ATTR_SERVICE_STATE = "serviceState";
    public static final String ATTR_SERVICE_DESCRIPTION = "serviceDescription";
    private static final Reference refTarget = new Reference(ATTR_TARGET);


    public IsHadoopServiceLive() throws RemoteException {
    }


    /**
     * startup-time resolution
     * @throws RemoteException              for network problems
     * @throws SmartFrogResolutionException if the target does not resolve
     */
    @Override
    protected void resolveTargetOnStartup() throws SmartFrogResolutionException, RemoteException {
        super.resolveTargetOnStartup();
        getService();
    }

    /**
     * Get the service
     * @return the service
     * @throws RemoteException for network problems
     * @throws SmartFrogResolutionException if the target does not resolven
     */
    public HadoopService getService() throws SmartFrogResolutionException, RemoteException {
        Prim prim = getTarget();
        if (!(prim instanceof HadoopService)) {
            String error = "Unable to bind to a component that is not a Hadoop service: "
                    + prim.sfCompleteName();
            throw new SmartFrogResolutionException(refTarget, sfCompleteNameSafe(), error);
        }
        return (HadoopService) prim;
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
        HadoopService service;
        try {
            service = getService();
        } catch (SmartFrogResolutionException e) {
            //resolution problem; log at debug level
            sfLog().debug("Failed to resolve service", e);
            setFailureCause(e);
            return false;
        }
        Service.ServiceState state = service.getServiceState();
        sfReplaceAttribute(ATTR_SERVICE_STATE, state.toString());
        String description = service.getDescription();
        sfReplaceAttribute(ATTR_SERVICE_DESCRIPTION, description);
        return evalOrFail(service.isServiceLive(),"service is not live "+description);
    }
}
