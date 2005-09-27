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
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;

import nu.xom.Element;
import org.smartfrog.services.deployapi.system.Constants;


/**
 * Helps conver EPRs
 * created 20-Sep-2005 17:41:13
 */

public class EprHelper {
    public static final String WSA = Constants.WS_ADDRESSING_NAMESPACE;
    public static final String WSA2004 = Constants.WS_ADDRESSING_2004_NAMESPACE;

    protected EprHelper() {
    }

    /**
     * Convert Xom to a new EPR
     *
     * @param addr
     * @return
     */
    public static EndpointReference XomWsa2003ToEpr(Element addr) {
        String wsa = WSA;
        String uri=addr.getFirstChildElement(Constants.WSA_ELEMENT_ADDRESS,wsa
                ).getValue();
        EndpointReference dest = new EndpointReference(uri);
/*
        Element serviceName = addr.getFirstChildElement(
                wsa,"ServiceName");
        if(serviceName!=null) {
            String portType=serviceName.getAttributeValue(WSA_ATTR_PORTNAME,wsa);
            QName qname=new QName(serviceName.getValue();
            dest.setServiceName(new ServiceName());

        }
*/
        return dest;
    }

    public static EndpointReference XomWsa2004ToEpr(Element element) {
        throw FaultRaiser.raiseNotImplementedFault("wsa2004");
    }

    public static EndpointReference XomWsaToEpr(Element addr) {
        String ns = addr.getNamespaceURI();
        if (WSA.equals(ns)) {
            return XomWsa2003ToEpr(addr);
        } else {
            if (WSA2004
                    .equals(ns)) {
                return XomWsa2004ToEpr(addr);
            }
        }
        throw FaultRaiser.raiseBadArgumentFault("Unknown namespace "+ns);
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

    /**
     * Turn an endpointer into a readable string
     * @param epr
     * @return printable description
     */
    public static String stringify(EndpointReference epr) {
        return epr.getAddress();
    }

    /**
     * compare two endpoints for equality
     * @param e1
     * @param e2
     * @return
     */
    public static boolean compareEndpoints(EndpointReference e1, EndpointReference e2) {
        return e1.getAddress().equals(e2.getAddress()); 
    }
}


