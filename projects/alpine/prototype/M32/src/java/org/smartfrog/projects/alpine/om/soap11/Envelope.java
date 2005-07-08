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
import nu.xom.Elements;
import org.smartfrog.projects.alpine.xmlutils.BaseElementsIterator;

/**
 * The element name is "Envelope".
 * The element MUST be present in a SOAP message
 * The element MAY contain namespace declarations as well as additional attributes. 
 * If present, such additional attributes MUST be namespace-qualified. Similarly, 
 * the element MAY contain additional sub elements. 
 * If present these elements MUST be namespace-qualified and MUST follow the SOAP Body element.
 */
public class Envelope extends Soap11Element {
    
    public Envelope() {
        super(QNAME_ENVELOPE);
    }

    public Envelope(String name) {
        super(name);
    }

    public Envelope(String name, String uri) {
        super(name, uri);
    }

    public Envelope(Element element) {
        super(element);
    }
    
    /**
     * duplicate ourselves
     *
     * @return a copy of ourselves
     */
    protected Element shallowCopy() {
        return new Envelope(getQualifiedName(), getNamespaceURI());
    }
    
    /**
     * Get the body
     * @return the body or null
     */ 
    public Body getBody() {
        Element child = getFirstChildElement(QNAME_BODY);
        return (Body) child;
    }

    /**
     * Get an iterator over the headers
     *
     * @return the body or null
     */
    public BaseElementsIterator<Header> getHeaders() {
        Elements childElements = getChildElements(QNAME_HEADER);
        return new BaseElementsIterator<Header>(childElements);
    }
    
}
