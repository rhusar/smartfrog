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
import org.smartfrog.services.xml.utils.XsdUtils;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

/**
 * created 26-May-2005 13:33:33
 */

public class LazyAttribute extends GenericAttribute {
    public static final String ERROR_NOT_BOOLEAN = "Not an XML Schema boolean value: ";


    public LazyAttribute() {
    }

    public LazyAttribute(Attribute attribute) throws CdlXmlParsingException {
        super(attribute);
    }

    public boolean isTrue() {
        return XsdUtils.isXsdBooleanTrue(getValue());
    }

    public boolean isFalse() {
        return XsdUtils.isXsdBooleanTrue(getValue());
    }


    /**
     * Extract a lazy attribute from an element.
     *
     * @param element  element to extract from
     * @param required flag to set to true if the attribute is required
     * @return
     */
    public static LazyAttribute extract(Element element, boolean required)
            throws CdlXmlParsingException {
        return findAndBind(ATTR_LAZY,
                LazyAttribute.class,
                element,
                required, false);
    }

    public static boolean isLazy(Element element, boolean required)
            throws CdlXmlParsingException {
        LazyAttribute lazy = extract(element, required);
        if (lazy == null || lazy.isFalse()) {
            return false;
        }

        if (lazy.isTrue()) {
            return true;
        }
        //if we get here: neither true nor false: error
        throw new CdlXmlParsingException(ERROR_NOT_BOOLEAN + lazy.getValue());
    }

}
