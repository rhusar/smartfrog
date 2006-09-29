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
package org.smartfrog.services.deployapi.axis2.endpoints;

import nu.xom.Document;
import nu.xom.Element;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.om.OMElement;
import org.smartfrog.services.deployapi.engine.Application;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.DeploymentException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.axis2.Axis2Utils;

import java.io.IOException;
import java.net.URI;

/**
 * created Aug 4, 2004 3:59:42 PM
 */

public class Processor extends FaultRaiser {
    private static final Log log = LogFactory.getLog(Processor.class);

    public Processor(SmartFrogAxisEndpoint owner) {
        this.owner = owner;
    }

    /**
     * our owner
     */
    private SmartFrogAxisEndpoint owner;

    private MessageContext messageContext;

    public SmartFrogAxisEndpoint getOwner() {
        return owner;
    }


    public MessageContext getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    private static URI makeRuntimeException(String url,
                                            Exception e) {
        log.error("url", e);
        throw new RuntimeException(url, e);
    }




    /**
     * look up a job in the repository
     *
     * @param jobURI
     * @return the jobstate reference
     * @throws BaseException if there is no such job
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
     * @throws DeploymentException if there is no match
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
     * override this for AXIOM-AXIOM processing.
     * the default hands it off to {@link #process(Document)}
     * @param request
     * @return the response
     * @throws IOException
     */
    public OMElement process(OMElement request) throws IOException {
        Document document = Axis2Utils.axiomToXom(request);
        return Axis2Utils.xomToAxiom(process(document));
    }

    /**
     * override this for Xom-based processing
     * @param request
     * @return the response
     * @throws IOException
     */
    public Element process(Document request) throws IOException {
        throwNotImplemented();
        return null;
    }


}
