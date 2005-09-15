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

import org.apache.axis2.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.xmlbeans.XmlException;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.DeploymentException;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfEndpoint;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.smartfrog.services.deployapi.system.Constants;
import org.ggf.xbeans.cddlm.api.CreateRequestDocument;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.EndpointReferenceType;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.AttributedURI;

import javax.xml.namespace.QName;

/**
 */
public class PortalEndpoint extends WsrfEndpoint {

    /**
     * deliver a message
     *
     * @param operation
     * @param inMessage
     * @return the body of the response
     * @throws AxisFault
     * @throws BaseException unchecked basefault
     */
    public OMElement dispatch(QName operation, MessageContext inMessage) throws AxisFault {
        OMElement result = super.dispatch(operation, inMessage);
        if(result!=null) {
            return result;
        }
        verifyDeployApiNamespace(operation);
        String action=operation.getLocalPart();
        OMElement request=inMessage.getEnvelope().getBody().getFirstElement();
        if("createRequest".equals(action)) {
            return Create(request);
        }
        return null;
    }

    public OMElement Create(OMElement request) throws AxisFault {
        Axis2Beans<CreateRequestDocument> create=new Axis2Beans<CreateRequestDocument>();
        CreateRequestDocument doc=create.convert(request);
        CreateRequestDocument.CreateRequest createRequest = doc.getCreateRequest();
        validate(createRequest);

        //hostname processing
        String hostname=org.smartfrog.services.deployapi.system.Constants.LOCALHOST;
        if(createRequest.isSetHostname()) {
            hostname=createRequest.getHostname();
        }
        if(hostname!=null) {
            throw new BaseException(Constants.ERROR_CREATE_UNSUPPORTED_HOST);
        }
        //TODO: create a new endpoint
        String endpoint="http://localhost:8080/services/System";
        EndpointReferenceType epr=EndpointReferenceType.Factory.newInstance();
        AttributedURI uri=AttributedURI.Factory.newInstance();
        epr.addNewAddress().setStringValue(endpoint);
        //XmlOptions options=new XmlOptions();
        return null;
    }

    public OMElement Resolve(OMElement request) throws AxisFault {
        return null;
    }

    public OMElement LookupSystem(OMElement request) throws AxisFault {
        return null;
    }
}
