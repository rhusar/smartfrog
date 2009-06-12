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

package org.smartfrog.projects.alpine.om.soap11;

import nu.xom.Element;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;

/**
 * The element name is "Body".
 * 
 * The element MUST be present in a SOAP message and MUST be an immediate 
 * child element of a SOAP Envelope element. 
 * It MUST directly follow the SOAP Header element if present. 
 * Otherwise it MUST be the first immediate child element of the SOAP Envelope element.
 *
 * The element MAY contain a set of body entries each being an immediate 
 * child element of the SOAP Body element. 
 * Immediate child elements of the SOAP Body element MAY be namespace-qualified. 
 * SOAP defines the SOAP Fault element, which is used to indicate error messages 
 * 
 */
public class Body extends Soap11Element  {

    public Body() {
        super(QNAME_BODY);
    }

    public Body(String name) {
        super(name);
    }

    public Body(String name, String uri) {
        super(name, uri);
    }

    public Body(Element element) {
        super(element);
    }

    /**
     * duplicate ourselves
     * @return a copy of ourselves
     */ 
    protected Element shallowCopy() {
        return new Body(getQualifiedName(), getNamespaceURI());
    }
    
    /**
     * do we have a fault
     * @return true iff there is a fault child
     */ 
    public boolean isFault() {
        return getFirstChildElement(QNAME_FAULT)!=null;
    }

    /**
     * Validate the Xml. Throw {@link InvalidXmlException} if invalid.
     */
    public void validateXml() {
        super.validateXml();
    }
}
