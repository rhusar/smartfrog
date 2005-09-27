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

package org.smartfrog.services.deployapi.transport.wsrf;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.om.OMFactory;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.smartfrog.services.deployapi.binding.bindings.GetResourcePropertyBinding;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.services.deployapi.transport.endpoints.Processor;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyDocument;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.rmi.RemoteException;


/**
 * Implement WSRP
 */
public abstract class WsrfEndpoint extends XmlBeansEndpoint {

    private static Log log = LogFactory.getLog(WsrfEndpoint.class);

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
        OMElement request = inMessage.getEnvelope().getBody().getFirstElement();
        String requestName = request.getLocalName();
        QName qName = request.getQName();
        log.info("received " + qName);
        //verifyDeployApiNamespace(qName);
        if (Constants.WSRF_OPERATION_GETRESOURCEPROPERTY.equals(requestName)) {
            return GetResourceProperty(inMessage, request);
        }
        return null;
    }


    /**
     * Return a resource source for this message.
     *
     * @param message
     * @return the source of resources. Return null for no match in that context
     * @throws BaseException
     */
    public WSRPResourceSource retrieveResourceSource(MessageContext message) {
        return null;
    }

    OMNamespace resolveNamespace(OMElement request, String prefix) {
        Iterator namespaces = request.getAllDeclaredNamespaces();
        while (namespaces.hasNext()) {
            OMNamespace omNamespace = (OMNamespace) namespaces.next();
            if (prefix.equals(omNamespace.getPrefix())) {
                return omNamespace;
            }
        }
        //no match
        return null;
    }

    /**
     * Implementation of GetResourceProperty
     *
     * @param message
     * @param request
     * @return
     * @throws AxisFault
     */
    public OMElement GetResourceProperty(MessageContext message, OMElement request) throws AxisFault {
        //step1: get the resource source for this endpoint
        WSRPResourceSource source = retrieveResourceSource(message);
        if (source == null) {
            throw new BaseException(Constants.F_WSRF_WSRP_UNKNOWN_RESOURCE);
        }
        GetResourcePropertyBinding binding = new GetResourcePropertyBinding();
        GetResourcePropertyDocument requestDoc = binding.convertRequest(request);
        QName qName = requestDoc.getGetResourceProperty();
        XmlObject result = source.getResource(qName);
        if (result == null) {
            throw invalidQNameException(qName.toString());
        }
        //conver to Axiom
        OMElement resultElement= Axis2Beans.convertDocument(result);
        //add a wrapper

        OMElement response = Utils.createOmElement(
                Constants.WSRF_WSRP_NAMESPACE,
                Constants.WSRF_RP_ELEMENT_GETRESOURCEPROPERTY_RESPONSE,
                "wsrf-rp");
        response.addChild(resultElement);
        return response;
    }

    /**
     * convert text of the form abc:local to a qname, within the context of the specified request
     *
     * @param text
     * @param request
     * @return the qname
     * @throws BaseException with WSRF-specific faults if things don't work.
     */
    protected QName textToQName(String text, OMElement request) {
        if (text == null) {
            throw invalidQNameException("");
        }
        text = text.trim();
        int prefixIndex = text.indexOf(":");
        if (prefixIndex == -1) {
            throw invalidQNameException(text);
        }
        String prefix = text.substring(0, prefixIndex);
        String local = text.substring(prefixIndex + 1);
        if (prefix.length() == 0 || local.length() == 0) {
            throw invalidQNameException(text);
        }
        OMNamespace ns = resolveNamespace(request, prefix);
        if (ns == null) {
            log.error("Prefix does not match to any namespace");
            throw invalidQNameException(text);
        }
        QName qName = new QName(ns.getName(), local);
        return qName;
    }

    private BaseException invalidQNameException(String qname) {
        log.error("Invalid Qname : [" + qname + "]");
        BaseException baseException = new BaseException(Constants.F_WSRF_WSRP_INVALID_RESOURCE_PROPERTY_QNAME);
        baseException.setFaultReason(qname);
        return baseException;
    }

    public OMElement GetMultipleResourceProperties(MessageContext message, OMElement request) throws AxisFault {
        return null;
    }

    public OMElement Subscribe(MessageContext message, OMElement request) throws AxisFault {
        return null;
    }

    public OMElement GetCurrentMessage(MessageContext message, OMElement request) throws AxisFault {
        return null;
    }

    /**
     * get the bit of a request that matters
     * @param inMessage
     * @return the first element in the body
     */
    protected OMElement getRequestBody(MessageContext inMessage) {
        return inMessage.getEnvelope().getBody().getFirstElement();
    }

    /**
     * if processor==null, raise an appropriate exception
     * @param processor
     * @param operation
     * @throws org.apache.axis2.AxisFault
     */
    protected void verifyProcessorSet(Processor processor, QName operation) throws
            AxisFault {
        if (processor == null) {
            //if we get here: error
            String action = operation.getLocalPart();
            throw new AxisFault("Unknown message: " + action);
        }
    }
}
