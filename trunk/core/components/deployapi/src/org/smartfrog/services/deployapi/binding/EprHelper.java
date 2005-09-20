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
import org.ggf.xbeans.cddlm.wsrf.wsa2003.EndpointReferenceType;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.AttributedURI;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.ReferencePropertiesType;

/**
 * Helps conver EPRs
 * created 20-Sep-2005 17:41:13
 */

public class EprHelper {

    protected EprHelper() {
    }

    public static EndpointReference Wsa2003ToEPR(EndpointReferenceType source) {
        AttributedURI addrURI = source.getAddress();
        EndpointReference dest=new EndpointReference(addrURI.getStringValue());
        ReferencePropertiesType props = source.getReferenceProperties();
        //TODO: reference types
        return dest;
    }

    public static EndpointReference Wsa2004ToEPR(org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType source) {
        org.ggf.xbeans.cddlm.wsrf.wsa2004.AttributedURI addrURI = source.getAddress();
        EndpointReference dest = new EndpointReference(addrURI.getStringValue());
        org.ggf.xbeans.cddlm.wsrf.wsa2004.ReferencePropertiesType props = source.getReferenceProperties();
        //TODO: reference types
        return dest;
    }

    public static EndpointReferenceType EPRToWsa2003(EndpointReference source) {
        EndpointReferenceType dest= EndpointReferenceType.Factory.newInstance();
        dest.addNewAddress().setStringValue(source.getAddress());
        //TODO: reference types
        return dest;
    }

    public static org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType EPRToWsa2004(EndpointReference source) {
        org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType dest =
                org.ggf.xbeans.cddlm.wsrf.wsa2004.EndpointReferenceType.Factory.newInstance();
        dest.addNewAddress().setStringValue(source.getAddress());
        //TODO: reference types
        return dest;
    }

}
