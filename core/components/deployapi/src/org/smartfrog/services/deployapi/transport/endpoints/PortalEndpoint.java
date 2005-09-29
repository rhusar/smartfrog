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
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.endpoints.portal.CreateProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.LookupSystemProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.ResolveProcessor;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfEndpoint;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;

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
    public OMElement dispatch(QName operation, MessageContext inMessage)
            throws RemoteException {
        OMElement result = super.dispatch(operation, inMessage);
        if (result != null) {
            return result;
        }


        OMElement request = getRequestBody(inMessage);
        String requestName = request.getLocalName();
        verifyDeployApiNamespace(request.getQName());
        Processor processor = createProcessor(requestName);
        verifyProcessorSet(processor, operation);

        processor.setMessageContext(inMessage);
        return processor.process(request);
    }

    /**
     * Create the relevant processor for this operation.
     *
     * @param requestName
     * @return
     */
    protected Processor createProcessor(String requestName) {
        Processor processor = null;
        if (Constants.API_ELEMENT_CREATE_REQUEST.equals(requestName)) {
            processor = new CreateProcessor(this);
        }
        if (Constants.API_ELEMENT_PORTALRESOLVE_REQUEST.equals(requestName)) {
            processor = new ResolveProcessor(this);
        }
        if (Constants.API_ELEMENT_LOOKUPSYSTEM_REQUEST.equals(requestName)) {
            processor = new LookupSystemProcessor(this);
        }
        return processor;
    }


    /**
     * Return a resource source for this message.
     * we hand all this off to the server instance, it knowing these things
     * @param message
     * @return the source of resources. Return null for no match in that context
     * @throws BaseException
     */
    public WSRPResourceSource retrieveResourceSource(MessageContext message) {
        return ServerInstance.currentInstance();
    }
}