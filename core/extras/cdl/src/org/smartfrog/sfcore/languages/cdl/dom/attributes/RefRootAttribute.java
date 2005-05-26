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
package org.smartfrog.sfcore.languages.cdl.dom.attributes;

import nu.xom.Attribute;
import nu.xom.Element;
import org.smartfrog.sfcore.languages.cdl.CdlParsingException;
import org.smartfrog.sfcore.languages.cdl.dom.DocNode;

import javax.xml.namespace.QName;
import javax.xml.XMLConstants;

/**
 * created 26-May-2005 11:18:11
 */

public class RefRootAttribute extends QNameAttribute {

    public RefRootAttribute() {
    }

    /**
     * construct a refroot; crack open the value and build a qname from it.
     * @param attribute
     * @throws CdlParsingException
     */
    public RefRootAttribute(Attribute attribute) throws CdlParsingException {
        super(attribute);
    }

    /**
     * Extract an attribute from an element.
     *
     * @param element  element to extract from
     * @param required flag to set to true if the attribute is required
     * @return
     */
    public static RefRootAttribute extract(Element element, boolean required)
            throws CdlParsingException {
        return (RefRootAttribute) findAndBind(ATTR_REFROOT,
                RefRootAttribute.class,
                element,
                required);
    }

}
