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
package org.smartfrog.services.deployapi.transport.endpoints.portal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.xbeans.cddlm.api.CreateRequestDocument;
import org.ggf.xbeans.cddlm.api.CreateResponseDocument;
import org.ggf.xbeans.cddlm.api.CreateResponseDocument.CreateResponse;
import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.endpoints.Processor;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.services.deployapi.transport.endpoints.system.OptionProcessor;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

/**
 * This class is *NOT* re-entrant. Create one for each deployment. created Aug
 * 4, 2004 3:58:37 PM
 */

public class CreateProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(CreateProcessor.class);

    private CreateRequestDocument.CreateRequest request;
    private OptionProcessor options;
    private Job job;
    public static final String ERROR_NO_DESCRIPTOR = "No descriptor element";

    public CreateProcessor(XmlBeansEndpoint owner) {
        super(owner);
    }

    public CreateRequestDocument.CreateRequest getRequest() {
        return request;
    }

    public OptionProcessor getOptions() {
        return options;
    }

    /**
     * deployment
     *
     * @param createRequest
     * @return
     * @
     */
    public CreateResponse create(CreateRequestDocument.CreateRequest createRequest) {

        JobRepository repository;
        repository = ServerInstance.currentInstance().getJobs();
        //hostname processing
        String hostname = Constants.LOCALHOST;
        if (createRequest.isSetHostname()) {
            hostname = createRequest.getHostname();
        }

        job = repository.createNewJob(hostname);

        //create a new jobstate
        request = createRequest;

        if (!Constants.LOCALHOST.equals(hostname)) {
            throw new BaseException(Constants.ERROR_CREATE_UNSUPPORTED_HOST);
        }
        //create a new response
        CreateResponseDocument responseDoc = CreateResponseDocument.Factory.newInstance();
        CreateResponseDocument.CreateResponse response = responseDoc.addNewCreateResponse();
        response.setSystemReference(job.getEndpoint());
        response.setResourceId(job.getId());


        return response;
    }


}
