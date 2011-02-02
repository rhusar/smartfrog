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
import static org.ggf.cddlm.generated.api.CddlmConstants.API_ELEMENT_ADDFILE_RESPONSE;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_ELEMENT_PING_REQUEST;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_ELEMENT_RUN_REQUEST;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_ELEMENT_TERMINATE_REQUEST;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_SYSTEM_OPERATION_INITIALIZE;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_SYSTEM_OPERATION_RUN;
import static org.ggf.cddlm.generated.api.CddlmConstants.API_SYSTEM_OPERATION_TERMINATE;
import static org.ggf.cddlm.generated.api.CddlmConstants.CDL_API_TYPES_NAMESPACE;
import static org.ggf.cddlm.generated.api.CddlmConstants.PROPERTY_SYSTEM_SYSTEM_STATE;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.services.deployapi.binding.UriListType;
import org.smartfrog.services.deployapi.binding.XomHelper;
import static org.smartfrog.services.deployapi.binding.XomHelper.apiElement;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.notifications.muws.MuwsEventReceiver;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;
import java.util.List;

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

    public SystemSession(WsrfSession parent, AlpineEPR endpoint) {
        super(parent,endpoint);
    }

    /**
     * Build a session from a returned reference
     *
     * @param parent
     * @param root
     */
    public SystemSession(WsrfSession parent, Element root) {
        super(parent,null);
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
        //return queue(API_SYSTEM_OPERATION_PING, request);
        return queue(request);
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
    public LifecycleStateEnum getLifecycleState() {
        List<Element> result = getResourcePropertyList(PROPERTY_SYSTEM_SYSTEM_STATE);
        SoapElement parent = (SoapElement) result.get(0);
        return Utils.parseCmpState(parent);
    }

    /**
     * make an run request
     *
     * @return the response
     */
    public MessageDocument run()  {
        SoapElement request;
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
    public MessageDocument initialize(SoapElement request)  {
        return invokeBlocking(API_SYSTEM_OPERATION_INITIALIZE,
                request);
    }

    /**
     * End the add-file operation
     * @param transmission
     * @return
     */
    public UriListType endAddFile(Transmission transmission) {
        transmission.blockForResult(getTimeout());
        Element response = extractResponse(transmission,
                new QName(CDL_API_TYPES_NAMESPACE, API_ELEMENT_ADDFILE_RESPONSE));
        UriListType list=new UriListType(response);
        return list;
    }

    /**
     * submit an add file request, block until it is finished
     * @param request
     * @return the list of URIs
     */
    public UriListType addFile(SoapElement request) {
        //Transmission transmission = queue(API_SYSTEM_OPERATION_ADDFILE, request);
        Transmission transmission = queue(request);
        return endAddFile(transmission);
    }

    /**
     * Subscribe to the lifecycle events (blocking call)
     *
     * @param callback  callback URL
     * @param useNotify notify?
     * @return the new subscription
     */
    public CallbackSubscription subscribeToLifecycleEvents(String callback, boolean useNotify) {
        return subscribe(Constants.SYSTEM_LIFECYCLE_EVENT, callback, useNotify, null);
    }

    /**
     * Subscribe to the lifecycle events (blocking call)
     *
     * @param receiver receiver
     * @return the new subscription, bound to the receiver
     */
    public CallbackSubscription subscribeToLifecycleEvents(MuwsEventReceiver receiver) {
        CallbackSubscription  sub=subscribe(Constants.SYSTEM_LIFECYCLE_EVENT, receiver.getURL(), true, null);
        sub.setReceiver(receiver);
        return sub;
    }

}
