/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.transport.endpoints.alpine;

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.faults.ServerException;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;
import org.smartfrog.services.deployapi.transport.endpoints.system.SystemProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.InitializeProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.TerminateProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.RunProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.PingProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.DestroyProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.system.AddFileProcessor;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.deployapi.transport.wsrf.NotificationSubscription;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.engine.Application;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.notifications.EventSubscriberManager;
import nu.xom.Element;

import javax.xml.namespace.QName;

/**
 * created 10-Apr-2006 10:54:30
 */

public class SystemHandler extends WsrfHandler {

    /**
     * Message handler
     *
     * @param messageContext
     * @param endpointContext
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    @Override
    public void process(MessageContext messageContext, EndpointContext endpointContext) {
        //hand off WSRF processing to the superclass
        super.process(messageContext, endpointContext);
        if (messageContext.isProcessed()) {
            //exit immediately if processing took place.
            return;
        }

        MessageDocument request = messageContext.getRequest();
        MessageDocument response = messageContext.createResponse();
        Element payload = request.getBody().getFirstChildElement();
        if (payload == null) {
            throw new ServerException("Empty SOAP message");
        }
        //get a processor
        SystemProcessor processor = createProcessor(payload);
        verifyProcessorSet(processor, payload);

        processor.setMessageContext(messageContext);
        //look up the job
        Application job = lookupJob(request);
        processor.setJob(job);
        //this is the pivot point; declare ourselves finished
        messageContext.setProcessed(true);
        processor.process(request, response);
    }

    /**
     * Look up a job
     *
     * @param inMessage
     * @return a job or null for no job matching that query found
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *          if the args are bad
     */
    protected Application lookupJob(MessageDocument inMessage) {
        JobRepository jobs = ServerInstance.currentInstance().getJobs();
        AlpineEPR to = getTo(inMessage);
        Application job = jobs.lookupJobFromEndpointer(to);
        return job;
    }

    /**
     * Create the relevant processor for this operation.
     *
     * @param operation name of the message
     * @return a processor
     */
    protected SystemProcessor createProcessor(Element operation) {
        String requestName = operation.getLocalName();
        SystemProcessor processor = null;
        if (Constants.API_ELEMENT_INITALIZE_REQUEST.equals(requestName)) {
            verifyDeployApiNamespace(operation);
            processor = new InitializeProcessor(this);
        }
        if (Constants.API_ELEMENT_TERMINATE_REQUEST.equals(requestName)) {
            verifyDeployApiNamespace(operation);
            processor = new TerminateProcessor(this);
        }
        if (Constants.API_ELEMENT_ADDFILE_REQUEST.equals(requestName)) {
            verifyDeployApiNamespace(operation);
            processor = new AddFileProcessor(this);
        }
        if (Constants.API_ELEMENT_RUN_REQUEST.equals(requestName)) {
            verifyDeployApiNamespace(operation);
            processor = new RunProcessor(this);
        }
        if (Constants.API_ELEMENT_PING_REQUEST.equals(requestName)) {
            verifyDeployApiNamespace(operation);
            processor = new PingProcessor(this);
        }
        if (Constants.API_ELEMENT_RESOLVE_REQUEST.equals(requestName)) {
            verifyDeployApiNamespace(operation);
            //processor = new LookupSystemProcessor(this);
        }
        if (Constants.API_ELEMENT_RESOLVE_REQUEST.equals(requestName)) {
            verifyDeployApiNamespace(operation);
            //processor = new LookupSystemProcessor(this);
        }
        if (Constants.WSRF_ELEMENT_DESTROY_REQUEST.equals(requestName)) {
            verifyNamespace(operation, Constants.WSRF_WSRL_NAMESPACE);
            processor = new DestroyProcessor(this);
        }
        return processor;
    }


    /**
     * The resource source for this message.
     * @param message
     * @return
     */
    @Override
    public WSRPResourceSource retrieveResourceSource(MessageContext message) {
        Application job = lookup(message);
        return job;
    }

    private Application lookup(MessageContext message) {
        Application job = lookupJob(message.getRequest());
        if (job == null) {
            throw FaultRaiser.raiseNoSuchApplicationFault("Unknown application");
        }
        return job;
    }

    /**
     * subscribe to the portal events
     *
     * @param messageContext current message context
     * @param subscription
     */
    protected void registerSubscription(MessageContext messageContext,
                                        NotificationSubscription subscription) {
        //verify the topic
        verifyTopic(Constants.SYSTEM_LIFECYCLE_EVENT, subscription);
        //register it per-server
        Application app=lookup(messageContext);
        app.getSubscribers().add(subscription);
        getServerInstance().getSubscriptionStore().add(subscription);
    }

    /**
     * Returns a string representation of the object. I
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "System";
    }

}
