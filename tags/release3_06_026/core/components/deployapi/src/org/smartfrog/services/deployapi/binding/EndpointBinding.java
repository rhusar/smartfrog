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
package org.smartfrog.services.deployapi.binding;

import org.apache.axis2.AxisFault;
import org.apache.axis2.om.OMElement;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.smartfrog.services.deployapi.client.ApiCall;
import org.smartfrog.services.deployapi.client.Endpointer;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;

/**
 * One stop class to create both the in and out bindings for an endpoint.
 * created 20-Sep-2005 17:09:45
 */

public abstract class EndpointBinding<Tin extends XmlObject,Tout extends XmlObject> {

    private Axis2Beans<Tin> in;
    private Axis2Beans<Tout> out;

    private QName operation;


    public EndpointBinding(XmlOptions inOptions, XmlOptions outOptions) {
        in = new Axis2Beans<Tin>(inOptions);
        out = new Axis2Beans<Tout>(outOptions);
    }

    public EndpointBinding() {
        this(null, null);
    }

    public QName getOperation() {
        return operation;
    }

    public void setOperation(QName operation) {
        this.operation = operation;
    }

    public void setOperation(String namespace, String localpart) {
        this.operation = new QName(namespace, localpart);
    }

    public Axis2Beans<Tin> getRequestBinding() {
        return in;
    }

    public Axis2Beans<Tout> getResponseBinding() {
        return out;
    }

    public Tin convertRequest(OMElement element) {
        return in.convert(element);
    }

    public OMElement convertRequest(Tin data) {
        return in.convert(data);
    }

    public Tout convertResponse(OMElement element) {
        return out.convert(element);
    }

    public OMElement convertResponse(Tout data) {
        return out.convert(data);
    }

    /**
     * create a request object
     *
     * @return a request
     */
    public abstract Tin createRequest();

    /**
     * create a request object
     *
     * @return a response
     */
    public abstract Tout createResponse();

    /**
     * Invoke the call in a blocking operation with our payload
     *
     * @param call
     * @param operation
     * @param data
     * @return the response
     * @throws AxisFault
     */
    public Tout invokeBlocking(ApiCall call, String operation, Tin data) throws AxisFault {
        final OMElement toSend = convertRequest(data);
        OMElement omElement = call.invokeBlocking(operation, toSend);
        return convertResponse(omElement);
    }
    

    /**
     * Invoke the call in a blocking operation with our payload
     *
     * @param endpointer
     * @param data
     * @return the response
     * @throws AxisFault
     */
    public Tout invokeBlocking(Endpointer endpointer, String operation, Tin data) throws RemoteException {
        ApiCall call = endpointer.createStub(operation);
        if(call.lookupOperation(operation)==null) {
            throw new BaseException("No operation "+operation+" on endpointer "+endpointer);
        }
        return invokeBlocking(call, operation, data);
    }

    public XmlOptions getInOptions() {
        return in.getOptions();
    }

    public XmlOptions getOutOptions() {
        return out.getOptions();
    }

}
