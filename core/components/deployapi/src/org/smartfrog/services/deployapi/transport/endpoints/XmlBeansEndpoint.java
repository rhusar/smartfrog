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
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.om.OMElement;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ggf.xbeans.cddlm.cmp.DeploymentFaultType;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.DeploymentException;

import javax.xml.namespace.QName;
import java.util.ArrayList;

/**
 * An endpoint that is deployed under Smartfrog.
 */
public abstract class XmlBeansEndpoint {

    /**
     * deliver a message
     *
     * @param operation
     * @param inMessage
     * @return the body of the response
     * @throws AxisFault
     * @throws BaseException unchecked basefault
     */
    public abstract OMElement dispatch(QName operation, MessageContext inMessage) throws AxisFault;

    /**
     * check that the namespace is ok.
     * Special: Null namespaces are allowed on a get
     *
     * @param operation
     * @param expectedURI
     * @throws DeploymentException if things are bad
     */
    public void verifyNamespace(QName operation, String expectedURI) {
        String requestURI = operation.getNamespaceURI();
        if (!expectedURI.equals(requestURI)) {
            throw new DeploymentException(String.format(
                    "Wrong request namespace: expected " + expectedURI +
                            " - got " + requestURI));
        }
    }

    protected void verifyDeployApiNamespace(QName operation) {
        verifyNamespace(operation, Constants.CDL_API_TYPES_NAMESPACE);
    }

    /**
     * We are invalid.
     *
     * @param message
     * @throws DeploymentException
     * @returns true for use in conditional code
     */
    protected boolean validate(XmlObject message) {
        ArrayList validationErrors = new ArrayList();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);
        if (!message.validate(validationOptions)) {
            DeploymentFaultType fault = DeploymentFaultType.Factory.newInstance();
            //TODO
            throw new BaseException(Constants.BAD_ARGUMENT_ERROR_MESSAGE);
        }
        return true;
    }
}
