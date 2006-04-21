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
package org.smartfrog.services.deployapi.alpineclient.model;

import nu.xom.Element;
import nu.xom.Document;
import static org.ggf.cddlm.generated.api.CddlmConstants.*;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.transport.Session;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.binding.XomHelper;
import static org.smartfrog.services.deployapi.binding.XomHelper.apiElement;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.LifecycleStateEnum;

import java.util.List;
import java.rmi.RemoteException;
import java.io.IOException;

/**
 * created 10-Apr-2006 17:08:08
 */

public class SystemSession extends WsrfSession {

    /**
     * cached resource id
     */
    private String resourceId;

    public SystemSession(AlpineEPR endpoint, boolean validating, TransmitQueue queue) {
        super(endpoint, validating, queue);
    }

    public SystemSession(Session parent, AlpineEPR endpoint) {
        super(endpoint, parent.isValidating(), parent.getQueue());
    }

    /**
     * Build a session from a returned reference
     *
     * @param parent
     * @param root
     */
    public SystemSession(Session parent, Element root) {
        super(null, parent.isValidating(), parent.getQueue());
        resourceId = XomHelper.getElementValue(root,
                "api:ResourceId");
        Element address = XomHelper.getElement(root,
                "api:systemReference");
        AlpineEPR epr = new AlpineEPR(address, Constants.WS_ADDRESSING_NAMESPACE);
        bind(epr);
    }

    public Transmission beginPing() {
        SoapElement request;
        request = XomHelper.apiElement(API_ELEMENT_PING_REQUEST);
        return queue(API_SYSTEM_OPERATION_PING, request);
    }

    public Element endPing(Transmission tx) {
        MessageDocument response = tx.blockForResult(getTimeout());
        return response.getPayload();
    }

    public Element ping() {
        return endPing(beginPing());
    }


    /**
     * Get the lifecycle state
     * @return
     * @throws RemoteException
     */
    public LifecycleStateEnum getLifecycleState() throws RemoteException {
        String value = getResourcePropertyValue(PROPERTY_SYSTEM_SYSTEM_STATE);
        LifecycleStateEnum state =
                LifecycleStateEnum.extract(value);
        return state;
    }

    protected MessageDocument invokeBlocking(String operation,Element request) {
        Transmission transmission = queue(operation, request);
        return transmission.blockForResult(getTimeout());

    }

    /**
     * make an run request
     *
     * @return the response
     */
    public MessageDocument run()  {
        Element request;
        request = apiElement(API_ELEMENT_RUN_REQUEST);
        MessageDocument document = invokeBlocking(API_SYSTEM_OPERATION_RUN,
                request);
        return document;
    }

    /**
     * terminate the app; it will still exist
     *
     * @param reason
     * @return
     */
    public MessageDocument terminate(String reason) {
        SoapElement request;
        request = apiElement(API_ELEMENT_TERMINATE_REQUEST);
        if (reason != null) {
            SoapElement er = apiElement("reason",reason);
            request.appendChild(er);
        }
        return invokeBlocking(API_SYSTEM_OPERATION_TERMINATE,
                request);
    }

    /**
     * make an init request
     *
     * @param request
     * @return the response
     */
    public MessageDocument initialize(Element request)  {
        return invokeBlocking(API_SYSTEM_OPERATION_INITIALIZE,
                request);
    }


}
