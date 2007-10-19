package org.smartfrog.services.deployapi.transport.endpoints.alpine;

import nu.xom.Element;
import nu.xom.Elements;
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
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.DeploymentException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfUtils;
import org.smartfrog.services.deployapi.transport.wsrf.NotificationSubscription;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.xml.java5.iterators.ElementsIterator;

import javax.xml.namespace.QName;
import java.util.List;

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
        SoapElement request = getRequest(messageContext);
        String requestName = getRequestName(messageContext);
        QName qName = XsdUtils.makeQName(request);
        log.info("received " + qName);
        if (Constants.WSRF_OPERATION_GETRESOURCEPROPERTY.equals(requestName)) {
            GetResourceProperty(messageContext, request);
        }
        if (Constants.WSRF_OPERATION_GETMULTIPLERESOURCEPROPERTIES.equals(requestName)) {
            GetMultipleResourceProperties(messageContext, request);
        }
        if (Constants.WSRF_OPERATION_GETCURRENTMESSAGE
                .equals(requestName)) {
            throw FaultRaiser.raiseNotImplementedFault(qName.toString());
        }
        if (Constants.WSRF_OPERATION_SUBSCRIBE
                .equals(requestName)) {
            WSNSubscribe(messageContext,endpointContext);
        }
        //TODO: handle WSNT operations
        if (Constants.WSRF_ELEMENT_DESTROY_REQUEST.equals(requestName)) {
            verifyNamespace(getRequest(messageContext),
                    Constants.WSRF_WSRL_NAMESPACE);
            boolean processed=destroy(messageContext,endpointContext);
            if(processed) {
                setResponse(messageContext,
                    new Element(Constants.WSRF_ELEMENT_DESTROY_RESPONSE,
                            Constants.WSRF_WSRL_NAMESPACE));
            }
        }
    }

    /**
     * Handle a destroy operation
     * default operation is to raise a fault
     * @param messageContext
     * @param endpointContext
     */
    protected  boolean destroy(MessageContext messageContext,
                         EndpointContext endpointContext) {
        //throw FaultRaiser.raiseNotImplementedFault("<wsrf:destroy/>. This endpoint is not destroyable "+toString());
        log.debug("Skipping destruction of an indestructable endpoint "+toString());
        return false;
    }

    protected SoapElement getRequest(MessageContext messageContext) {
        return messageContext.getRequest().getPayload();
    }

    protected String getRequestName(MessageContext messageContext) {
        return messageContext.getRequest().getPayload().getLocalName();
    }

    /**
     * Get the operation of a request
     * @param messageContext
     * @return qname of the node of the request
     */
    protected QName getRequestOperation(MessageContext messageContext) {
        return XsdUtils.makeQName(getRequest(messageContext));
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
     * Resolve one property in a single or multiple request.
     *
     * @param messageContext
     * @param propertyRequest
     * @return
     */
    private List<Element> resolveOneProperty(MessageContext messageContext, Element propertyRequest) {
        //step1: get the resource source for this endpoint
        WSRPResourceSource source = retrieveResourceSource(messageContext);
        if (source == null) {
            throw new BaseException(Constants.F_WSRF_WSRP_UNKNOWN_RESOURCE);
        }
        //step2: get the qname out of the request
        QName property = extractQNameFromText(propertyRequest);
        //step3: resolve it
        List<Element> result = source.getProperty(property);
        if (result == null) {
            throw ResourceUnknownFault(property);
        }
        return result;
    }

    private QName extractQNameFromText(Element propertyRequest) {
        QName property;
        String value = propertyRequest.getValue();
        property = XsdUtils.resolveQName(propertyRequest, value, false);
        if (property == null) {
            throw invalidQNameException("Unable to resolve the qname in the request "
                    + propertyRequest.getValue());
        }
        return property;
    }


    /**
     * Implementation of GetResourceProperty
     *
     * @param messageContext
     * @param request
     * @return
     */
    public void GetResourceProperty(MessageContext messageContext, SoapElement request) {
        List<Element> result = resolveOneProperty(messageContext, request);

        //step4: build the response
        Element responseBody = WsrfUtils
                .WsRfRpElement(Constants.WSRF_RP_ELEMENT_GETRESOURCEPROPERTY_RESPONSE);
        for(Element e:result) {
            responseBody.appendChild(e.copy());
        }

        //step5: append to the new response message
        setResponse(messageContext, responseBody);
    }

    /**
     * Implementation of GetMultipleResourceProperties
     *
     * @param messageContext
     * @param request
     */
    public void GetMultipleResourceProperties(MessageContext messageContext, SoapElement request) {
        //build the response
        Element responseBody = WsrfUtils
                .WsRfRpElement(Constants.WSRF_RP_ELEMENT_GETMULTIPLERESOURCEPROPERTIES_RESPONSE);

        //iterate through elements
        Elements requests = request.getChildElements();
        ElementsIterator it=new ElementsIterator(requests);
        for(Element oneRequest:it) {
            QName propName=extractQNameFromText(oneRequest);
            SoapElement se = new SoapElement(propName);
            List<Element> result = resolveOneProperty(messageContext, oneRequest);
            for(Element e:result) {
                se.appendChild(e.copy());
            }
            responseBody.appendChild(se);
        }

        //append to the new response message
        setResponse(messageContext, responseBody);
    }

    /**
     * Set the response of a message to an XML element, and mark ourselves as processed
     * @param messageContext
     * @param responseBody
     */
    public void setResponse(MessageContext messageContext, Element responseBody) {
        messageContext.createResponse().getBody().appendChild(responseBody);

        //declare ourselves as processed
        messageContext.setProcessed(true);
    }


    public BaseException invalidQNameException(String qname) {
        log.error("Invalid QName : [" + qname + "]");
        BaseException baseException = new BaseException(Constants.F_WSRF_WSRP_INVALID_RESOURCE_PROPERTY_QNAME);
        baseException.setFaultReason("The qualified name is not valid in the context of this element: \""
                +qname
                +'"');
        return baseException;
    }

    /**
     * generate an unknown resource fault
     * @param qname
     * @return a new fault
     */
    public BaseException ResourceUnknownFault(QName qname) {
        log.error("Unknown QName : [" + qname + "]");
        BaseException baseException = new BaseException(Constants.F_WSRF_WSRP_UNKNOWN_RESOURCE);
        baseException.setFaultReason("Unknown resource "+qname.toString());
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
            throw new ServerException("Unknown message: " + operation);
        }
    }


    /**
     * Override point, handle a wsn subscription operation
     *
     * @param messageContext
     * @param endpointContext
     */
    public void WSNSubscribe(MessageContext messageContext,
                             EndpointContext endpointContext) {

        SoapElement payload = messageContext.getCurrentMessage().getPayload();
        NotificationSubscription subscription=new NotificationSubscription(payload);
        registerSubscription(messageContext,subscription);
        subscription.createSubscriptionURL(getServerInstance().getSubscriptionURL());
        SoapElement subscribeResponse = subscription.createSubscribeResponse();
        setResponse(messageContext,subscribeResponse);
    }

    /**
     * Get the server instance for this component
     * @return
     */
    public ServerInstance getServerInstance() {
        return ServerInstance.currentInstance();
    }


    /**
     * Override this for WS-Notification to work.
     * @param messageContext current message context
     * @param subscription
     */
    protected void registerSubscription(MessageContext messageContext,
                                        NotificationSubscription subscription) {
        throw FaultRaiser.raiseNotImplementedFault("Subscriptions to this endpoint is not supported");
    }


    /**
     * Get the To: component of a (mandatory) WS-A address
     * @param inMessage
     * @return the address
     * @throws org.smartfrog.projects.alpine.faults.ServerException if there is no address
     */
    public AlpineEPR getTo(MessageDocument inMessage) {
        AddressDetails addressDetails = inMessage.getAddressDetails();
        if(addressDetails==null) {
            throw new ServerException("No WS-Addressing details in message");
        }
        AlpineEPR to=addressDetails.getTo();
        return to;
    }


    protected void verifyTopic(QName expectedTopic, NotificationSubscription subscription) {
        QName topic = subscription.getTopic();
        if(!expectedTopic.equals(topic)) {
            throw FaultRaiser.raiseBadArgumentFault("Unsupported topic: ["+topic+"] - expected "+expectedTopic);
        }
    }


    /**
     * Returns a string representation of the object. I
     * @return a string representation of the object.
     */
    public String toString() {
        return "WSRF Handler";
    }
}
