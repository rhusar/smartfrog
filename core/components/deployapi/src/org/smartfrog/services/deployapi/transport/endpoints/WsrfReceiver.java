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

package org.smartfrog.services.deployapi.transport.endpoints;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.MessageInformationHeaders;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.OperationDescription;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.engine.DependencyManager;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;
import org.apache.axis2.soap.SOAP11Constants;
import org.apache.axis2.soap.SOAP12Constants;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wsdl.WSDLService;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.sfcore.security.SFClassLoader;

import javax.xml.namespace.QName;

/**
 */
public class WsrfReceiver extends AbstractInOutSyncMessageReceiver
        implements MessageReceiver {

    protected static Log faultLog = LogFactory.getLog("DISPATCH-FAULTS");

    /**
     * flag which indicates that an address is mandatory
     */
    private static boolean addressingMandatory = false;

    public static boolean isAddressingMandatory() {
        return addressingMandatory;
    }

    public static void setAddressingMandatory(boolean addressingMandatory) {
        WsrfReceiver.addressingMandatory = addressingMandatory;
    }

    public void invokeBusinessLogic(MessageContext inMessage, MessageContext outMessage) throws AxisFault {

        try {
            // get the implementation class for the Web Service
            Object destObject = getTheImplementationObject(inMessage);

            //Inject the Message Context if it is asked for
            DependencyManager.configureBusinussLogicProvider(destObject, inMessage);

            MessageInformationHeaders addressInfo = inMessage.getMessageInformationHeaders();
            if (isAddressingMandatory() && addressInfo != null) {
                throw new BaseException("WS-Addressing is mandatory on WSRF resources");
            }

            //get the operation
            OperationDescription opDesc = inMessage.getOperationContext()
                    .getAxisOperation();
            QName operation = opDesc.getName();
            String style = inMessage.getOperationContext()
                    .getAxisOperation()
                    .getStyle();
            if (!WSDLService.STYLE_DOC.equals(style)) {
                throw new AxisFault(org.smartfrog.services.deployapi.system.Constants.ERROR_NOT_DOCLIT);
            }

            SOAPEnvelope envelope = null;
            OMElement result = null;
            XmlBeansEndpoint endpoint = (XmlBeansEndpoint) destObject;
            result = endpoint.dispatch(operation, inMessage);
            envelope = getSOAPFactory().getDefaultEnvelope();
            if (result != null) {
                envelope.getBody().addChild(result);
            }
            outMessage.setEnvelope(envelope);
        } catch (BaseException e) {
            faultLog.error("BaseException ",e);
            throw e.makeAxisFault();
        } catch (AxisFault e) {
            faultLog.error("AxisFault", e);
            throw e;
        } catch (Exception e) {
            faultLog.error("Exception", e);
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * Method getTheImplementationObject
     *
     * @param msgContext
     * @return
     * @throws AxisFault
     */
/*
    protected Object getTheImplementationObject(MessageContext msgContext)
            throws AxisFault {
        ServiceDescription service =
                msgContext
                        .getOperationContext()
                        .getServiceContext()
                        .getServiceConfig();
        return makeNewServiceObject(msgContext);
    }
*/

    /**
     * Method makeNewServiceObject
     * This is an override of the normal one, as we need to use a different classloader
     *
     * @param msgContext
     * @return the service object
     * @throws AxisFault
     */
    @Override
    protected Object makeNewServiceObject(MessageContext msgContext)
            throws AxisFault {

        String nsURI = msgContext.getEnvelope().getNamespace().getName();
        if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(nsURI)) {
            fac = OMAbstractFactory.getSOAP12Factory();
        } else if (
                SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(nsURI)) {
            fac = OMAbstractFactory.getSOAP11Factory();
        } else {
            throw new AxisFault(Messages.getMessage("invalidSOAPversion"));
        }
        return loadImplementationClassViaSmartFrog(msgContext);
    }

    private Object loadImplementationClassViaClassloader(MessageContext msgContext) throws AxisFault {
        try {

            ServiceDescription service =
                    msgContext
                            .getOperationContext()
                            .getServiceContext()
                            .getServiceConfig();
            //this is the override: we use the same classloader that loads this receiver.
            ClassLoader classLoader;
            ClassLoader axis2classLoader = service.getClassLoader();
            classLoader = this.getClass().getClassLoader();
            Parameter implInfoParam = service.getParameter(SERVICE_CLASS);
            if (implInfoParam != null) {
                Class implClass =
                        Class.forName(
                                (String) implInfoParam.getValue(),
                                true,
                                classLoader);
                return implClass.newInstance();
            } else {
                throw new AxisFault(
                        Messages.getMessage(
                                "paramIsNotSpecified",
                                "SERVICE_CLASS"));
            }

        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * Load an impl class via smartfrog
     *
     * @param msgContext
     * @return
     * @throws AxisFault
     */
    private Object loadImplementationClassViaSmartFrog(MessageContext msgContext) throws AxisFault {
        try {

            ServiceDescription service =
                    msgContext
                            .getOperationContext()
                            .getServiceContext()
                            .getServiceConfig();
            Parameter implInfoParam = service.getParameter(SERVICE_CLASS);

            if (implInfoParam == null) {
                throw new AxisFault(
                        Messages.getMessage(
                                "paramIsNotSpecified",
                                "SERVICE_CLASS"));
            }
            String classname = (String) implInfoParam.getValue();
            classname = classname.trim();
            Class implClass =
                    SFClassLoader.forName(classname, null, true);
            return implClass.newInstance();

        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

}
