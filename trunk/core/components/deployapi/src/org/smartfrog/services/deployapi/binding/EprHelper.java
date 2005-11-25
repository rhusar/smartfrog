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

import nu.xom.Element;
import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.ServiceName;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;


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


    /**
     * address-only mapper
     * @param epr
     * @param uri
     * @return a WSA address in the 2004 namespace
     */
    public static Element makeAddress2004(EndpointReference epr, String uri) {
        return makeAddress(epr, Constants.WS_ADDRESSING_2004_NAMESPACE);
    }

    /**
     * address-only mapper
     *
     * @param epr
     * @param uri
     * @return a WSA address in the 2004 namespace
     */
    public static Element makeAddress2003(EndpointReference epr, String uri) {
        return makeAddress(epr, Constants.WS_ADDRESSING_NAMESPACE);
    }

    /**
        * address-only mapper
        * @param epr
        * @param uri
        * @return a WSA address in the relevant namespace
        */
    public static Element makeAddress(EndpointReference epr,String uri) {
        String url = epr.getAddress();
        return makeAddress(url, uri);
    }

    public static Element makeAddress(String url, String namespace) {
        Element endpoint =new Element("EndpointReference",namespace);
        Element address =new Element(Constants.WSA_ELEMENT_ADDRESS,namespace);
        address.appendChild(url);
        endpoint.appendChild(address);
        return endpoint;
    }


}


