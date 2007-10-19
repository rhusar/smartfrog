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

/**
 * created 21-Apr-2005 14:26:16
 */

public class Documentation extends DocNode {

    public Documentation(String name) {
        super(name);
    }

    public Documentation(String name, String uri) {
        super(name, uri);
    }

    public Documentation(Element element) {
        super(element);
    }


    protected Element shallowCopy() {
        return new Documentation(getQualifiedName(), getNamespaceURI());
    }



    /**
     * Test that a (namespace,localname) pair matches our type
     *
     * @param namespace
     * @param localname
     *
     * @return true for a match
     */
    public static boolean isA(String namespace, String localname) {
        return isNode(namespace, localname, ELEMENT_DOCUMENTATION);
    }



}
