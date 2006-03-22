/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.wsa;

import nu.xom.Element;
import org.smartfrog.projects.alpine.om.soap11.Envelope;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.Header;

/*
<wsa:EndpointReference
 xmlns:wsa="http://www.w3.org/2005/08/addressing"
 xmlns:wsaw="http://www.w3.org/2005/03/addressing/wsdl"
 xmlns:fabrikam="http://example.com/fabrikam"
 xmlns:wsdli="http://www.w3.org/2005/08/wsdl-instance"
 wsdli:wsdlLocation="http://example.com/fabrikam
 http://example.com/fabrikam/fabrikam.wsdl">
<wsa:Address>http://example.com/fabrikam/acct</wsa:Address>
<wsa:Metadata>
<wsaw:InterfaceName>fabrikam:Inventory</wsaw:InterfaceName>
</wsa:Metadata>
<wsa:ReferenceParameters>
<fabrikam:CustomerKey>123456789</fabrikam:CustomerKey>
<fabrikam:ShoppingCart>ABCDEFG</fabrikam:ShoppingCart>
</wsa:ReferenceParameters>
</wsa:EndpointReference>
*/

/**
 * Alpine model of an EndpointReference
 * created 22-Mar-2006 14:56:06
 * <code>
 * @see <a href="http://www.w3.org/TR/2005/CR-ws-addr-soap-20050817/">WS-A specification</a>
 </code>
 */

public class AlpineEPR {


    public static final String ELEMENT_TO = "To";

    public static final String ELEMENT_ACTION = "Action";

    public static final String ELEMENT_ADDRESS = "Address";

    public static final String ELEMENT_METADATA = "Metadata";

    public static final String ELEMENT_REFERENCE_PARAMETERS = "ReferenceParameters";

    public static final String ATTR_IS_REFERENCE_PARAMETERE = "IsReferenceParameter";

    
    private String address;

    private Element Metadata;

    private Element ReferenceParameters;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Element getMetadata() {
        return Metadata;
    }

    public void setMetadata(Element metadata) {
        Metadata = metadata;
    }

    public Element getReferenceParameters() {
        return ReferenceParameters;
    }

    public void setReferenceParameters(Element referenceParameters) {
        ReferenceParameters = referenceParameters;
    }


    public void addToSoapMessage(MessageDocument message,String namespace,boolean markReferences) {
        Envelope env=message.getEnvelope();
        Header header=env.getHeader();

    }
}
