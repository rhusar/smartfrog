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
package org.smartfrog.sfcore.languages.cdl.dom;

import org.smartfrog.sfcore.languages.cdl.CdlParsingException;
import org.ggf.cddlm.generated.api.CddlmConstants;
import nu.xom.Node;
import nu.xom.Element;
import nu.xom.Attribute;

/**
 * created 21-Apr-2005 14:25:53
 */

public class DocNode implements FromXML, Names {

    /**
     * Parse from XM
     * @throws CdlParsingException
     */
    public void fromXML(Element element) throws CdlParsingException {

    }

    /**
     * Test for an element being in the namespace
     * @param e
     * @return true iff we are in the CDL namespace
     * @see #DOC_NAMESPACE
     */
    public static boolean inCdlNamespace(Element e) {
        return DOC_NAMESPACE.equals(e.getNamespaceURI());
    }

    /**
     * Test for an attribute being in the namespace
     *
     * @param a
     * @return true iff we are in the CDL namespace
     * @see #DOC_NAMESPACE
     */
    public static boolean inCdlNamespace(Attribute a) {
        return DOC_NAMESPACE.equals(a.getNamespaceURI());
    }

    public static boolean isNode(Element e,String name) {
        return inCdlNamespace(e) && name.equals(e.getLocalName());
    }

    /**
     * Get an attribute in the CDL namespace
     * @param element node to examine
     * @param attribute attribute to get
     * @param required flag set to true if needed
     * @return the string value of the attribute
     * @throws CdlParsingException
     */
    protected String getCdlAttribute(Element element, String attribute,boolean required)
        throws CdlParsingException {
        String value = element.getAttribute(attribute,DOC_NAMESPACE).getValue();
        if(value==null && required) {
            throw new CdlParsingException("Missing attribute "+attribute+" from element "+element); 
        }
        return value;
    }
}
