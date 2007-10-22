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

import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.faults.ServerException;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.services.deployapi.engine.Application;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.DeploymentException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;

import java.io.IOException;
import java.net.URI;

/**
 * This is a fork of the original endpoints.processsor class, one that works in a pure alpine mode.
 * created Aug 4, 2004 3:59:42 PM
 */

public abstract class AlpineProcessor extends FaultRaiser {
    private static final Log log = LogFactory.getLog(AlpineProcessor.class);

    public AlpineProcessor(WsrfHandler owner) {
        this.owner = owner;
    }

    /**
     * our owner
     */
    private WsrfHandler owner;

    private MessageContext messageContext;

    public WsrfHandler getOwner() {
        return owner;
    }


    public MessageContext getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
    }


    /**
     * look up a job in the repository
     *
     * @param jobURI
     * @return the jobstate reference
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *          if there is no such job
     */
    public Application lookupJob(URI jobURI) throws BaseException {
        Application job = lookupJobNonFaulting(jobURI);
        if (job == null) {
            throw raiseNoSuchApplicationFault(
                    ERROR_APP_URI_NOT_FOUND + jobURI.toString());
        }
        return job;
    }

    /**
     * map from URI to job
     *
     * @param jobURI seach uri
     * @return job or null for no match
     */

    public Application lookupJobNonFaulting(URI jobURI) {
        JobRepository jobs = ServerInstance.currentInstance().getJobs();
        Application job = jobs.lookup(jobURI);
        return job;
    }

    /**
     * look up a job by resid
     *
     * @param resourceId
     * @return
     * @throws org.smartfrog.services.deployapi.transport.faults.DeploymentException
     *          if there is no match
     */
    protected Application lookupJob(String resourceId) {
        ServerInstance server = ServerInstance.currentInstance();
        Application job = server.getJobs().lookup(resourceId);
        if (job == null) {
            throw new DeploymentException(Constants.F_NO_SUCH_APPLICATION);
        }
        return job;
    }


    /**
     * Process an incoming message.
     *
     * @param request
     * @param response
     * @return the response message, with body and headers set up.
     */
    public void process(MessageDocument request, MessageDocument response) {
        SoapElement payload = request.getPayload();
        if (payload == null) {
            throw new ServerException("Empty SOAP message");
        }
        try {
            //process the message
            Element body = process(payload);
            //append the payload under the response
            if (body != null) {
                response.getBody().appendChild(body);
            }
        } catch (IOException e) {
            throw new BaseException(CddlmConstants.F_RUNTIME_EXCEPTION, e);
        }
    }


    /**
     * Override point: process the body of a message.
     *
     * @param payload received contents of the SOAP Body
     * @return the body of the response or null for an empty response
     */
    public Element process(SoapElement payload) throws IOException {
        //do nothing
        return null;
    }

}
