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
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.AxisFault;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfEndpoint;
import org.smartfrog.services.deployapi.transport.endpoints.system.InitializeProcessor;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.smartfrog.services.deployapi.engine.Job;
import org.ggf.xbeans.cddlm.api.InitializeRequestDocument;
import org.ggf.xbeans.cddlm.api.InitializeResponseDocument;

import javax.xml.namespace.QName;

/*
* System EPR
 */

public class SystemEndpoint extends WsrfEndpoint {


    /**
     * deliver a message
     *
     * @param operation
     * @param inMessage
     * @return the body of the response
     * @throws org.apache.axis2.AxisFault
     * @throws BaseException              unchecked basefault
     */
    public OMElement dispatch(QName operation, MessageContext inMessage) throws AxisFault {
        OMElement result = super.dispatch(operation, inMessage);
        if (result != null) {
            return result;
        }
        verifyDeployApiNamespace(operation);
        Job job=lookupJob(inMessage);
        String action = operation.getLocalPart();
        OMElement request = inMessage.getEnvelope().getBody().getFirstElement();
        if (Constants.API_ELEMENT_INITALIZE_REQUEST.equals(action)) {
            return Initialize(job,request);
        }
        if (Constants.API_ELEMENT_RESOLVE_REQUEST.equals(action)) {
            return Resolve(request);
        }
        if (Constants.API_ELEMENT_ADDFILE_REQUEST.equals(action)) {
            return AddFile(request);
        }
        if (Constants.API_ELEMENT_RUN_REQUEST.equals(action)) {
            return Run(request);
        }
        if (Constants.API_ELEMENT_TERMINATE_REQUEST.equals(action)) {
            return Terminate(request);
        }
        if (Constants.API_ELEMENT_PING_REQUEST.equals(action)) {
            return Ping(request);
        }
        return null;
    }

    protected Job lookupJob(MessageContext inMessage) {
        FaultRaiser.throwNotImplemented();
        return null;
    }

    public OMElement AddFile(OMElement request) throws BaseException {
        FaultRaiser.throwNotImplemented();
        return null;
    }

    public OMElement Initialize(Job job, OMElement request) throws BaseException {
        Axis2Beans<InitializeRequestDocument> inBinding = new Axis2Beans<InitializeRequestDocument>();
        InitializeRequestDocument doc = inBinding.convert(request);
        Utils.maybeValidate(doc);
        InitializeProcessor processor=new InitializeProcessor(this);
        InitializeResponseDocument responseDoc;
        responseDoc = processor.initialize(job, doc.getInitializeRequest());
        Axis2Beans<InitializeResponseDocument> outBinding = new Axis2Beans<InitializeResponseDocument>();
        OMElement responseOM = outBinding.convert(responseDoc);
        return responseOM;
    }

    public OMElement Resolve(OMElement request) throws BaseException {
        FaultRaiser.throwNotImplemented();
        return null;
    }

    public OMElement Run(OMElement request) throws BaseException {
        FaultRaiser.throwNotImplemented();
        return null;
    }

    public OMElement Terminate(OMElement request) throws BaseException {
        FaultRaiser.throwNotImplemented();
        return null;
    }

    public OMElement Ping(OMElement request) throws BaseException {
        FaultRaiser.throwNotImplemented();
        return null;
    }

    public OMElement Destroy(OMElement request) throws BaseException {
        FaultRaiser.throwNotImplemented();
        return null;
    }

}
