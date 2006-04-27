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
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.faults.ClientException;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.transport.Session;
import org.smartfrog.projects.alpine.transport.Transmission;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;
import org.smartfrog.services.deployapi.system.Constants;

import javax.xml.namespace.QName;

/**
 * Base class for commonality for {@link PortalSession} and {@link SystemSession}
 * created 10-Apr-2006 17:12:06
 */

public abstract class WsrfSession extends Session {

    /**
     * Default timeout in milliseconds
     * {@value}
     */
    public static long DEFAULT_TIMEOUT = 30000;
    /**
     * private XMLNS used inside requests
     */
    public static final String PRIVATE_NAMESPACE = "getprop_ns";
    public static final QName QNAME_WSRF_GET_PROPERTY = new QName(
            CddlmConstants.WSRF_WSRP_NAMESPACE,
            CddlmConstants.WSRF_RP_ELEMENT_GETRESOURCEPROPERTY_REQUEST);

    public static final QName QNAME_WSRF_GET_PROPERTY_RESPONSE = new QName(
            CddlmConstants.WSRF_WSRP_NAMESPACE,
            CddlmConstants.WSRF_RP_ELEMENT_GETRESOURCEPROPERTY_RESPONSE);

    public static final QName QNAME_WSRF_RL_DESTROY_REQUEST = new QName(
            CddlmConstants.WSRF_WSRL_NAMESPACE,
            CddlmConstants.WSRF_ELEMENT_DESTROY_REQUEST);
    public static final QName QNAME_WSRF_RL_DESTROY_RESPONSE = new QName(
            CddlmConstants.WSRF_WSRL_NAMESPACE,
            CddlmConstants.WSRF_ELEMENT_DESTROY_RESPONSE);

    protected WsrfSession(AlpineEPR endpoint, boolean validating, TransmitQueue queue) {
        super(endpoint, null, validating);
        setQueue(queue);
    }


    private long timeout = DEFAULT_TIMEOUT;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


    /**
     * Bind to an address; forces us to use the
     * {@link Constants.WS_ADDRESSING_NAMESPACE} namespace for WSA, and
     * mark the headers as MustUnderstand.
     *
     * @param endpoint
     */
    public void bind(AlpineEPR endpoint) {
        super.bind(endpoint);
        getAddress().setMustUnderstand(true);
        getAddress().setNamespace(Constants.WS_ADDRESSING_NAMESPACE);
    }


    /**
     * Check that there was a body in the response and that it was of the expected type.
     * A ClientException is raised if it is the wrong type
     * @param tx           transmission containing the response and the To: address in the request
     * @param expectedType qname of the expected root element of the message
     */
    protected Element extractResponse(Transmission tx, QName expectedType) {
        MessageDocument response = tx.getResponse();
        Element payload = response.getPayload();
        AlpineRuntimeException fault = null;
        if (payload == null) {
            fault = new ClientException("Empty body of SOAP message");
        }
        if (!XsdUtils.isNamed(payload, expectedType)) {
            fault = new ClientException("Wrong response message");
        }
        if (fault != null) {
            tx.addMessagesToFault(fault);
            throw fault;
        }
        return payload;
    }

    /**
     * Start a WSRF_RP GetResourceProperty request
     *
     * @param property
     * @return the started transmission
     */
    public Transmission beginGetResourceProperty(QName property) {
        SoapElement request;
        request = new SoapElement(QNAME_WSRF_GET_PROPERTY);
        //add the namespace

        String prefix = property.getPrefix();
        if (prefix.length() == 0) {
            prefix = PRIVATE_NAMESPACE;
        }
        request.addNamespaceDeclaration(prefix, property.getNamespaceURI());
        //and the value
        request.appendChild(prefix + ":" + property.getLocalPart());
        return queue(CddlmConstants.WSRF_OPERATION_GETRESOURCEPROPERTY, request);
    }


    /**
     * Finish the WSRF_RP GetResourceProperty request
     *
     * @param tx
     * @return the contents of the response.
     */
    public Element endGetResourceProperty(Transmission tx) {
        tx.blockForResult(getTimeout());
        extractResponse(tx, QNAME_WSRF_GET_PROPERTY_RESPONSE);
        Element payload = tx.getResponse().getPayload();
        Element child = XsdUtils.getFirstChildElement(payload);
        if (child == null) {
            AlpineRuntimeException fault;
            fault = new ClientException("No child element in the response to the request");
            tx.addMessagesToFault(fault);
            throw fault;
        }
        return child;
    }

    /**
     * blocking call to get a request property
     *
     * @param property
     * @return
     */
    public Element getResourceProperty(QName property) {
        return endGetResourceProperty(beginGetResourceProperty(property));
    }

    public String getResourcePropertyValue(QName property) {
        Element e=getResourceProperty(property);
        if(e==null) {
            return null;
        }
        return e.getValue();
    }

    /**
     * Destroy an endpoint
     *
     * @return the transmission
     */
    public Transmission beginDestroy() {
        SoapElement request;
        request = new SoapElement(QNAME_WSRF_RL_DESTROY_REQUEST);
        return queue(CddlmConstants.WSRF_OPERATION_DESTROY, request);
    }

    /**
     * End a destroy operation by awaiting the result, then verifying that
     * the answer was of the right type
     *
     * @param tx
     */
    public void endDestroy(Transmission tx) {
        tx.blockForResult(getTimeout());
        extractResponse(tx, QNAME_WSRF_RL_DESTROY_RESPONSE);
    }

    /**
     * Blocking destroy operation
     */
    public void destroy() {
        endDestroy(beginDestroy());
    }
}
