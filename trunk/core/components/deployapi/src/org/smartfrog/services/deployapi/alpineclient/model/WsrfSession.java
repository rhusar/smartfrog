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
     * Check that there was a body in the response and that it was of the expected type.
     *
     * @param tx           transmission containing the response and the To: address in the request
     * @param expectedType qname of the expected root element of the message
     */
    protected void checkResponseMessageType(Transmission tx, QName expectedType) {
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
    }

    /**
     * Start a WSRF_RP GetResourceProperty request
     *
     * @param property
     * @return the started transmission
     */
    public Transmission startGetResourceProperty(QName property) {
        Element request;
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
        checkResponseMessageType(tx, QNAME_WSRF_GET_PROPERTY_RESPONSE);
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
        return endGetResourceProperty(startGetResourceProperty(property));
    }

    /**
     * test for an endpoint having muws capabilities. This call does not
     * talk to the server, it just processes the results (so is static)
     *
     * @param capabilities
     * @param uri
     * @return true iff the capability is found
     */
    public static boolean hasMuwsCapability(Element capabilities, String uri) {
        for (Element e : XsdUtils.elements(capabilities, CddlmConstants.PROPERTY_MUWS_MANAGEABILITY_CAPABILITY)) {
            if (uri.equals(e.getValue())) {
                return true;
            }
        }
        //failure
        return false;
    }

}
