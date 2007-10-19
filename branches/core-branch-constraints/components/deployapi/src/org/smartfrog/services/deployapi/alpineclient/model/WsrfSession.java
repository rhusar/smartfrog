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
import org.smartfrog.projects.alpine.wsa.MessageIDSource;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfUtils;

import javax.xml.namespace.QName;
import java.util.List;


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

    public static final QName QNAME_WSRF_GET_MULTIPLE_PROPERTIES = new QName(
            CddlmConstants.WSRF_WSRP_NAMESPACE,
            CddlmConstants.WSRF_RP_ELEMENT_GETMULTIPLERESOURCEPROPERTIES_REQUEST);

    public static final QName QNAME_WSRF_GET_MULTIPLE_PROPERTIES_RESPONSE = new QName(
            CddlmConstants.WSRF_WSRP_NAMESPACE,
            CddlmConstants.WSRF_RP_ELEMENT_GETMULTIPLERESOURCEPROPERTIES_RESPONSE);

    public static final QName QNAME_WSRF_RL_DESTROY_REQUEST = new QName(
            CddlmConstants.WSRF_WSRL_NAMESPACE,
            CddlmConstants.WSRF_ELEMENT_DESTROY_REQUEST);
    public static final QName QNAME_WSRF_RL_DESTROY_RESPONSE = new QName(
            CddlmConstants.WSRF_WSRL_NAMESPACE,
            CddlmConstants.WSRF_ELEMENT_DESTROY_RESPONSE);
    public static final QName QNAME_WSNT_SUBSCRIBE_RESPONSE = new QName(
            CddlmConstants.WSRF_WSNT_NAMESPACE,
            CddlmConstants.WSNT_ELEMENT_SUBSCRIBE_RESPONSE);

    /**
     * construct a new session
     * @param endpoint endpoint
     * @param validating should you validate docs
     * @param queue queue to use
     */
    protected WsrfSession(AlpineEPR endpoint, boolean validating, TransmitQueue queue) {
        super(endpoint, null, validating);
        setMessageIDSource(new MessageIDSource());
        setQueue(queue);
    }

    /**
     * Derive a new session from an existing session, sharing the existing
     * transmission queue and copying role and validation flags
     * @param parent parent endpoint
     * @param endpoint remote endpoint (can be null)
     */
    protected WsrfSession(WsrfSession parent, AlpineEPR endpoint) {
        super(endpoint, parent.getRole(), parent.isValidating());
        setMessageIDSource(new MessageIDSource());
        setQueue(parent.getQueue());
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
    * {@link Constants#WS_ADDRESSING_NAMESPACE} namespace for WSA, and
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
     * @return the response
     */
    protected Element extractResponse(Transmission tx, QName expectedType) {
        MessageDocument response = tx.getResponse();
        Element payload = response.getPayload();
        AlpineRuntimeException fault = null;
        if (payload == null) {
            fault = new ClientException("Empty body of SOAP message");
        } else if (!XsdUtils.isNamed(payload, expectedType)) {
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
     * @param property qname of the property to look up
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
        request.appendChild(prefix + ':' + property.getLocalPart());
        return queue(getSoapAction(request), request);
    }


    /**
     * Finish the WSRF_RP GetResourceProperty request
     *
     * @param tx
     * @return the contents of the response.
     */
    public List<Element> endGetResourceProperty(Transmission tx) {
        tx.blockForResult(getTimeout());
        extractResponse(tx, QNAME_WSRF_GET_PROPERTY_RESPONSE);
        Element payload = tx.getResponse().getPayload();
        List<Element> resultList=XsdUtils.makeList(payload.getChildElements());
        return resultList;
    }

    /**
     * blocking call to get a request property.
     * This call expects a single element as the reply, not a list, and will fail if
     * less or more comes back
     *
     * @param property property to retrieve
     * @return a single element 
     * @throws ClientException if the number of children received !=1
     */
    public Element getResourcePropertySingle(QName property) {
        return getResourcePropertySingle(property,true);
    }

    /**
     * blocking call to get a request property.
     * This call expects a single element as the reply, not a list, and will fail if
     * less or more comes back
     *
     * @param property property to retrieve
     * @param required a flag to state whether the property is required or not
     * @return a single element or null if none and required==false
     * @throws ClientException if the number of children received !=1
     */
    public Element getResourcePropertySingle(QName property,boolean required) {
        Transmission tx = beginGetResourceProperty(property);
        List<Element> elements = endGetResourceProperty(tx);
        AlpineRuntimeException fault=null;
        if (elements.isEmpty()) {
            if(!required) {
                return null;
            } else {
                fault = new ClientException("No child element in the response to the request");
            }

        } else if (elements.size() > 1) {
            fault = new ClientException("Too many children in response");
        }
        if(fault!=null) {
            tx.addMessagesToFault(fault);
            getLog().error(fault.toString(),fault);
            throw fault;
        }
        return elements.get(0);
    }

    public List<Element> getResourcePropertyList(QName property) {
        Transmission tx = beginGetResourceProperty(property);
        List<Element> elements = endGetResourceProperty(tx);
        return elements;
    }

    /**
     * Get the text value of a resource property
     * @param property property to retrieve
     * @return the value or null for an empty response
     */
    public String getResourcePropertyValue(QName property) {
        Element e=getResourcePropertySingle(property);
        if(e==null) {
            return null;
        }
        return e.getValue();
    }

    /**
     * Start a WSRF_RP GetResourceProperty request
     *
     * @param properties a list of properties to get
     * @return the started transmission
     */
    public Transmission beginGetMultipleResourceProperties(List<QName> properties) {
        SoapElement request;
        request = WsrfUtils.WsRfRpElement(CddlmConstants.WSRF_RP_ELEMENT_GETMULTIPLERESOURCEPROPERTIES_REQUEST);

        for(QName property:properties) {
            //add the namespace
            SoapElement child=WsrfUtils.WsRfRpElement("ResourceProperty");
            String prefix = property.getPrefix();
            if (prefix.length() == 0) {
                prefix = PRIVATE_NAMESPACE;
            }
            child.addNamespaceDeclaration(prefix, property.getNamespaceURI());
            //and the value
            child.appendChild(prefix + ':' + property.getLocalPart());
            //add the child to the graph
            request.appendChild(child);
        }
        return queue(getSoapAction(request), request);
    }


    /**
     * end the transmission. This returns the payload, which contains the
     * nested elements which can then be asked for by qname.
     * @param tx
     * @return the result
     */
    public Element endGetMultipleResourceProperties(Transmission tx) {
        tx.blockForResult(getTimeout());
        return extractResponse(tx, QNAME_WSRF_GET_MULTIPLE_PROPERTIES_RESPONSE);
    }

    public Element getMultipleResourceProperties(List<QName> properties) {
        return endGetMultipleResourceProperties(beginGetMultipleResourceProperties(properties));
    }

    /**
     * Destroy an endpoint
     *
     * @return the transmission
     */
    public Transmission beginDestroy() {
        SoapElement request;
        request = new SoapElement(QNAME_WSRF_RL_DESTROY_REQUEST);
        return queue(request);
    }

    /**
     * End a destroy operation by awaiting the result, then verifying that
     * the answer was of the right type
     *
     * @param tx transmission to wait for
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

    /**
     * subscribe to an endpoint
     * @param topic qname to subscribe to
     * @param callback epr for messages
     * @param useNotify use notify flag
     * @param expiryTime optional expiry time
     * @return the transmission
     */
    public Transmission beginSubscribe(QName topic, String callback, boolean useNotify, String expiryTime) {
        AlpineEPR epr=new AlpineEPR(callback);
        SoapElement subscription=WsrfUtils.createSubscriptionRequest(epr, topic, useNotify, expiryTime);
        return queue(subscription);
    }

    /**
     * End the subscription, extract the EPR. This is validated
     * @param tx
     * @return
     */
    public CallbackSubscription endSubscribe(Transmission tx) {
        tx.blockForResult(getTimeout());
        Element payload = extractResponse(tx, QNAME_WSNT_SUBSCRIBE_RESPONSE);
        return new CallbackSubscription(this,payload);
    }

    /**
     * subscribe to an endpoint
     * @param topic qname to subscribe to
     * @param callback epr for messages
     * @param useNotify use notify flag
     * @param expiryTime optional expiry time
     * @return the transmission
     */
    public CallbackSubscription subscribe(QName topic, String callback, boolean useNotify, String expiryTime) {
        return endSubscribe(beginSubscribe(topic, callback, useNotify, expiryTime));
    }

    /**
     * Invoke an operation with blocking
     * @param operation the operation to invoke
     * @param request the request
     * @return the response
     */
    public MessageDocument invokeBlocking(String operation,SoapElement request) {
        Transmission transmission = queue(operation, request);
        return transmission.blockForResult(getTimeout());
    }

    /**
     * For use in the toString method; return the session type
     *
     * @return the type of this session, e,g "SOAP", "WSRF"
     */
    protected String sessionType() {
        return "WSRF";
    }
}
