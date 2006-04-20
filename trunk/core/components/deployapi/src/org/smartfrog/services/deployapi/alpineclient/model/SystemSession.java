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
import static org.ggf.cddlm.generated.api.CddlmConstants.*;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.transport.Session;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.system.Constants;

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
     * Create an inline request.
     * @param language URI of the language
     * @param descriptor a descriptor which must not have any parent.
     * @param options a list of options, can be null
     * @return
     */
    public SoapElement createInitRequestInline(
            String language,
            Element descriptor, List<Element> options) {
        SoapElement body=XomHelper.apiElement("body",descriptor);
        SoapElement dt = XomHelper.apiElement("descriptor",body);
        XomHelper.addApiAttr(dt, "language", language);
        return completeInitRequest(dt, options);
    }

    /**
     * finish off an init requset
     * @param dt descriptor type
     * @param options list of options, can be null
     * @return
     */
    private SoapElement completeInitRequest(SoapElement dt, List<Element> options) {
        SoapElement request;
        request = XomHelper.apiElement(API_ELEMENT_INITALIZE_REQUEST,dt);
        if(options!=null) {
            //add any options
            SoapElement ot = XomHelper.apiElement("options");
            for(Element e:options) {
                ot.appendChild(e);
            }
            request.appendChild(ot);
        }
        return request;
    }


    public SoapElement createInitRequestURL(String language,
                                            String descriptorURL, List<Element> options) {
        SoapElement ref = XomHelper.apiElement("reference", descriptorURL);
        SoapElement dt = XomHelper.apiElement("descriptor", ref);
        XomHelper.addApiAttr(dt, "language", language);
        return completeInitRequest(dt, options);
    }
}
