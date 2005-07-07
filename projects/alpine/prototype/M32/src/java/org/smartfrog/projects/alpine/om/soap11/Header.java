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

import nu.xom.Attribute;
import nu.xom.Element;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;

/**
 * The element MAY be present in a SOAP message. If present, the element MUST be the first immediate child element of a
 * SOAP Envelope element. The element MAY contain a set of header entries each being an immediate child element of the
 * SOAP Header element. All immediate child elements of the SOAP Header element MUST be namespace-qualified.
 */
public class Header extends Soap11Element {

    public Header(String name) {
        super(name);
    }

    public Header(String name, String uri) {
        super(name, uri);
    }

    public Header(Element element) {
        super(element);
    }

    /**
     * query the mustUnderstand attribute
     *
     * @return true if it exists and is "1", false if it is absent or "0"
     * @throws InvalidXmlException if it has any other value
     */
    public boolean isMustUnderstand() {
        Attribute attribute = getAttribute(ATTR_MUST_UNDERSTAND, NAMESPACE_SOAP11);
        if (attribute == null) {
            return false;
        }
        if ("1".equals(attribute.getValue())) {
            return true;
        }
        if ("0".equals(attribute.getValue())) {
            return false;
        }
        throw new InvalidXmlException(FAULTCODE_MUST_UNDERSTAND);
    }

    /**
     * Validate the Xml. Throw {@link InvalidXmlException} if invalid.
     */
    public void validateXml() {
        isMustUnderstand();
    }
}
