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

import java.util.Locale;

/**
 * This represents headers
 *
 * The element MAY be present in a SOAP message. If present, the element MUST be the first immediate child element of a
 * SOAP Envelope element. The element MAY contain a set of header entries each being an immediate child element of the
 * SOAP Header element. All immediate child elements of the SOAP Header element MUST be namespace-qualified.
 */
public class Header extends Soap11Element {


    public Header() {
        super(QNAME_HEADER);
    }

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
     * add or replace an element as a header
     * @param element element to append
     * @param mustUnderstand whether this header must be understood or not
     */
    public void setHeaderElement(Element element, boolean mustUnderstand) {
        setMustUnderstand(element, getNamespaceURI(), mustUnderstand);
        addOrReplaceChild(element);
    }
    /**
     * duplicate ourselves
     *
     * @return a copy of ourselves
     */
    protected Element shallowCopy() {
        return new Header(getQualifiedName(), getNamespaceURI());
    }
    /**
     * query the mustUnderstand attribute
     * @param that the header that is being compared
     * @param soapNamespace
     * @return true if it exists and is "1", false if it is absent or "0"
     * @throws InvalidXmlException if it has any other value
     */
    public static boolean isMustUnderstand(Element that, String soapNamespace) {
        Attribute attribute = that.getAttribute(ATTR_MUST_UNDERSTAND,
                soapNamespace);
        if (attribute == null) {
            return false;
        }
        String value = attribute.getValue().toLowerCase(Locale.ENGLISH);
        if ("1".equals(value) || "true".equals(value)) {
            return true;
        }
        if ("0".equals(value) || "false".equals(value)) {
            return false;
        }
        throw new InvalidXmlException(FAULTCODE_MUST_UNDERSTAND);
    }

    /**
     * remove any existing mustUnderstand header, and set a new one to either true or false
     * @param soapNamespace
     * @param understand should we understand or not?
     */
    public static void setMustUnderstand(Element that,
                                         String soapNamespace,
                                         boolean understand) {
        Attribute attribute = that.getAttribute(ATTR_MUST_UNDERSTAND,
                soapNamespace);
        if (attribute != null) {
            that.removeAttribute(attribute);
        }
        Attribute mu=new Attribute(PREFIX_SOAP +":"+ATTR_MUST_UNDERSTAND,
                soapNamespace,understand?"1":"0");
        that.addAttribute(mu);
    }

}
