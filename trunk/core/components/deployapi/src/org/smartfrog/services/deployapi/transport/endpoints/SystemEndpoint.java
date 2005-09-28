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
package org.smartfrog.services.deployapi.transport.endpoints;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.xbeans.cddlm.api.InitializeRequestDocument;
import org.ggf.xbeans.cddlm.api.InitializeResponseDocument;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.smartfrog.services.deployapi.binding.bindings.TerminateBinding;
import org.smartfrog.services.deployapi.binding.bindings.InitializeBinding;
import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.endpoints.system.InitializeProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.TerminateProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.SystemProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.PingProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.RunProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.CreateProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.ResolveProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.LookupSystemProcessor;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfEndpoint;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;

import javax.xml.namespace.QName;
import java.net.URL;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

/*
* System EPR
 */;

public class SystemEndpoint extends WsrfEndpoint {

    Log log= LogFactory.getLog(SystemEndpoint.class);
    
    /**
     * deliver a message
     *
     * @param operation
     * @param inMessage
     * @return the body of the response
     * @throws org.apache.axis2.AxisFault
     * @throws BaseException              unchecked basefault
     */
    public OMElement dispatch(QName operation, MessageContext inMessage)
            throws RemoteException {
        OMElement result = super.dispatch(operation, inMessage);
        if (result != null) {
            return result;
        }
        OMElement request = getRequestBody(inMessage);
        String requestName = request.getLocalName();
        verifyDeployApiNamespace(request.getQName());
        SystemProcessor processor = createProcessor(requestName);
        verifyProcessorSet(processor, operation);

        processor.setMessageContext(inMessage);
        Job job = lookupJob(inMessage);
        processor.setJob(job);
        return processor.process(request);
    }

    
    /**
     * Look up a job
     * @param inMessage
     * @return a job or null for no job matching that query found
     * @throws BaseException if the args are bad
     */
    protected Job lookupJob(MessageContext inMessage) {
        JobRepository jobs = ServerInstance.currentInstance().getJobs();
        EndpointReference to = inMessage.getTo();
        Job job=jobs.lookupJobFromEndpointer(to);
        return job;
    }

    /**
     * Create the relevant processor for this operation.
     *
     * @param requestName
     * @return a processor
     */
    protected SystemProcessor createProcessor(String requestName) {
        SystemProcessor processor = null;
        if (Constants.API_ELEMENT_INITALIZE_REQUEST.equals(requestName)) {
            processor = new InitializeProcessor(this);
        }
        if (Constants.API_ELEMENT_TERMINATE_REQUEST.equals(requestName)) {
            processor = new TerminateProcessor(this);
        }
        if (Constants.API_ELEMENT_ADDFILE_REQUEST.equals(requestName)) {
            //processor = new LookupSystemProcessor(this);
        }
        if (Constants.API_ELEMENT_RUN_REQUEST.equals(requestName)) {
            processor = new RunProcessor(this);
        }
        if (Constants.API_ELEMENT_PING_REQUEST.equals(requestName)) {
            processor = new PingProcessor(this);
        }
        if (Constants.API_ELEMENT_RESOLVE_REQUEST.equals(requestName)) {
            //processor = new LookupSystemProcessor(this);
        }
        return processor;
    }


    public WSRPResourceSource retrieveResourceSource(MessageContext message) {
        Job job = lookupJob(message);
        if(job==null) {
            throw FaultRaiser.raiseNoSuchApplicationFault("Unknown application");
        }
        return job;
    }

}
