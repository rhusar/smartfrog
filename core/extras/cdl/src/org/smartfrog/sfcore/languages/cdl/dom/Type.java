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

import nu.xom.Element;
import org.smartfrog.sfcore.languages.cdl.CdlParsingException;

/**
 * Types contain XSD declarations (blech). Wouldnt it be nice if we could use other
 * schema types. 
 * created 21-Apr-2005 14:26:35
 */

public class Type extends DocumentedNode {

    public Type() {
    }

    public Type(Element node) throws CdlParsingException {
        bind(node);
    }

    /**
     * Parse from XM
     *
     * @throws CdlParsingException
     */
    public void bind(Element element) throws CdlParsingException {
        super.bind(element);
        //not implemented
        //TODO
    }

    /**
     * test that a node is of the right type
     *
     * @param element
     * @return true if the element namespace and localname match what we handle
     */
    static boolean isA(Element element) {
        return isNode(element, ELEMENT_TYPES);
    }

}
