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
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.description.OperationDescription;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.engine.DependencyManager;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;
import org.apache.wsdl.WSDLService;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import javax.xml.namespace.QName;

/**
 */
public class  WsrfReceiver extends AbstractInOutSyncMessageReceiver
        implements MessageReceiver {

    /**
     * flag which indicates that an address is mandatory
     */
    private static boolean addressingMandatory=false;

    public static boolean isAddressingMandatory() {
        return addressingMandatory;
    }

    public static void setAddressingMandatory(boolean addressingMandatory) {
        WsrfReceiver.addressingMandatory = addressingMandatory;
    }

    public void invokeBusinessLogic(MessageContext inMessage, MessageContext outMessage) throws AxisFault {
        // get the implementation class for the Web Service
        Object destObject = getTheImplementationObject(inMessage);


        //Inject the Message Context if it is asked for
        DependencyManager.configureBusinussLogicProvider(destObject, inMessage);

        MessageInformationHeaders addressInfo = inMessage.getMessageInformationHeaders();
        if(isAddressingMandatory() && addressInfo!=null) {
            throw new BaseException("WS-Addressing is mandatory on WSRF resources");
        }

        //get the operation
        OperationDescription opDesc = inMessage.getOperationContext()
                .getAxisOperation();
        QName operation=opDesc.getName();
        String style = inMessage.getOperationContext()
                .getAxisOperation()
                .getStyle();
        if (!WSDLService.STYLE_DOC.equals(style)) {
            throw new AxisFault(org.smartfrog.services.deployapi.system.Constants.ERROR_NOT_DOCLIT);
        }

        SOAPEnvelope envelope = null;
        try {
            OMElement result = null;
            XmlBeansEndpoint endpoint=(XmlBeansEndpoint) destObject;
            result=endpoint.dispatch(operation, inMessage);
            envelope = getSOAPFactory().getDefaultEnvelope();
            if (result != null) {
                envelope.getBody().addChild(result);
            }
        } catch (BaseException e) {
            throw e.makeAxisFault();

        }
        outMessage.setEnvelope(envelope);

    }

}
