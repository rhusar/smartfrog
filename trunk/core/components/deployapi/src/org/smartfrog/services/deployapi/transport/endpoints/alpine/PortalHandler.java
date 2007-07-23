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
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.faults.ServerException;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.endpoints.portal.CreateProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.LookupSystemProcessor;
import org.smartfrog.services.deployapi.transport.endpoints.portal.ResolveProcessor;
import org.smartfrog.services.deployapi.transport.wsrf.NotificationSubscription;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;

/**
 * created 10-Apr-2006 10:54:18
 */

public class PortalHandler extends WsrfHandler {


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
        super.process(messageContext,endpointContext);
        if(messageContext.isProcessed()) {
            //exit immediately if processing took place.
            return;
        }

        MessageDocument request = messageContext.getRequest();
        MessageDocument response = messageContext.createResponse();
        Element payload=request.getBody().getFirstChildElement();
        if (payload == null) {
            throw new ServerException("Empty SOAP message");
        }
        String requestName = payload.getLocalName();
        verifyDeployApiNamespace(payload);

        //if we are here the request is in the deploy api xmlns
        AlpineProcessor processor = createProcessor(requestName);
        verifyProcessorSet(processor, payload);

        processor.setMessageContext(messageContext);
        //this is the pivot point; declare ourselves finished
        messageContext.setProcessed(true);
        processor.process(request,response);
    }

    /**
     * Create the relevant processor for this operation.
     *
     * @param requestName
     * @return the processor or null for no match
     */
    protected AlpineProcessor createProcessor(String requestName) {
        AlpineProcessor processor = null;
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
     *
     * @param message
     * @return the source of resources. Return null for no match in that context
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *
     */
    public WSRPResourceSource retrieveResourceSource(MessageContext message) {
        return getServerInstance();
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
        verifyTopic(Constants.PORTAL_CREATED_EVENT, subscription);
        getServerInstance().subscribeToPortalEvents(subscription);
    }

    /**
     * Returns a string representation of the object. I
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "Portal";
    }

}
