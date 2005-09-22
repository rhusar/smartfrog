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
package org.smartfrog.services.deployapi.transport.endpoints.portal;

import org.apache.axis2.AxisFault;
import org.apache.axis2.om.OMElement;
import org.ggf.xbeans.cddlm.api.LookupSystemRequestDocument;
import org.ggf.xbeans.cddlm.api.LookupSystemResponseDocument;
import org.smartfrog.services.deployapi.binding.bindings.LookupSystemBinding;
import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.endpoints.Processor;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.services.deployapi.transport.faults.DeploymentException;

/**
 * created 21-Sep-2005 10:37:53
 */

public class LookupSystemProcessor extends Processor {

    public LookupSystemProcessor(XmlBeansEndpoint owner) {
        super(owner);
    }


    public OMElement process(OMElement request) throws AxisFault {
        LookupSystemBinding binding = new LookupSystemBinding();
        LookupSystemRequestDocument lookupSystemRequestDocument = binding.convertRequest(request);
        LookupSystemRequestDocument.LookupSystemRequest lookupSystemRequest = lookupSystemRequestDocument
                .getLookupSystemRequest();
        ServerInstance server = ServerInstance.currentInstance();
        String resourceId = lookupSystemRequest.getResourceId();
        Job job = server.getJobs().lookup(resourceId);
        if (job == null) {
            throw new DeploymentException(Constants.F_NO_SUCH_APPLICATION);
        }
        LookupSystemResponseDocument response = binding.createResponse();
        response.setLookupSystemResponse(job.getEndpoint());
        return binding.convertResponse(response);
    }
}
