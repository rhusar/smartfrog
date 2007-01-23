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
import static org.ggf.cddlm.generated.api.CddlmConstants.API_ELEMENT_LOOKUPSYSTEM_REQUEST;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.system.Constants;

import javax.xml.namespace.QName;

/**
 * created 10-Apr-2006 17:07:57
 */

public class PortalSession extends WsrfSession {
    public static final QName QNAME_LOOKUP_SYSTEM_RESPONSE = new QName(
            CddlmConstants.CDL_API_TYPES_NAMESPACE,
            CddlmConstants.API_ELEMENT_LOOKUPSYSTEM_RESPONSE);

    public static final QName QNAME_CREATE_SYSTEM_RESPONSE = new QName(
            CddlmConstants.CDL_API_TYPES_NAMESPACE,
            CddlmConstants.API_ELEMENT_CREATE_RESPONSE);

    /**
     * Package scoped constructor.
     *
     * @param endpoint
     * @param validating
     * @param queue
     */
    public PortalSession(AlpineEPR endpoint, boolean validating, TransmitQueue queue) {
        super(endpoint, validating, queue);
    }

    /**
     * Look up a system by ID
     * @param id resource ID
     * @return the transmission
     */
    public Transmission beginLookupSystem(String id) {
        SoapElement resid = XomHelper.apiElement("ResourceId", id);
        SoapElement request;
        request = XomHelper.apiElement(API_ELEMENT_LOOKUPSYSTEM_REQUEST, resid);
        return queue(request);
    }

    /**
     * wait for something to finish. this is where any errors get raised
     *
     * @param tx transmission
     * @return the system session looked up
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *          for trouble
     */
    public SystemSession endLookupSystem(Transmission tx) {
        MessageDocument response = tx.blockForResult(getTimeout());
        extractResponse(tx, QNAME_LOOKUP_SYSTEM_RESPONSE);
        Element payload = response.getPayload();
        //get the WSA address back
        AlpineEPR epr=new AlpineEPR(payload,CddlmConstants.WS_ADDRESSING_NAMESPACE);
        return new SystemSession(this, epr);
    }

    /**
     * Blocking implementation of the lookup system operation
     *
     * @param id system to look up
     * @return a system
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *          for trouble
     */
    public SystemSession lookupSystem(String id) {
        return endLookupSystem(beginLookupSystem(id));
    }

    /**
     * Create a session on a remote system.
     *
     * @param hostname is an optional hostname for the system
     * @return the queued transmission
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *          for trouble
     */
    public Transmission beginCreate(String hostname) {
        SoapElement request;
        request = XomHelper.apiElement(Constants.API_ELEMENT_CREATE_REQUEST);
        if (hostname != null) {
            Element child = XomHelper.apiElement("hostname", hostname);
            request.appendChild(child);
        }
        return queue(request);
    }

    /**
     * Wait for the creation operation to complete
     *
     * @param tx
     * @return the created session
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *          for trouble
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *          for trouble
     */
    public SystemSession endCreate(Transmission tx) {
        MessageDocument response = tx.blockForResult(getTimeout());
        extractResponse(tx, QNAME_CREATE_SYSTEM_RESPONSE);
        Element payload = response.getPayload();
        return new SystemSession(this, payload);
    }

    /**
     * Blocking create operation
     *
     * @param hostname hostname, can be null
     * @return a session
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *          for trouble
     */
    public SystemSession create(String hostname) {
        return endCreate(beginCreate(hostname));
    }

    /**
     * Subscribe to the portal events (blocking call)
     * @param callback callback URL
     * @param useNotify notify?
     * @return the new subscription
     */
    public CallbackSubscription subscribeToPortalEvents(String callback, boolean useNotify) {
        return subscribe(Constants.PORTAL_CREATED_EVENT, callback, useNotify, null);
    }
}
