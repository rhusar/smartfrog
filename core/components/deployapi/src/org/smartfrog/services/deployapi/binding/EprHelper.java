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

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.ServiceName;
import org.apache.axis2.addressing.AnyContentType;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.AttributedURI;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.EndpointReferenceType;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.ReferencePropertiesType;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.ServiceNameType;
import org.ggf.xbeans.cddlm.wsrf.wsa2004.AttributedQName;
import org.ggf.xbeans.cddlm.wsrf.wsa2004.ReferenceParametersType;

import javax.xml.namespace.QName;

/**
 * Helps conver EPRs
 * created 20-Sep-2005 17:41:13
 */

public class EprHelper {

    protected EprHelper() {
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
