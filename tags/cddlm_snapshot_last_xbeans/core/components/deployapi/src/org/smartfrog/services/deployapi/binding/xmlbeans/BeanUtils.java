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
package org.smartfrog.services.deployapi.binding.xmlbeans;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.ServiceName;
import org.apache.axis2.addressing.AnyContentType;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.w3c.dom.Node;

import java.util.ArrayList;

import nu.xom.Element;
import nu.xom.converters.DOMConverter;

/**
 * This is only for use with beans
 * created 25-Nov-2005 16:23:09
 */

public class BeanUtils {
    /**
     * We are invalid.
     *
     * @param message
     * @throws org.smartfrog.services.deployapi.transport.faults.DeploymentException
     *
     * @returns true for use in conditional code
     * @deprecated
     */
    public static boolean validate(XmlObject message) {
        ArrayList validationErrors = new ArrayList();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);
        if (!message.validate(validationOptions)) {
            message.dump();
            throw FaultRaiser.raiseBadArgumentFault("XML did not validate against the schema");
        }
        return true;
    }

    /**
     * validate documents iff assertions are enabled
     *
     * @param message
     * @deprecated
     */
    public static void maybeValidate(XmlObject message) {
        assert validate(message);
    }

    /**
     * Turn a bean (which must map to an element, unless you like runtime exceptions
     * @param bean to convert
     * @return the converted doc
     * @deprecated
     */
    public static Element beanToXom(XmlObject bean) {
        Node dom = bean.getDomNode();
        org.w3c.dom.Element elt=(org.w3c.dom.Element)dom;
        return DOMConverter.convert(elt);
    }

    public static EndpointReference Wsa2003ToEPR(EndpointReferenceType source) {
        AttributedURI addrURI = source.getAddress();
        EndpointReference dest = new EndpointReference(addrURI.getStringValue());
        if(source.isSetServiceName()) {
            ServiceNameType sourceServiceName = source.getServiceName();
            ServiceName destServiceName=new ServiceName(sourceServiceName.getQNameValue(),
                    sourceServiceName.getPortName());
            dest.setServiceName(destServiceName);
        }
        if(source.isSetReferenceProperties()) {
            ReferencePropertiesType props = source.getReferenceProperties();
            props.newCursor();
            dest.getReferenceParameters();
            AnyContentType content=new AnyContentType();

            //TODO: reference properties are not currently supported

        }
        return dest;
    }

    public static EndpointReferenceType EPRToWsa2003(EndpointReference source) {
        EndpointReferenceType dest = EndpointReferenceType.Factory.newInstance();
        dest.addNewAddress().setStringValue(source.getAddress());
        ServiceName serviceName = source.getServiceName();
        if(serviceName!=null) {
            ServiceNameType destServiceName = dest.addNewServiceName();
            destServiceName.setPortName(serviceName.getEndpointName());
            destServiceName.setQNameValue(serviceName.getName());
        }
        //TODO: reference properties are not currently supported
        return dest;
    }

    public static EndpointReference Wsa2004ToEPR(org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType source) {
        org.ggf.xbeans.cddlm.wsrf.wsa2004.AttributedURI addrURI = source.getAddress();
        EndpointReference dest = new EndpointReference(addrURI.getStringValue());
        org.ggf.xbeans.cddlm.wsrf.wsa2004.ReferencePropertiesType props = source.getReferenceProperties();
        //TODO: reference properties are not currently supported
        return dest;
    }

    public static org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType EPRToWsa2004(EndpointReference source) {
        org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType dest =
                org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType.Factory.newInstance();
        dest.addNewAddress().setStringValue(source.getAddress());
        //TODO: reference properties are not currently supported
        return dest;
    }

    /**
     * copy a source to a dest
     * @param source
     */
    public static void copyInto(org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType source,
                                EndpointReferenceType dest) {
        org.ggf.xbeans.cddlm.wsrf.wsa2004.AttributedURI address = source.getAddress();

        dest.addNewAddress().setStringValue(address.getStringValue());

        AttributedQName portType = source.getPortType();
        dest.addNewPortType().setQNameValue(portType.getQNameValue());
        org.ggf.xbeans.cddlm.wsrf.wsa2004.ServiceNameType serviceName = source.getServiceName();
        dest.addNewServiceName().setPortName(serviceName.getPortName());
        org.ggf.xbeans.cddlm.wsrf.wsa2004.ReferencePropertiesType sourceProperties = source.getReferenceProperties();
        ReferencePropertiesType destProperties = dest.addNewReferenceProperties();
        //TODO
    }
}
