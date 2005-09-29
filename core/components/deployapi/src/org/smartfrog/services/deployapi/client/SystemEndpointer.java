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

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.OperationDescription;
import org.apache.axis2.description.ServiceDescription;
import org.ggf.xbeans.cddlm.api.CreateResponseDocument;
import org.smartfrog.services.deployapi.binding.EprHelper;
import org.smartfrog.services.deployapi.system.Constants;

import javax.xml.namespace.QName;
import java.net.URL;

/**
 * Model for a remote system.
 * Needs a resourceID for hashCode and equals to work, so cannot be inserted into collections until then.
 * created 21-Sep-2005 12:55:10
 */

public class SystemEndpointer extends Endpointer {

    private String resourceID;
    protected static org.apache.axis2.description.OperationDescription[] operations;
    protected static ServiceDescription serviceDescription;


    //From Axis2 generated SystemEPRStub
    static {

        //creating the Service
        serviceDescription = new ServiceDescription(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "SystemEPR"));

        //creating the operations
        OperationDescription __operation;
        operations = new OperationDescription[11];

        __operation = new OperationDescription();
        __operation.setName(new QName(Constants.CDL_API_WSDL_NAMESPACE,
                "GetMultipleResourceProperties"));
        operations[0] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "AddFile"));
        operations[1] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "Ping"));
        operations[2] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(new QName(Constants.CDL_API_WSDL_NAMESPACE,
                "GetCurrentMessage"));
        operations[3] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "Resolve"));
        operations[4] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "Subscribe"));
        operations[5] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation
                .setName(new QName(Constants.CDL_API_WSDL_NAMESPACE, "Run"));
        operations[6] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "Destroy"));
        operations[7] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(new QName(Constants.CDL_API_WSDL_NAMESPACE,
                "GetResourceProperty"));
        operations[8] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "Terminate"));
        operations[9] = __operation;
        serviceDescription.addOperation(__operation);

        __operation = new OperationDescription();
        __operation.setName(
                new QName(Constants.CDL_API_WSDL_NAMESPACE, "Initialize"));
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
        this.resourceID = resourceID;
        init();
    }

    public SystemEndpointer(CreateResponseDocument.CreateResponse response)
            throws AxisFault {
        resourceID = response.getResourceId();
        init();
        bindToEndpointer(EprHelper.Wsa2003ToEPR(response.getSystemReference()));
    }

    public SystemEndpointer(String url) throws AxisFault {
        super(url);
        init();
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public ServiceDescription getServiceDescription() {
        return serviceDescription;
    }

    /**
     * we use resourceID for equality, not the url itself
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SystemEndpointer that = (SystemEndpointer) o;

        if (resourceID != null ? !resourceID.equals(that.resourceID) : that.resourceID != null) return false;

        return true;
    }

    /**
     * hash is based on resourceID
     *
     * @return
     */
    public int hashCode() {
        return (resourceID != null ? resourceID.hashCode() : 0);
    }

    /**
     * String info
     *
     * @return
     */
    public String toString() {
        return "System ID=" + resourceID + " URL=" + url.toString();
    }
}
