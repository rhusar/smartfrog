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
package org.smartfrog.services.deployapi.transport.endpoints.system;

import org.apache.axis2.AxisFault;
import org.apache.axis2.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.xbeans.cddlm.api.TerminateRequestDocument;
import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.transport.endpoints.Processor;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.net.URI;
import java.rmi.RemoteException;

/**
 * process undeploy operation created Aug 4, 2004 4:04:20 PM
 */

public class TerminateProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(TerminateProcessor.class);

    public TerminateProcessor(XmlBeansEndpoint owner) {
        super(owner);
    }

    public OMElement process(OMElement request) throws AxisFault {
        return super.process(request);
    }

    public boolean terminate(TerminateRequestDocument.TerminateRequest undeploy)
            throws RemoteException {
        //TODO
        final URI appURI = null;
        if (appURI == null) {
            throw raiseBadArgumentFault(ERROR_NO_APPLICATION);
        }

        String reason;
        reason = undeploy.getReason();
        if (reason == null) {
            reason = "";
        }
        Job job = lookupJobNonFaulting(appURI);
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
     * @throws java.rmi.RemoteException
     */
    private boolean terminate(Job job, String reason)
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
