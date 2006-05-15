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

import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.engine.Application;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.AlpineProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.WsrfHandler;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import java.util.Locale;

/**
 * This class is *NOT* re-entrant. Create one for each deployment. created Aug
 * 4, 2004 3:58:37 PM
 */

public class CreateProcessor extends AlpineProcessor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(CreateProcessor.class);

    private Application job;
    public static final String ERROR_NO_DESCRIPTOR = "No descriptor element";

    public CreateProcessor(WsrfHandler owner) {
        super(owner);
    }


    /**
     * Override point: process the body of a message.
     *
     * @param rootElement received contents of the SOAP Body
     * @return the body of the response or null for an empty response
     */
    public Element process(SoapElement rootElement) {
        JobRepository repository;
        repository = ServerInstance.currentInstance().getJobs();
        //hostname processing
        String hostname = Constants.LOCALHOST;
        Element host =
                XomHelper.getElement(rootElement, "api:hostname", false);
        if(host!=null) {
            hostname=host.getValue().trim()
                    .toLowerCase(Locale.ENGLISH);
        }
        //REVISIT: delete
        /* turning this off to see what happens.

        if (!Constants.LOCALHOST.equals(hostname)
                && !Constants.LOCALHOST_IPV4.equals(hostname)) {
            throw new BaseException(Constants.F_UNSUPPORTED_CREATION_HOST);
        }
        */

        job = repository.createNewJob(hostname);

        Element response=XomHelper.apiElement("createResponse");
        Element resID=XomHelper.apiElement(Constants.RESOURCE_ID);
        resID.appendChild(job.getId());
        Element address=(Element) job.getEndpointer().copy();
        XomHelper.adopt(address, Constants.SYSTEM_REFERENCE);
        response.appendChild(resID);
        response.appendChild(address);
        return response;
    }
}


