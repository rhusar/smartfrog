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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.engine.JobRepository;
import org.smartfrog.services.cddlm.engine.JobState;
import org.smartfrog.services.cddlm.engine.ServerInstance;
import org.smartfrog.services.cddlm.generated.api.types.TerminateRequest;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * process undeploy operation created Aug 4, 2004 4:04:20 PM
 */

public class TerminateProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(TerminateProcessor.class);


    public TerminateProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    public boolean terminate(TerminateRequest undeploy)
            throws RemoteException {
        final URI appURI = undeploy.getApplication();
        if (appURI == null) {
            throw raiseBadArgumentFault(ERROR_NO_APPLICATION);
        }

        String reason;
        reason = undeploy.getReason();
        if (reason == null) {
            reason = "";
        }
        JobState job = lookupJobNonFaulting(appURI);
        if (job == null) {
            //job was not found, this is not an error.
            return true;
        }
        log.info("terminating " + job.getName() + " for " + reason);
        if (terminate(job, reason)) {
            //purge the store
            JobRepository jobs = ServerInstance.currentInstance().getJobs();
            jobs.remove(appURI);
            return true;
        }
        return false;
    }

    /**
     * Terminate a job
     *
     * @param job    job to kill
     * @param reason why
     * @return
     * @throws RemoteException
     */
    private boolean terminate(JobState job, String reason)
            throws RemoteException {
        Prim target = job.resolvePrimNonFaulting();
        if (target == null) {
            log.info("job already terminated");
            return true;
        }
        TerminationRecord termination;
        termination =
                new TerminationRecord(TerminationRecord.NORMAL, reason, null);
        target.sfTerminate(termination);
        job.enterTerminatedStateNotifying(termination);
        return true;
    }


}
