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
import org.ggf.xbeans.cddlm.api.TerminateResponseDocument;
import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.services.deployapi.binding.bindings.TerminateBinding;

import java.net.URI;
import java.rmi.RemoteException;

/**
 * process undeploy operation created Aug 4, 2004 4:04:20 PM
 */

public class TerminateProcessor extends SystemProcessor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(TerminateProcessor.class);

    public TerminateProcessor(XmlBeansEndpoint owner) {
        super(owner);
    }

    public OMElement process(OMElement request) throws RemoteException {
        TerminateBinding binding = new TerminateBinding();
        TerminateResponseDocument response = TerminateResponseDocument.Factory
                .newInstance();
        if(job!=null) {
            TerminateRequestDocument inDoc = binding.convertRequest(request);
            terminate(inDoc);
        }
        return binding.convertResponse(response);
    }


    public TerminateResponseDocument terminate(TerminateRequestDocument terminate)
            throws RemoteException {
        TerminateRequestDocument.TerminateRequest terminateRequest;
        terminateRequest = terminate.getTerminateRequest();
        String reason = terminateRequest.getReason();
        if (reason == null) {
            reason = "";
        }

        log.info("Terminating " + job.getId() + " with reason:" + reason);
        if (job.terminate(reason)) {
            //purge the store
            JobRepository jobs = ServerInstance.currentInstance().getJobs();
            jobs.remove(job);
        }

        TerminateResponseDocument response = TerminateResponseDocument.Factory
                .newInstance();
        return response;
    }




}
