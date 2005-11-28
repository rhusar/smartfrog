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
package org.smartfrog.services.deployapi.client;

import nu.xom.Document;
import nu.xom.Element;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.InOutAxisOperation;
import org.smartfrog.services.deployapi.binding.EprHelper;
import org.smartfrog.services.deployapi.binding.XomHelper;
import static org.smartfrog.services.deployapi.binding.XomHelper.apiElement;
import static org.smartfrog.services.deployapi.system.Constants.*;
import org.smartfrog.services.deployapi.system.LifecycleStateEnum;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * Model for a remote system. Needs a resourceID for hashCode and equals to
 * work, so cannot be inserted into collections until then. created 21-Sep-2005
 * 12:55:10
 */

public class SystemEndpointer extends Endpointer {

    private String cachedResourceId;
    protected static AxisOperation[] operations;
    protected static AxisService serviceDescription;


    //From Axis2 generated SystemEPRStub
    static {

        //creating the Service
        serviceDescription = new AxisService(
                new QName(CDL_API_WSDL_NAMESPACE, "SystemEPR"));

        //creating the operations
        AxisOperation __operation;
        operations = new AxisOperation[11];

        __operation = new InOutAxisOperation();
        __operation.setName(new QName(CDL_API_WSDL_NAMESPACE,
                WSRF_OPERATION_GETMULTIPLERESOURCEPROPERTIES));
        operations[0] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(CDL_API_WSDL_NAMESPACE, API_SYSTEM_OPERATION_ADDFILE));
        operations[1] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(CDL_API_WSDL_NAMESPACE, API_SYSTEM_OPERATION_PING));
        operations[2] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(new QName(CDL_API_WSDL_NAMESPACE,
                WSRF_OPERATION_GETCURRENTMESSAGE));
        operations[3] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(CDL_API_WSDL_NAMESPACE, API_SYSTEM_OPERATION_RESOLVE));
        operations[4] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(CDL_API_WSDL_NAMESPACE, WSRF_OPERATION_SUBSCRIBE));
        operations[5] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation
                .setName(new QName(CDL_API_WSDL_NAMESPACE, API_SYSTEM_OPERATION_RUN));
        operations[6] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(CDL_API_WSDL_NAMESPACE, WSRF_OPERATION_DESTROY));
        operations[7] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(new QName(CDL_API_WSDL_NAMESPACE,
                WSRF_OPERATION_GETRESOURCEPROPERTY));
        operations[8] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(CDL_API_WSDL_NAMESPACE, API_SYSTEM_OPERATION_TERMINATE));
        operations[9] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new InOutAxisOperation();
        __operation.setName(
                new QName(CDL_API_WSDL_NAMESPACE, API_SYSTEM_OPERATION_INITIALIZE));
        operations[10] = __operation;
        serviceDescription.addOperation(__operation);

    }

    public SystemEndpointer() {
    }

    public SystemEndpointer(URL url) throws AxisFault {
        super(url);
        init();
    }

    public SystemEndpointer(EndpointReference endpointer, String resourceID)
            throws AxisFault {
        super(endpointer);
        this.cachedResourceId = resourceID;
        init();
    }


    /**
     * construct from a createResponse
     * @param response
     * @throws AxisFault
     */
    public SystemEndpointer(Document response)
            throws AxisFault {
        final Element root = response.getRootElement();
        cachedResourceId = XomHelper.getElementValue(root,
                "api:ResourceId");
        init();
        Element address= XomHelper.getElement(root,
                "api:systemReference");
        bindToEndpointer(EprHelper.XomWsa2003ToEpr(address));
    }

    public SystemEndpointer(String url) throws AxisFault {
        super(url);
        init();
    }

    public String getCachedResourceId() {
        return cachedResourceId;
    }

    public void setCachedResourceId(String resourceID) {
        this.cachedResourceId = resourceID;
    }

    public AxisService getServiceDescription() {
        return serviceDescription;
    }

    /**
     * we use resourceID for equality, not the url itself
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SystemEndpointer that = (SystemEndpointer) o;

        if (cachedResourceId !=
                null ? !cachedResourceId.equals(that.cachedResourceId) : that
                .cachedResourceId !=
                null) {
            return false;
        }

        return true;
    }

    /**
     * hash is based on resourceID
     *
     * @return
     */
    public int hashCode() {
        return (cachedResourceId != null ? cachedResourceId.hashCode() : 0);
    }

    /**
     * String info
     *
     * @return
     */
    public String toString() {
        return "System ID=" + cachedResourceId + " URL=" + url.toString();
    }



    /**
     * make an init request
     *
     * @param request
     * @return
     * @throws java.io.IOException
     */
    public Document initialize(Element request) throws IOException {
        return invokeBlocking(API_SYSTEM_OPERATION_INITIALIZE,
                request);
    }

    /**
     * make an run request
     *
     * @return
     * @throws IOException
     */
    public Document run() throws IOException {
        Element request;
        request = apiElement(API_ELEMENT_RUN_REQUEST);

        Document document = invokeBlocking(API_SYSTEM_OPERATION_RUN,
                request);
        return document;
    }

    /**
     * terminate the app; it will still exist
     *
     * @param reason
     * @return
     * @throws IOException
     */
    public Document terminate(String reason) throws IOException {
        Element request;
        request = apiElement(API_ELEMENT_TERMINATE_REQUEST);
        if (reason != null) {
            Element er = apiElement("reason");
            er.appendChild(reason);
            request.appendChild(er);
        }
        return invokeBlocking(API_SYSTEM_OPERATION_TERMINATE,
                request);
    }

    /**
     * Ping the node.
     * @throws IOException
     * @return round trip time.
     */
    public long ping() throws
            IOException {
        Element request;
        request = apiElement(API_ELEMENT_PING_REQUEST);
        Date start, finish;
        start=new Date();
        invokeBlocking(API_SYSTEM_OPERATION_PING,
                request);
        finish = new Date();
        return finish.getTime()-start.getTime();
    }

    /**
     * destroy the app, will remove all trace of it
     *
     * @return
     * @throws IOException
     */
    public Document destroy() throws IOException {
        Element request;
        request = new Element(WSRF_ELEMENT_DESTROY_REQUEST,
                WSRF_WSRL_NAMESPACE);
        return invokeBlocking(WSRF_OPERATION_DESTROY,
                request);
    }

    public LifecycleStateEnum getLifecycleState() throws RemoteException {
        //TODO; extract more bits
        QName property = PROPERTY_SYSTEM_SYSTEM_STATE;
        String value = getStringProperty(property);
        LifecycleStateEnum state =
                LifecycleStateEnum.extract(value);
        return state;
    }


}
