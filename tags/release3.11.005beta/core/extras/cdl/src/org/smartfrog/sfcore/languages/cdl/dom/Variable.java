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
import org.smartfrog.sfcore.languages.cdl.dom.attributes.NameAttribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

/**
 * created 21-Apr-2005 14:43:52
 */

public class Variable extends Ref {

    public Variable(String name) {
        super(name);
    }

    public Variable(String name, String uri) {
        super(name, uri);
    }

    public Variable(Element element) {
        super(element);
    }

    public void bind() throws CdlXmlParsingException {
        //get the bits of ref
        super.bind();
        //add a name attribute
        getNameAttribute();
    }

    public NameAttribute getNameAttribute() {
        return NameAttribute.extract(this, true);
    }

    /**
     * get the string value of the name; valid only when bound
     *
     * @return
     */
    public String getNameValue() {
        return getNameAttribute().getValue();
    }

    public static boolean isA(String namespace,String localname) {
        return isNode(namespace,localname, ELEMENT_VARIABLE);
    }
}
