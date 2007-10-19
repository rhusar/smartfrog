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
 * A node that has an (optional) documentation element created 21-Apr-2005
 * 14:55:02
 */

public class DocumentedNode extends DocNode {

    public DocumentedNode(String name) {
        super(name);
    }

    public DocumentedNode(String name, String uri) {
        super(name, uri);
    }

    public DocumentedNode(Element element) {
        super(element);
    }


    protected Element shallowCopy() {
        return new DocumentedNode(getQualifiedName(), getNamespaceURI());
    }
    
    /**
     * optional documentation attribute
     */
    private Documentation documentation;

    /**
     * Get the documentation
     *
     * @return documentation (may be null)
     */
    public Documentation getDocumentation() {
        return documentation;
    }

    /**
     * Set the documentation
     *
     * @param documentation (can be null)
     */
    public void setDocumentation(Documentation documentation) {
        this.documentation = documentation;
    }
}
