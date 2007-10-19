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
package org.smartfrog.services.deployapi.transport.endpoints.system;

import nu.xom.Element;
import org.smartfrog.services.deployapi.engine.JobRepository;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.WsrfHandler;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import java.io.IOException;

/**
 * created 22-Sep-2005 15:41:33
 */

public class DestroyProcessor extends SystemProcessor {
    public static final String ERROR_SYSTEM_NOT_FOUND_TO_DESTROY = "system may already have been destroyed";

    public DestroyProcessor(WsrfHandler owner) {
        super(owner);
    }


    public Element process(SoapElement request) throws IOException {
        jobMustExist();
        JobRepository jobs = ServerInstance.currentInstance().getJobs();
        jobs.destroy(job);
        Element response = new Element(Constants.WSRF_ELEMENT_DESTROY_RESPONSE,
                Constants.WSRF_WSRL_NAMESPACE);
        return response;
    }
}
