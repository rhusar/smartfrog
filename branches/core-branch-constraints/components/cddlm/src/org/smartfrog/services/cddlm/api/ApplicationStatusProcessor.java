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
package org.smartfrog.services.cddlm.api;

import org.apache.axis.types.URI;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.engine.JobState;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleStateEnum;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * created Aug 4, 2004 4:28:35 PM
 */

public class ApplicationStatusProcessor extends Processor {
    public ApplicationStatusProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    /**
     * app status processor does a ping to see if the dest is still alive
     *
     * @param applicationStatus
     * @return
     * @throws RemoteException
     */
    public ApplicationStatusType applicationStatus(
            ApplicationStatusRequest applicationStatus)
            throws RemoteException {
        URI reference = applicationStatus.getApplication();
        JobState job = lookupJob(reference);
        Prim process = job.resolvePrimFromJob();
        ApplicationStatusType status = job.createApplicationStatus();
        boolean running;
        try {
            process.sfPing(null);
            running = true;
        } catch (SmartFrogLivenessException e) {
            running = false;
            status.setStateInfo(e.toString());
        }
        String statusName = running ? "running" : "terminated";
        status.setState(LifecycleStateEnum.fromString(statusName));

        return status;
    }

}
