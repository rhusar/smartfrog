package org.smartfrog.services.deployapi.transport.endpoints.alpine;

import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.core.Context;
import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.faults.ServerException;
import org.smartfrog.projects.alpine.handlers.HandlerBase;
import org.smartfrog.projects.alpine.interfaces.MessageHandler;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.DeploymentException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;

import javax.xml.namespace.QName;

/**
 * This is the WSRF message handler.
 * It has some helper functions, and a dispatcher that catches BaseExceptions
 * and turns them into SOAP Faults
 */
public abstract class WsrfHandler extends HandlerBase implements MessageHandler {

    private Log log = LogFactory.getLog(WsrfHandler.class);

    /**
     * Bind a handler to a context. This may include a SmartFrog binding, though
     * that depends upon the implementation
     *
     * @param context the context of the handler
     */
    public void bind(Context context) {

    }

    /**
     * Message handler
     *
     * @param messageContext
     * @param endpointContext
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public void processMessage(MessageContext messageContext,
                               EndpointContext endpointContext) {
        process(messageContext, endpointContext);
    }

    /**
     * Message handler
     *
     * @param messageContext
     * @param endpointContext
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public void process(MessageContext messageContext,
                        EndpointContext endpointContext) {
        MessageDocument inMessage = messageContext.getRequest();
        Element request = inMessage.getEnvelope().getBody().getFirstChildElement();
        String requestName = request.getLocalName();
        QName qName = XsdUtils.makeQName(request);
        log.info("received " + qName);
        if (Constants.WSRF_OPERATION_GETRESOURCEPROPERTY.equals(requestName)) {
            GetResourceProperty(messageContext, request);
        }
        if (Constants.WSRF_OPERATION_GETMULTIPLERESOURCEPROPERTIES.equals(requestName)) {
            throw FaultRaiser.raiseNotImplementedFault(qName.toString());
        }
        if (Constants.WSRF_OPERATION_GETCURRENTMESSAGE
                .equals(requestName)) {
            throw FaultRaiser.raiseNotImplementedFault(qName.toString());
        }
        if (Constants.WSRF_OPERATION_SUBSCRIBE
                .equals(requestName)) {
            throw FaultRaiser.raiseNotImplementedFault(qName.toString());
        }
    }


    /**
     * Return a resource source for this message.
     *
     * @param message
     * @return the source of resources. Return null for no match in that context
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *
     */
    public WSRPResourceSource retrieveResourceSource(MessageContext message) {
        return null;
    }

    String resolveNamespace(Element request, String prefix) {
        return request.getNamespaceURI(prefix);
    }

    /**
     * Implementation of GetResourceProperty
     *
     * @param messageContext
     * @param request
     * @return
     */
    public void GetResourceProperty(MessageContext messageContext, Element request) {
        //step1: get the resource source for this endpoint
        WSRPResourceSource source = retrieveResourceSource(messageContext);
        if (source == null) {
            throw new BaseException(Constants.F_WSRF_WSRP_UNKNOWN_RESOURCE);
        }
        //step2: get the qname out of the request
        QName property;
        String value = request.getValue();
        property = XsdUtils.resolveQName(request, value, false);
        if (property == null) {
            throw invalidQNameException("Unable to resolve the qname in the request " + request.getValue());
        }
        //step3: resolve it
        Element result = source.getProperty(property);
        if (result == null) {
            throw invalidQNameException(property.toString());
        }

        //step4: build the response
        Element responseBody = new SoapElement(
                "wsrf-rp:" + Constants.WSRF_RP_ELEMENT_GETRESOURCEPROPERTY_RESPONSE,
                Constants.WSRF_WSRP_NAMESPACE);
        responseBody.appendChild(result.copy());

        //step5: append to the new response message
        messageContext.createResponse().getBody().appendChild(responseBody);

        //declare ourselves as processed
        messageContext.setProcessed(true);
    }

    /**
     * convert text of the form abc:local to a qname, within the context of the specified request
     *
     * @param text
     * @param request
     * @return the qname
     * @throws BaseException with WSRF-specific faults if things don't work.
     */
    private QName textToQName(String text, Element request) {
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
        String ns = request.getNamespaceURI(prefix);
        if (ns == null) {
            log.error("Prefix does not match to any namespace");
            throw invalidQNameException(text);
        }
        QName qName = new QName(ns, local);
        return qName;
    }

    private BaseException invalidQNameException(String qname) {
        log.error("Invalid QName : [" + qname + "]");
        BaseException baseException = new BaseException(Constants.F_WSRF_WSRP_INVALID_RESOURCE_PROPERTY_QNAME);
        baseException.setFaultReason(qname);
        return baseException;
    }


    /**
     * check that the namespace is ok.
     * Special: Null namespaces are allowed on a get
     *
     * @param operation
     * @param expectedURI
     * @throws org.smartfrog.services.deployapi.transport.faults.DeploymentException
     *          if things are bad
     */
    public void verifyNamespace(Element operation, String expectedURI) {
        String requestURI = operation.getNamespaceURI();
        if (!expectedURI.equals(requestURI)) {
            throw new DeploymentException(String.format(
                    "Wrong request namespace: expected " + expectedURI +
                            " - got " + requestURI));
        }
    }

    protected void verifyDeployApiNamespace(Element operation) {
        verifyNamespace(operation, Constants.CDL_API_TYPES_NAMESPACE);
    }

    /**
     * if processor==null, raise an appropriate exception
     *
     * @param processor
     * @param operation
     */
    protected void verifyProcessorSet(AlpineProcessor processor, Element operation) {
        if (processor == null) {
            //if we get here: error
            String action = operation.getLocalName();
            throw new ServerException("Unknown message: " + operation);
        }
    }
}
