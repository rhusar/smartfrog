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
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

/**
 * this is only used inside an {@link org.smartfrog.sfcore.languages.cdl.dom.Expression}
 * created 26-May-2005 11:19:00
 */

public class ValueOfAttribute extends GenericAttribute {
    public ValueOfAttribute() {
    }

    public ValueOfAttribute(Attribute attribute) throws CdlXmlParsingException {
        super(attribute);
    }

    /**
     * Extract an attribute from an element.
     *
     * @param element  element to extract from
     * @param required flag to set to true if the attribute is required
     * @return
     */
    public static ValueOfAttribute extract(Element element, boolean required)
            throws CdlXmlParsingException {
        return findAndBind(ATTR_VALUE_OF,
                ValueOfAttribute.class,
                element,
                required, true);
    }
}
