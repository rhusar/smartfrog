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
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.om.OMElement;
import org.ggf.xbeans.cddlm.api.CreateRequestDocument;
import org.ggf.xbeans.cddlm.api.CreateResponseDocument;
import org.ggf.xbeans.cddlm.api.LookupSystemRequestDocument;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.smartfrog.services.deployapi.binding.bindings.LookupSystemBinding;
import org.smartfrog.services.deployapi.transport.endpoints.portal.CreateProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.PortalProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.ResolveProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.LookupSystemProcessor;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfEndpoint;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;

import javax.xml.namespace.QName;

/**
 */
public class PortalEndpoint extends WsrfEndpoint {


    /**
     * deliver a message
     *
     * @param operation
     * @param inMessage
     * @return the body of the response
     * @throws AxisFault
     * @throws BaseException unchecked basefault
     */
    public OMElement dispatch(QName operation, MessageContext inMessage) throws AxisFault {
        OMElement result = super.dispatch(operation, inMessage);
        if (result != null) {
            return result;
        }

        verifyDeployApiNamespace(operation);
        String action = operation.getLocalPart();
        PortalProcessor processor=null;

        OMElement request = inMessage.getEnvelope().getBody().getFirstElement();
        if (Constants.API_ELEMENT_CREATE_REQUEST.equals(action)) {
            processor=new CreateProcessor(this);
        }
        if (Constants.API_ELEMENT_RESOLVE_REQUEST.equals(action)) {
            processor = new ResolveProcessor(this);
        }
        if (Constants.API_ELEMENT_LOOKUPSYSTEM_REQUEST.equals(action)) {
            processor = new LookupSystemProcessor(this);
        }
        if(processor==null) {
            //if we get here: error
            throw new AxisFault("Unknown message: "+ action);
        } else {
            return processor.process(request);
        }
    }

}
